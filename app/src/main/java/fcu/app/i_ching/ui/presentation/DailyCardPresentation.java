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

    public static DailyCardPresentation from(Hexagram hexagram, boolean night) {
        return new DailyCardPresentation(
                night ? "甲辰年 壬申月 丁卯日" : "早安，今天想安靜一下嗎？",
                "第" + hexagram.number + "卦｜" + hexagram.fullName,
                night ? hexagram.judgment : "「謙卑自守，則吉無不利。」",
                hexagram.summary,
                night ? 128 : 72,
                night ? 11 : 8,
                night
        );
    }
}
