package fcu.app.i_ching.data;

import java.util.Arrays;
import java.util.List;

public class Hexagram {
    public final int number;
    public final String name;
    public final String fullName;
    public final String upper;
    public final String lower;
    public final boolean[] linesBottomToTop;
    public final List<String> tags;
    public final String judgment;
    public final String summary;
    public final String theme;
    public final String classicalText;
    public final String modernText;
    public final List<String> doItems;
    public final List<String> avoidItems;

    public Hexagram(int number, String name, String fullName, String upper, String lower,
                    boolean[] linesBottomToTop, List<String> tags, String judgment, String summary,
                    String theme, String classicalText, String modernText,
                    List<String> doItems, List<String> avoidItems) {
        this.number = number;
        this.name = name;
        this.fullName = fullName;
        this.upper = upper;
        this.lower = lower;
        this.linesBottomToTop = linesBottomToTop;
        this.tags = tags;
        this.judgment = judgment;
        this.summary = summary;
        this.theme = theme;
        this.classicalText = classicalText;
        this.modernText = modernText;
        this.doItems = doItems;
        this.avoidItems = avoidItems;
    }

    public static Hexagram basic(int number, String name, String fullName, String upper, String lower,
                                 boolean[] lines, String tag1, String tag2) {
        return new Hexagram(number, name, fullName, upper, lower, lines, Arrays.asList(tag1, tag2),
                "以平常心觀察變化，順勢而行。",
                "此卦提醒你回到當下，先看清局勢，再做下一步。",
                tag1 + "、" + tag2,
                "卦辭詳解待補。",
                "這一卦可作為自我反思的入口：辨識當前處境的主要力量，並選擇較穩妥的行動。",
                Arrays.asList("整理資訊", "保留彈性"),
                Arrays.asList("急於定論", "忽略訊號"));
    }
}
