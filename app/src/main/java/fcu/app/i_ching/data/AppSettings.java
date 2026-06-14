package fcu.app.i_ching.data;

public class AppSettings {
    public boolean onboardingComplete;
    public boolean darkMode;
    public boolean reduceMotion;
    public boolean autoSave;

    public AppSettings(boolean onboardingComplete, boolean darkMode, boolean reduceMotion, boolean autoSave) {
        this.onboardingComplete = onboardingComplete;
        this.darkMode = darkMode;
        this.reduceMotion = reduceMotion;
        this.autoSave = autoSave;
    }
}
