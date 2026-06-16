package fcu.app.i_ching;

import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;

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

    private static File repoFile(String path) {
        File fromRoot = new File(path);
        if (fromRoot.exists()) return fromRoot;
        return new File(".." + File.separator + path);
    }
}
