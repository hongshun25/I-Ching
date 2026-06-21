package fcu.app.i_ching.data;

public class AppSettings {
    public enum FontScale {
        SMALL("小", 0.92f),
        MEDIUM("適中", 1.0f),
        LARGE("大", 1.12f);

        public final String label;
        public final float multiplier;

        FontScale(String label, float multiplier) {
            this.label = label;
            this.multiplier = multiplier;
        }
    }

    public boolean onboardingComplete;
    public boolean darkMode;
    public boolean reduceMotion;
    public boolean autoSave;
    public FontScale fontScale;
    public DivinationMethod defaultMethod;
    public boolean dailyReminderEnabled;
    public int dailyReminderHour;
    public int dailyReminderMinute;

    public AppSettings(boolean onboardingComplete, boolean darkMode, boolean reduceMotion, boolean autoSave,
                       FontScale fontScale, DivinationMethod defaultMethod, boolean dailyReminderEnabled,
                       int dailyReminderHour, int dailyReminderMinute) {
        this.onboardingComplete = onboardingComplete;
        this.darkMode = darkMode;
        this.reduceMotion = reduceMotion;
        this.autoSave = autoSave;
        this.fontScale = fontScale;
        this.defaultMethod = defaultMethod;
        this.dailyReminderEnabled = dailyReminderEnabled;
        this.dailyReminderHour = dailyReminderHour;
        this.dailyReminderMinute = dailyReminderMinute;
    }
}
