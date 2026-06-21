package fcu.app.i_ching.data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class SettingsStoreTest {
    private Context context;
    private SettingsStore store;

    @Before
    public void setUp() {
        context = RuntimeEnvironment.getApplication();
        context.getSharedPreferences("i_ching_settings", Context.MODE_PRIVATE).edit().clear().commit();
        context.getSharedPreferences(AccountStore.PREFS, Context.MODE_PRIVATE).edit().clear().commit();
        AccountStore.resetForTests();
        AccountStore.get(context).useGuest();
        store = new SettingsStore(context);
    }

    @Test
    public void defaultsMatchLocalBetaPreferences() {
        AppSettings settings = store.get();

        assertFalse(settings.onboardingComplete);
        assertFalse(settings.darkMode);
        assertTrue(settings.reduceMotion);
        assertTrue(settings.autoSave);
        assertEquals(AppSettings.FontScale.MEDIUM, settings.fontScale);
        assertEquals(DivinationMethod.COINS, settings.defaultMethod);
        assertFalse(settings.dailyReminderEnabled);
        assertEquals(9, settings.dailyReminderHour);
        assertEquals(0, settings.dailyReminderMinute);
        assertTrue(store.favoriteHexagrams().isEmpty());
    }

    @Test
    public void togglesPersistAndFavoritesCanBeAddedAndRemoved() {
        store.setOnboardingComplete(true);
        store.setDarkMode(true);
        store.setReduceMotion(false);
        store.setAutoSave(false);
        store.setFontScale(AppSettings.FontScale.LARGE);
        store.setDefaultMethod(DivinationMethod.YARROW);
        store.setDailyReminderEnabled(true);
        store.setDailyReminderTime(18, 45);

        assertTrue(store.isOnboardingComplete());
        assertTrue(store.isDarkMode());
        assertFalse(store.isReduceMotion());
        assertFalse(store.isAutoSave());
        assertEquals(AppSettings.FontScale.LARGE, store.fontScale());
        assertEquals(DivinationMethod.YARROW, store.defaultMethod());
        assertTrue(store.isDailyReminderEnabled());
        assertEquals(18, store.dailyReminderHour());
        assertEquals(45, store.dailyReminderMinute());

        assertTrue(store.toggleFavorite(15));
        assertTrue(store.isFavorite(15));
        assertFalse(store.toggleFavorite(15));
        assertFalse(store.isFavorite(15));
    }

    @Test
    public void accountScopedSettingsAreIsolated() {
        AccountStore accounts = AccountStore.get(context);
        store.setDarkMode(true);
        store.setAutoSave(false);
        store.setFontScale(AppSettings.FontScale.SMALL);
        store.setDefaultMethod(DivinationMethod.SIMPLE);
        store.setDailyReminderEnabled(true);
        store.setDailyReminderTime(7, 30);
        assertTrue(store.toggleFavorite(15));

        AccountStore.AuthResult first = accounts.register("first@example.com", "password123", "password123");
        store.transferGuestSettingsTo(first.account.id);

        assertTrue(store.isDarkMode());
        assertFalse(store.isAutoSave());
        assertEquals(AppSettings.FontScale.SMALL, store.fontScale());
        assertEquals(DivinationMethod.SIMPLE, store.defaultMethod());
        assertTrue(store.isDailyReminderEnabled());
        assertEquals(7, store.dailyReminderHour());
        assertEquals(30, store.dailyReminderMinute());
        assertTrue(store.isFavorite(15));

        AccountStore.AuthResult second = accounts.register("second@example.com", "password123", "password123");
        assertTrue(second.success);
        store.setDarkMode(false);
        store.setAutoSave(true);
        store.setFontScale(AppSettings.FontScale.LARGE);
        store.setDefaultMethod(DivinationMethod.YARROW);
        store.setDailyReminderEnabled(false);
        store.setDailyReminderTime(21, 15);
        assertFalse(store.isFavorite(15));

        accounts.login("first@example.com", "password123");

        assertTrue(store.isDarkMode());
        assertFalse(store.isAutoSave());
        assertEquals(AppSettings.FontScale.SMALL, store.fontScale());
        assertEquals(DivinationMethod.SIMPLE, store.defaultMethod());
        assertTrue(store.isDailyReminderEnabled());
        assertEquals(7, store.dailyReminderHour());
        assertEquals(30, store.dailyReminderMinute());
        assertTrue(store.isFavorite(15));
    }

    @Test
    public void guestTransferMovesFavoritesAndResetsGuestScope() {
        AccountStore accounts = AccountStore.get(context);
        store.setReduceMotion(false);
        store.setFontScale(AppSettings.FontScale.LARGE);
        store.setDefaultMethod(DivinationMethod.YARROW);
        store.setDailyReminderEnabled(true);
        store.setDailyReminderTime(6, 5);
        assertTrue(store.toggleFavorite(29));

        AccountStore.AuthResult registered = accounts.register("user@example.com", "password123", "password123");
        store.transferGuestSettingsTo(registered.account.id);

        assertFalse(store.isReduceMotion());
        assertEquals(AppSettings.FontScale.LARGE, store.fontScale());
        assertEquals(DivinationMethod.YARROW, store.defaultMethod());
        assertTrue(store.isDailyReminderEnabled());
        assertEquals(6, store.dailyReminderHour());
        assertEquals(5, store.dailyReminderMinute());
        assertTrue(store.isFavorite(29));

        accounts.useGuest();

        assertTrue(store.isReduceMotion());
        assertEquals(AppSettings.FontScale.MEDIUM, store.fontScale());
        assertEquals(DivinationMethod.COINS, store.defaultMethod());
        assertFalse(store.isDailyReminderEnabled());
        assertEquals(9, store.dailyReminderHour());
        assertEquals(0, store.dailyReminderMinute());
        assertFalse(store.isFavorite(29));
    }
}
