package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SettingsStore {
    private static final String PREFS = "i_ching_settings";
    private static final String KEY_ONBOARDING_COMPLETE = "onboardingComplete";
    private static final String KEY_DARK_MODE = "darkMode";
    private static final String KEY_REDUCE_MOTION = "reduceMotion";
    private static final String KEY_AUTO_SAVE = "autoSave";
    private static final String KEY_FONT_SCALE = "fontScale";
    private static final String KEY_DEFAULT_METHOD = "defaultMethod";
    private static final String KEY_DAILY_REMINDER_ENABLED = "dailyReminderEnabled";
    private static final String KEY_DAILY_REMINDER_HOUR = "dailyReminderHour";
    private static final String KEY_DAILY_REMINDER_MINUTE = "dailyReminderMinute";
    private static final String KEY_FAVORITES = "favorites";
    private static final String KEY_ACCOUNT_SETTINGS_MIGRATED = "accountSettingsMigrated";

    private final SharedPreferences prefs;
    private final AccountStore accountStore;

    public SettingsStore(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        accountStore = AccountStore.get(context);
        migrateLegacySettingsToGuestIfNeeded();
    }

    public AppSettings get() {
        return new AppSettings(
                isOnboardingComplete(),
                isDarkMode(),
                isReduceMotion(),
                isAutoSave(),
                fontScale(),
                defaultMethod(),
                isDailyReminderEnabled(),
                dailyReminderHour(),
                dailyReminderMinute()
        );
    }

    public boolean isOnboardingComplete() { return prefs.getBoolean(KEY_ONBOARDING_COMPLETE, false); }
    public void setOnboardingComplete(boolean value) { prefs.edit().putBoolean(KEY_ONBOARDING_COMPLETE, value).apply(); }
    public boolean isDarkMode() { return prefs.getBoolean(scoped(KEY_DARK_MODE), false); }
    public void setDarkMode(boolean value) { prefs.edit().putBoolean(scoped(KEY_DARK_MODE), value).apply(); }
    public boolean isReduceMotion() { return prefs.getBoolean(scoped(KEY_REDUCE_MOTION), true); }
    public void setReduceMotion(boolean value) { prefs.edit().putBoolean(scoped(KEY_REDUCE_MOTION), value).apply(); }
    public boolean isAutoSave() { return prefs.getBoolean(scoped(KEY_AUTO_SAVE), true); }
    public void setAutoSave(boolean value) { prefs.edit().putBoolean(scoped(KEY_AUTO_SAVE), value).apply(); }
    public AppSettings.FontScale fontScale() {
        return fontScaleFromName(prefs.getString(scoped(KEY_FONT_SCALE), AppSettings.FontScale.MEDIUM.name()));
    }
    public void setFontScale(AppSettings.FontScale value) {
        prefs.edit().putString(scoped(KEY_FONT_SCALE), normalizeFontScale(value).name()).apply();
    }
    public DivinationMethod defaultMethod() {
        return methodFromName(prefs.getString(scoped(KEY_DEFAULT_METHOD), DivinationMethod.COINS.name()));
    }
    public void setDefaultMethod(DivinationMethod value) {
        prefs.edit().putString(scoped(KEY_DEFAULT_METHOD), normalizeMethod(value).name()).apply();
    }
    public boolean isDailyReminderEnabled() {
        return prefs.getBoolean(scoped(KEY_DAILY_REMINDER_ENABLED), false);
    }
    public void setDailyReminderEnabled(boolean value) {
        prefs.edit().putBoolean(scoped(KEY_DAILY_REMINDER_ENABLED), value).apply();
    }
    public int dailyReminderHour() {
        return clamp(prefs.getInt(scoped(KEY_DAILY_REMINDER_HOUR), 9), 0, 23);
    }
    public int dailyReminderMinute() {
        return clamp(prefs.getInt(scoped(KEY_DAILY_REMINDER_MINUTE), 0), 0, 59);
    }
    public void setDailyReminderTime(int hour, int minute) {
        prefs.edit()
                .putInt(scoped(KEY_DAILY_REMINDER_HOUR), clamp(hour, 0, 23))
                .putInt(scoped(KEY_DAILY_REMINDER_MINUTE), clamp(minute, 0, 59))
                .apply();
    }

    public Set<String> favoriteHexagrams() {
        return new HashSet<>(prefs.getStringSet(scoped(KEY_FAVORITES), new HashSet<>()));
    }

    public boolean isFavorite(int number) {
        return favoriteHexagrams().contains(String.valueOf(number));
    }

    public boolean toggleFavorite(int number) {
        Set<String> favorites = favoriteHexagrams();
        String key = String.valueOf(number);
        boolean nowFavorite;
        if (favorites.contains(key)) {
            favorites.remove(key);
            nowFavorite = false;
        } else {
            favorites.add(key);
            nowFavorite = true;
        }
        prefs.edit().putStringSet(scoped(KEY_FAVORITES), favorites).apply();
        return nowFavorite;
    }

    public void transferGuestSettingsTo(String accountId) {
        if (accountId == null || accountId.isEmpty() || AccountStore.GUEST_ACCOUNT_ID.equals(accountId)) return;
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(scopedFor(accountId, KEY_DARK_MODE), prefs.getBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DARK_MODE), false));
        editor.putBoolean(scopedFor(accountId, KEY_REDUCE_MOTION), prefs.getBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_REDUCE_MOTION), true));
        editor.putBoolean(scopedFor(accountId, KEY_AUTO_SAVE), prefs.getBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_AUTO_SAVE), true));
        editor.putString(scopedFor(accountId, KEY_FONT_SCALE), prefs.getString(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_FONT_SCALE), AppSettings.FontScale.MEDIUM.name()));
        editor.putString(scopedFor(accountId, KEY_DEFAULT_METHOD), prefs.getString(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DEFAULT_METHOD), DivinationMethod.COINS.name()));
        editor.putBoolean(scopedFor(accountId, KEY_DAILY_REMINDER_ENABLED), prefs.getBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DAILY_REMINDER_ENABLED), false));
        editor.putInt(scopedFor(accountId, KEY_DAILY_REMINDER_HOUR), prefs.getInt(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DAILY_REMINDER_HOUR), 9));
        editor.putInt(scopedFor(accountId, KEY_DAILY_REMINDER_MINUTE), prefs.getInt(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DAILY_REMINDER_MINUTE), 0));
        editor.putStringSet(scopedFor(accountId, KEY_FAVORITES),
                new HashSet<>(prefs.getStringSet(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_FAVORITES), new HashSet<>())));
        removeAccountKeys(editor, AccountStore.GUEST_ACCOUNT_ID);
        editor.commit();
    }

    public void clearAccount(String accountId) {
        SharedPreferences.Editor editor = prefs.edit();
        removeAccountKeys(editor, accountId);
        editor.commit();
    }

    private void migrateLegacySettingsToGuestIfNeeded() {
        if (prefs.getBoolean(KEY_ACCOUNT_SETTINGS_MIGRATED, false)) return;
        SharedPreferences.Editor editor = prefs.edit();
        if (prefs.contains(KEY_DARK_MODE)) {
            editor.putBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_DARK_MODE), prefs.getBoolean(KEY_DARK_MODE, false));
        }
        if (prefs.contains(KEY_REDUCE_MOTION)) {
            editor.putBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_REDUCE_MOTION), prefs.getBoolean(KEY_REDUCE_MOTION, true));
        }
        if (prefs.contains(KEY_AUTO_SAVE)) {
            editor.putBoolean(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_AUTO_SAVE), prefs.getBoolean(KEY_AUTO_SAVE, true));
        }
        if (prefs.contains(KEY_FAVORITES)) {
            editor.putStringSet(scopedFor(AccountStore.GUEST_ACCOUNT_ID, KEY_FAVORITES),
                    new HashSet<>(prefs.getStringSet(KEY_FAVORITES, new HashSet<>())));
        }
        editor.putBoolean(KEY_ACCOUNT_SETTINGS_MIGRATED, true).commit();
    }

    private String scoped(String key) {
        return scopedFor(accountStore.activeAccountId(), key);
    }

    private String scopedFor(String accountId, String key) {
        String resolvedAccountId = accountId == null || accountId.isEmpty()
                ? AccountStore.GUEST_ACCOUNT_ID
                : accountId;
        return "account." + resolvedAccountId + "." + key;
    }

    private void removeAccountKeys(SharedPreferences.Editor editor, String accountId) {
        editor.remove(scopedFor(accountId, KEY_DARK_MODE));
        editor.remove(scopedFor(accountId, KEY_REDUCE_MOTION));
        editor.remove(scopedFor(accountId, KEY_AUTO_SAVE));
        editor.remove(scopedFor(accountId, KEY_FONT_SCALE));
        editor.remove(scopedFor(accountId, KEY_DEFAULT_METHOD));
        editor.remove(scopedFor(accountId, KEY_DAILY_REMINDER_ENABLED));
        editor.remove(scopedFor(accountId, KEY_DAILY_REMINDER_HOUR));
        editor.remove(scopedFor(accountId, KEY_DAILY_REMINDER_MINUTE));
        editor.remove(scopedFor(accountId, KEY_FAVORITES));
    }

    private AppSettings.FontScale fontScaleFromName(String value) {
        try {
            return AppSettings.FontScale.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return AppSettings.FontScale.MEDIUM;
        }
    }

    private AppSettings.FontScale normalizeFontScale(AppSettings.FontScale value) {
        return value == null ? AppSettings.FontScale.MEDIUM : value;
    }

    private DivinationMethod methodFromName(String value) {
        try {
            return DivinationMethod.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return DivinationMethod.COINS;
        }
    }

    private DivinationMethod normalizeMethod(DivinationMethod value) {
        return value == null ? DivinationMethod.COINS : value;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
