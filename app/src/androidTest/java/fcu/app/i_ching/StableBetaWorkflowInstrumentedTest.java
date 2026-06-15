package fcu.app.i_ching;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.UiController;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.accessibility.AccessibilityChecks;
import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.List;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.IChingDatabase;
import fcu.app.i_ching.data.RecordRepository;
import fcu.app.i_ching.data.SettingsStore;

import static androidx.test.espresso.Espresso.closeSoftKeyboard;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.isEnabled;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class StableBetaWorkflowInstrumentedTest {
    private Context context;

    @BeforeClass
    public static void enableAccessibilityChecks() {
        AccessibilityChecks.enable();
    }

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        clearAppState();
    }

    @After
    public void tearDown() {
        clearAppState();
    }

    @Test
    public void onboardingLocalModeRoutesToDaily() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            waitFor(withId(R.id.local_mode_button));
            onView(withId(R.id.local_mode_button)).perform(click());

            waitFor(withText("早安，今天想安靜一下嗎？"));
            onView(withId(R.id.bottom_nav_daily)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void divinationFlowAutoSavesAndShowsRecord() {
        new SettingsStore(context).setOnboardingComplete(true);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showQuestion();
            });
            waitFor(withId(R.id.question_input));

            onView(withId(R.id.question_input)).perform(replaceText("內測工作節奏如何調整？"));
            closeSoftKeyboard();
            onView(withId(R.id.question_next_button)).perform(scrollTo(), click());
            waitFor(withId(R.id.method_simple_card));
            onView(withId(R.id.method_simple_card)).perform(scrollTo(), click());
            onView(withText("開始靜心 →")).perform(scrollTo(), click());
            waitFor(withId(R.id.ritual_skip_button));
            onView(withId(R.id.ritual_skip_button)).perform(click());

            waitForExists(withId(R.id.result_note_input));
            waitForRecordsCount(1);
            scenario.onActivity(MainActivity::showRecords);
            waitFor(withId(R.id.records_search_input));
            waitFor(withText("內測工作節奏如何調整？"));
        }
    }

    @Test
    public void recordsAllowNoteEditAndDelete() {
        new SettingsStore(context).setOnboardingComplete(true);
        seedRecord();

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showRecords();
            });
            waitFor(withText("內測紀錄問題"));

            waitFor(withContentDescription("編輯第15卦紀錄筆記"));
            onView(withContentDescription("編輯第15卦紀錄筆記")).perform(callOnClick());
            waitForExists(withHint("補充這次占卜的反思..."));
            onView(withHint("補充這次占卜的反思...")).perform(replaceText("已更新的內測筆記"));
            closeSoftKeyboard();
            onView(withText("儲存")).perform(click());
            waitFor(withText("已更新的內測筆記"));

            waitFor(withContentDescription("刪除第15卦紀錄"));
            onView(withContentDescription("刪除第15卦紀錄")).perform(callOnClick());
            waitFor(withText("刪除紀錄？"));
            onView(withText("刪除")).perform(click());
            waitForRecordsCount(0);
        }
    }

    @Test
    public void resultRecreateDoesNotDuplicateAutoSavedRecord() {
        new SettingsStore(context).setOnboardingComplete(true);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showResult("旋轉後是否仍是同一筆紀錄？", DivinationMethod.SIMPLE);
            });
            waitForExists(withId(R.id.result_note_input));
            waitForRecordsCount(1);

            scenario.recreate();

            waitForExists(withId(R.id.result_note_input));
            waitForRecordsCount(1);
        }
    }

    @Test
    public void recordsSearchAndMethodFilterSurviveRecreate() {
        new SettingsStore(context).setOnboardingComplete(true);
        seedRecord(new DivinationRecord(9101L, "保留篩選 金錢紀錄", 15, 55,
                DivinationMethod.COINS, new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4),
                9101L, ""));
        seedRecord(new DivinationRecord(9102L, "保留篩選 簡易紀錄", 29, 29,
                DivinationMethod.SIMPLE, new int[]{8, 7, 8, 8, 7, 8}, Arrays.asList(),
                9102L, ""));

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showRecords();
            });
            waitFor(withId(R.id.records_search_input));
            onView(withId(R.id.records_search_input)).perform(replaceText("保留篩選"));
            closeSoftKeyboard();
            onView(withText("簡易占法")).perform(click());
            waitFor(withText("保留篩選 簡易紀錄"));
            onView(withText("保留篩選 金錢紀錄")).check(doesNotExist());

            scenario.recreate();

            waitFor(withId(R.id.records_search_input));
            onView(withId(R.id.records_search_input)).check(matches(withText("保留篩選")));
            waitFor(withText("保留篩選 簡易紀錄"));
            onView(withText("保留篩選 金錢紀錄")).check(doesNotExist());
        }
    }

    @Test
    public void methodSelectionShowsExplicitSelectedState() {
        new SettingsStore(context).setOnboardingComplete(true);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showMethod("測試占法選擇狀態");
            });
            waitFor(withId(R.id.method_simple_card));

            onView(withId(R.id.method_simple_card)).perform(scrollTo(), click());

            waitFor(withId(R.id.method_simple_status));
            onView(withId(R.id.method_simple_status)).check(matches(isDisplayed()));
            onView(withId(R.id.method_simple_card)).check(matches(withContentDescription("簡易占法，已選擇")));
        }
    }

    @Test
    public void learnCenterFavoriteAndProfileDarkModePersist() {
        SettingsStore settings = new SettingsStore(context);
        settings.setOnboardingComplete(true);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showLearnCenter();
            });
            waitFor(withContentDescription("加入收藏第1卦"));
            onView(withContentDescription("加入收藏第1卦")).perform(scrollTo(), click());
            waitFor(withContentDescription("取消收藏第1卦"));
            assertTrue(new SettingsStore(context).isFavorite(1));

            scenario.onActivity(MainActivity::showProfile);
            waitFor(withId(R.id.profile_dark_mode_switch));
            onView(withId(R.id.profile_dark_mode_switch)).perform(scrollTo(), click());
            waitForCondition(() -> new SettingsStore(context).isDarkMode());
        }
    }

    @Test
    public void learnCenterSearchOpensRecyclerViewDetail() {
        new SettingsStore(context).setOnboardingComplete(true);

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showLearnCenter();
            });
            waitFor(withId(R.id.learn_search_input));

            onView(withId(R.id.learn_search_input)).perform(replaceText("謙"));
            closeSoftKeyboard();
            waitFor(withContentDescription("開啟第15卦地山謙詳情"));
            onView(withContentDescription("開啟第15卦地山謙詳情")).perform(callOnClick());

            waitFor(withText("第15卦｜謙"));
        }
    }

    @Test
    public void profileExportContractsUseStorageAccessFramework() {
        Intent jsonIntent = new ActivityResultContracts.CreateDocument("application/json")
                .createIntent(context, "i_ching_records.json");
        Intent textIntent = new ActivityResultContracts.CreateDocument("text/plain")
                .createIntent(context, "i_ching_records.txt");

        assertEquals(Intent.ACTION_CREATE_DOCUMENT, jsonIntent.getAction());
        assertEquals("application/json", jsonIntent.getType());
        assertEquals(Intent.ACTION_CREATE_DOCUMENT, textIntent.getAction());
        assertEquals("text/plain", textIntent.getType());
    }

    @Test
    public void profileExportRowsLaunchStorageAccessFramework() {
        new SettingsStore(context).setOnboardingComplete(true);
        seedRecord();
        Instrumentation.ActivityResult canceled = new Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null);

        Intents.init();
        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            intending(createDocumentIntent("application/json", "i_ching_records.json")).respondWith(canceled);
            intending(createDocumentIntent("text/plain", "i_ching_records.txt")).respondWith(canceled);
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showProfile();
            });

            waitForExists(withId(R.id.profile_export_json));
            onView(withId(R.id.profile_export_json)).perform(scrollTo(), callOnClick());
            waitForIntent(createDocumentIntent("application/json", "i_ching_records.json"));

            onView(withId(R.id.profile_export_text)).perform(scrollTo(), callOnClick());
            waitForIntent(createDocumentIntent("text/plain", "i_ching_records.txt"));
        } finally {
            Intents.release();
        }
    }

    @Test
    public void profileDeleteAllRemovesRecords() {
        new SettingsStore(context).setOnboardingComplete(true);
        seedRecord();

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            waitForRecordsCount(1);
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showProfile();
            });
            waitForExists(withId(R.id.profile_delete_all));
            onView(withId(R.id.profile_delete_all)).perform(scrollTo(), callOnClick());
            waitFor(withText("刪除全部紀錄？"));
            onView(withText("刪除全部")).perform(click());
            waitForRecordsCount(0);
        }
    }

    @Test
    public void profileDeleteAllCancelKeepsRecords() {
        new SettingsStore(context).setOnboardingComplete(true);
        seedRecord();

        try (ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class)) {
            waitForRecordsCount(1);
            scenario.onActivity(activity -> {
                activity.enterLocalMode();
                activity.showProfile();
            });
            waitForExists(withId(R.id.profile_delete_all));
            onView(withId(R.id.profile_delete_all)).perform(scrollTo(), callOnClick());
            waitFor(withText("刪除全部紀錄？"));
            onView(withText("取消")).perform(click());

            waitForRecordsCount(1);
            scenario.onActivity(MainActivity::showRecords);
            waitFor(withText("內測紀錄問題"));
        }
    }

    private void seedRecord() {
        seedRecord(new DivinationRecord(9001L, "內測紀錄問題", 15, 55,
                DivinationMethod.COINS, new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4),
                9001L, ""));
    }

    private void seedRecord(DivinationRecord record) {
        RecordRepository.get(context).addOrUpdate(record);
    }

    private void clearAppState() {
        IChingDatabase.get(context).recordDao().deleteAll();
        context.getSharedPreferences("i_ching_settings", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("i_ching_records", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences("i_ching_record_migration", Context.MODE_PRIVATE).edit().clear().commit();
    }

    private void waitFor(Matcher<View> matcher) {
        waitForCondition(() -> {
            try {
                onView(matcher).check(matches(isDisplayed()));
                return true;
            } catch (RuntimeException | AssertionError e) {
                return false;
            }
        });
    }

    private void waitForExists(Matcher<View> matcher) {
        waitForCondition(() -> {
            try {
                onView(matcher).check((view, noViewFoundException) -> {
                    if (noViewFoundException != null) throw noViewFoundException;
                });
                return true;
            } catch (RuntimeException | AssertionError e) {
                return false;
            }
        });
    }

    private void waitForRecordsCount(int count) {
        waitForCondition(() -> {
            List<DivinationRecord> records = RecordRepository.get(context).recordsNow();
            return records.size() == count;
        });
        assertEquals(count, RecordRepository.get(context).recordsNow().size());
    }

    private void waitForIntent(Matcher<Intent> matcher) {
        waitForCondition(() -> {
            try {
                intended(matcher);
                return true;
            } catch (AssertionError e) {
                return false;
            }
        });
    }

    private Matcher<Intent> createDocumentIntent(String type, String fileName) {
        return new TypeSafeMatcher<Intent>() {
            @Override
            protected boolean matchesSafely(Intent intent) {
                return Intent.ACTION_CREATE_DOCUMENT.equals(intent.getAction())
                        && type.equals(intent.getType())
                        && fileName.equals(intent.getStringExtra(Intent.EXTRA_TITLE));
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("ACTION_CREATE_DOCUMENT intent with type ")
                        .appendValue(type)
                        .appendText(" and title ")
                        .appendValue(fileName);
            }
        };
    }

    private ViewAction callOnClick() {
        return new ViewAction() {
            @Override
            public org.hamcrest.Matcher<View> getConstraints() {
                return isEnabled();
            }

            @Override
            public String getDescription() {
                return "call the view click listener";
            }

            @Override
            public void perform(UiController uiController, View view) {
                view.callOnClick();
                uiController.loopMainThreadUntilIdle();
            }
        };
    }

    private void waitForCondition(Condition condition) {
        long deadline = SystemClock.uptimeMillis() + 8000L;
        while (SystemClock.uptimeMillis() < deadline) {
            if (condition.isMet()) return;
            SystemClock.sleep(100L);
        }
        assertTrue("Timed out waiting for condition", condition.isMet());
    }

    private interface Condition {
        boolean isMet();
    }
}
