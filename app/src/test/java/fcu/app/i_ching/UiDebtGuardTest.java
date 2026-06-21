package fcu.app.i_ching;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class UiDebtGuardTest {
    private static final List<String> SCAN_ROOTS = Arrays.asList(
            "app/src/main/java",
            "app/src/main/res/layout",
            "app/src/main/res/values"
    );
    private static final String BANNED_ICON_TEXT = "[☰⚙◎✦↺♡♥✓◯◌●]";

    @Test
    public void productionUiDoesNotUseIconLikeTextSymbols() throws Exception {
        StringBuilder failures = new StringBuilder();
        for (String rootPath : SCAN_ROOTS) {
            File root = repoFile(rootPath);
            scan(root, failures);
        }
        if (failures.length() > 0) {
            fail("Replace icon-like text symbols with vector drawables:\n" + failures);
        }
    }

    @Test
    public void productionUiUsesFontFamiliesInsteadOfRawFontFiles() throws Exception {
        StringBuilder failures = new StringBuilder();
        for (String rootPath : SCAN_ROOTS) {
            collectRawFontReferences(repoFile(rootPath), failures);
        }
        if (failures.length() > 0) {
            fail("Reference font-family XML resources from UI, not raw font files:\n" + failures);
        }
    }

    @Test
    public void buttonStylesAreMaterialButtons() throws Exception {
        StringBuilder failures = new StringBuilder();
        collectTextViewButtons(repoFile("app/src/main/res/layout"), failures);
        if (failures.length() > 0) {
            fail("Use MaterialButton for styled command controls:\n" + failures);
        }
    }

    private static void scan(File file, StringBuilder failures) throws Exception {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) return;
            for (File child : children) scan(child, failures);
            return;
        }
        String name = file.getName();
        if (!name.endsWith(".java") && !name.endsWith(".xml")) return;
        String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        if (text.matches("(?s).*" + BANNED_ICON_TEXT + ".*")) {
            failures.append(file.getPath()).append('\n');
        }
    }

    private static void collectRawFontReferences(File file, StringBuilder failures) throws Exception {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) return;
            for (File child : children) collectRawFontReferences(child, failures);
            return;
        }
        String name = file.getName();
        if (!name.endsWith(".java") && !name.endsWith(".xml")) return;
        String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        if (text.contains("@font/noto_sans_tc\"")
                || text.contains("@font/noto_serif_tc\"")
                || text.contains("@font/noto_serif_cjk_tc_regular")) {
            failures.append(file.getPath()).append('\n');
        }
    }

    private static void collectTextViewButtons(File file, StringBuilder failures) throws Exception {
        if (file == null || !file.exists()) return;
        if (file.isDirectory()) {
            File[] children = file.listFiles();
            if (children == null) return;
            for (File child : children) collectTextViewButtons(child, failures);
            return;
        }
        if (!file.getName().endsWith(".xml")) return;
        String text = Files.readString(file.toPath(), StandardCharsets.UTF_8);
        if (text.matches("(?s).*<TextView[^>]+style=\"@style/IChing(Primary|Secondary|Tertiary)Button\".*")) {
            failures.append(file.getPath()).append('\n');
        }
    }

    @Test
    public void sharedChromeAvoidsDeadMenuAndSelectedTabBackground() throws Exception {
        String topBarXml = read("app/src/main/res/layout/include_top_bar.xml");
        String bottomNavXml = read("app/src/main/res/layout/include_bottom_nav.xml");
        String chromeJava = read("app/src/main/java/fcu/app/i_ching/ui/NavigationChrome.java");

        assertFalse(topBarXml.contains("navigationIcon=\"@drawable/ic_menu_24\""));
        assertFalse(topBarXml.contains("navigationContentDescription=\"@string/nav_menu\""));
        assertFalse(chromeJava.contains("setNavigationIcon(R.drawable.ic_menu_24)"));
        assertFalse(bottomNavXml.contains("@drawable/bg_bottom_nav_item"));
        assertTrue(bottomNavXml.contains("itemActiveIndicatorStyle=\"@null\""));
        assertTrue(topBarXml.contains("@dimen/top_bar_height"));
        assertTrue(bottomNavXml.contains("@dimen/bottom_nav_height"));
        assertTrue(bottomNavXml.contains("labelVisibilityMode=\"labeled\""));
        assertTrue(bottomNavXml.contains("itemTextAppearanceActive=\"@style/TextAppearance.IChing.BottomNav\""));
        assertFalse(chromeJava.contains("TOP_BAR_HEIGHT_DP"));
        assertFalse(chromeJava.contains("BOTTOM_NAV_HEIGHT_DP"));
    }

    @Test
    public void fullscreenFlowsApplySharedInsets() throws Exception {
        assertTrue(read("app/src/main/java/fcu/app/i_ching/ui/InsetsHelper.java")
                .contains("applyFullscreenScrollInsets"));
        String[] fullscreenFragments = {
                "SplashFragment.java",
                "OnboardingFragment.java",
                "AuthFragment.java",
                "LocalEntryFragment.java",
                "QuestionFragment.java",
                "MethodFragment.java",
                "RitualFragment.java",
                "ResultFragment.java"
        };
        for (String fragment : fullscreenFragments) {
            assertTrue(fragment + " should apply shared insets",
                    read("app/src/main/java/fcu/app/i_ching/ui/" + fragment)
                            .contains("InsetsHelper.applyFullscreen"));
        }
    }

    private static File repoFile(String path) {
        File fromRoot = new File(path);
        if (fromRoot.exists()) return fromRoot;
        return new File(".." + File.separator + path);
    }

    private static String read(String path) throws Exception {
        return Files.readString(repoFile(path).toPath(), StandardCharsets.UTF_8);
    }
}
