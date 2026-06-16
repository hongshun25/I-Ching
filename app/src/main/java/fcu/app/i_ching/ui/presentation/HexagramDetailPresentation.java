package fcu.app.i_ching.ui.presentation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramLine;

public final class HexagramDetailPresentation {
    public final String titleText;
    public final String summaryText;
    public final String primaryChipText;
    public final String secondaryChipText;
    public final boolean favorite;
    public final int favoriteIconRes;
    public final String favoriteContentDescription;
    public final List<Section> sections;

    private HexagramDetailPresentation(String titleText, String summaryText, String primaryChipText,
                                       String secondaryChipText, boolean favorite, int favoriteIconRes,
                                       String favoriteContentDescription, List<Section> sections) {
        this.titleText = titleText;
        this.summaryText = summaryText;
        this.primaryChipText = primaryChipText;
        this.secondaryChipText = secondaryChipText;
        this.favorite = favorite;
        this.favoriteIconRes = favoriteIconRes;
        this.favoriteContentDescription = favoriteContentDescription;
        this.sections = Collections.unmodifiableList(new ArrayList<>(sections));
    }

    public static HexagramDetailPresentation from(Hexagram hexagram, boolean favorite) {
        FavoriteHexagramPresentation favoritePresentation =
                FavoriteHexagramPresentation.from(hexagram.number, favorite);
        List<Section> sections = new ArrayList<>();
        sections.add(new Section("卦象組成", "上卦：" + hexagram.upper + "\n下卦：" + hexagram.lower));
        sections.add(new Section("主旨核心", hexagram.theme));
        sections.add(new Section("適宜情境", "• " + String.join("\n• ", hexagram.doItems)));
        sections.add(new Section("《易經》原文", hexagram.judgment + "\n\n" + hexagram.classicalText));
        sections.add(new Section("六爻爻辭", lineText(hexagram)));
        sections.add(new Section("現代解析", hexagram.modernText));
        return new HexagramDetailPresentation(
                "第" + hexagram.number + "卦｜" + hexagram.name,
                hexagram.summary,
                hexagram.fullName,
                hexagram.tags.contains("吉卦") ? "吉卦" : hexagram.tags.get(0),
                favoritePresentation.favorite,
                favoritePresentation.iconRes,
                favoritePresentation.contentDescription,
                sections
        );
    }

    private static String lineText(Hexagram hexagram) {
        StringBuilder builder = new StringBuilder();
        for (HexagramLine line : hexagram.lineTexts) {
            if (builder.length() > 0) builder.append("\n\n");
            builder.append(line.label).append("：").append(line.text).append("\n").append(line.modernHint);
        }
        return builder.toString();
    }

    public static final class Section {
        public final String title;
        public final String body;

        public Section(String title, String body) {
            this.title = title;
            this.body = body;
        }
    }
}
