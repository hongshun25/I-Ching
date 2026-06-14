package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.Set;

public class SettingsStore {
    private static final String PREFS = "i_ching_settings";
    private final SharedPreferences prefs;

    public SettingsStore(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public AppSettings get() {
        return new AppSettings(isOnboardingComplete(), isDarkMode(), isReduceMotion(), isAutoSave());
    }

    public boolean isOnboardingComplete() { return prefs.getBoolean("onboardingComplete", false); }
    public void setOnboardingComplete(boolean value) { prefs.edit().putBoolean("onboardingComplete", value).apply(); }
    public boolean isDarkMode() { return prefs.getBoolean("darkMode", false); }
    public void setDarkMode(boolean value) { prefs.edit().putBoolean("darkMode", value).apply(); }
    public boolean isReduceMotion() { return prefs.getBoolean("reduceMotion", true); }
    public void setReduceMotion(boolean value) { prefs.edit().putBoolean("reduceMotion", value).apply(); }
    public boolean isAutoSave() { return prefs.getBoolean("autoSave", true); }
    public void setAutoSave(boolean value) { prefs.edit().putBoolean("autoSave", value).apply(); }

    public Set<String> favoriteHexagrams() {
        return new HashSet<>(prefs.getStringSet("favorites", new HashSet<>()));
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
        prefs.edit().putStringSet("favorites", favorites).apply();
        return nowFavorite;
    }
}
