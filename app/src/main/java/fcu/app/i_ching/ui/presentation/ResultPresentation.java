package fcu.app.i_ching.ui.presentation;

import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.HexagramLine;

public final class ResultPresentation {
    public final String questionText;
    public final String titleText;
    public final String tagText;
    public final String relationText;
    public final String changingSummary;
    public final String changedLineText;
    public final String blindSpotText;
    public final String shareText;

    private ResultPresentation(String questionText, String titleText, String tagText, String relationText,
                               String changingSummary, String changedLineText, String blindSpotText, String shareText) {
        this.questionText = questionText;
        this.titleText = titleText;
        this.tagText = tagText;
        this.relationText = relationText;
        this.changingSummary = changingSummary;
        this.changedLineText = changedLineText;
        this.blindSpotText = blindSpotText;
        this.shareText = shareText;
    }

    public static ResultPresentation from(DivinationResult result) {
        String changingSummary = changingSummary(result);
        return new ResultPresentation(
                "“" + result.question + "”",
                "第" + result.hexagram.number + "卦｜" + result.hexagram.fullName,
                "上" + result.hexagram.upper + "　下" + result.hexagram.lower + "　" + result.method.label,
                "本卦 第" + result.hexagram.number + "卦｜" + result.hexagram.fullName
                        + "  →  之卦 第" + result.relatingHexagramNumber + "卦｜" + result.relatingHexagram.fullName,
                changingSummary,
                changedLineText(result),
                blindSpotText(result),
                shareText(result, changingSummary)
        );
    }

    private static String changingSummary(DivinationResult result) {
        if (result.changingLines.isEmpty()) return "本次無變爻，之卦與本卦相同。";
        StringBuilder builder = new StringBuilder("變爻：");
        for (int i = 0; i < result.changingLines.size(); i++) {
            if (i > 0) builder.append("、");
            builder.append(positionName(result.changingLines.get(i)));
        }
        return builder.toString();
    }

    private static String changedLineText(DivinationResult result) {
        if (result.changingLines.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (Integer position : result.changingLines) {
            if (position == null || position < 1 || position > result.hexagram.lineTexts.size()) continue;
            HexagramLine line = result.hexagram.lineTexts.get(position - 1);
            if (builder.length() > 0) builder.append("\n\n");
            builder.append(line.label).append("：").append(line.text).append("\n").append(line.modernHint);
        }
        return builder.toString();
    }

    private static String shareText(DivinationResult result, String changingSummary) {
        return "《易經占卜》\n"
                + "問題：" + result.question + "\n"
                + "占法：" + result.method.label + "\n"
                + "本卦：第" + result.hexagram.number + "卦｜" + result.hexagram.fullName + "\n"
                + changingSummary + "\n"
                + "之卦：第" + result.relatingHexagramNumber + "卦｜" + result.relatingHexagram.fullName + "\n"
                + "啟示：" + result.hexagram.summary;
    }

    private static String blindSpotText(DivinationResult result) {
        if (result.changingLines.isEmpty()) {
            return "本卦沒有變爻，容易把穩定誤認為停滯。先確認哪些條件已經足夠，再決定是否需要推進。";
        }
        if (result.changingLines.size() >= 3) {
            return "變爻較多，表示局勢同時牽動多個層面。先找出最容易失衡的一爻，不要一次處理所有問題。";
        }
        String tag = result.hexagram.tags.isEmpty() ? result.hexagram.name : result.hexagram.tags.get(0);
        return "變爻顯示轉折正在形成。留意你忽略的小訊號，尤其是與「"
                + tag + "」相關的行動節奏。";
    }

    static String positionName(int position) {
        switch (position) {
            case 1: return "初爻";
            case 2: return "二爻";
            case 3: return "三爻";
            case 4: return "四爻";
            case 5: return "五爻";
            case 6: return "上爻";
            default: return "第" + position + "爻";
        }
    }
}
