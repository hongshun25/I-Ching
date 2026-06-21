package fcu.app.i_ching.ui.presentation;

import fcu.app.i_ching.data.Hexagram;

public final class DailyCardPresentation {
    public final String greetingText;
    public final String titleText;
    public final String judgmentText;
    public final String summaryText;
    public final int hexagramWidthDp;
    public final int lineHeightDp;
    public final boolean centeredGreeting;

    private DailyCardPresentation(String greetingText, String titleText, String judgmentText,
                                  String summaryText, int hexagramWidthDp, int lineHeightDp,
                                  boolean centeredGreeting) {
        this.greetingText = greetingText;
        this.titleText = titleText;
        this.judgmentText = judgmentText;
        this.summaryText = summaryText;
        this.hexagramWidthDp = hexagramWidthDp;
        this.lineHeightDp = lineHeightDp;
        this.centeredGreeting = centeredGreeting;
    }

    public static DailyCardPresentation from(Hexagram hexagram, String dateText, boolean night) {
        return new DailyCardPresentation(
                dateText == null || dateText.isEmpty() ? "今日一卦" : dateText,
                "第" + hexagram.number + "卦｜" + hexagram.fullName,
                hexagram.judgment,
                hexagram.summary,
                night ? 128 : 72,
                night ? 11 : 8,
                night
        );
    }

    public static DailyCardPresentation from(Hexagram hexagram, boolean night) {
        return from(hexagram, "今日一卦", night);
    }
}
