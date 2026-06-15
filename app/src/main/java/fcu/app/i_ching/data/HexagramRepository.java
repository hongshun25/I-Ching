package fcu.app.i_ching.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class HexagramRepository {
    public static final String FILTER_ALL = "全部";
    public static final String FILTER_UPPER_CANON = "上經";
    public static final String FILTER_LOWER_CANON = "下經";
    public static final String FILTER_FAVORITES = "我的收藏";
    private static final List<Hexagram> HEXAGRAMS = build();
    private static final Map<Integer, Hexagram> BY_NUMBER = new HashMap<>();

    static {
        for (Hexagram hexagram : HEXAGRAMS) {
            BY_NUMBER.put(hexagram.number, hexagram);
        }
    }

    private HexagramRepository() {}

    public static List<Hexagram> all() {
        return HEXAGRAMS;
    }

    public static Hexagram get(int number) {
        Hexagram hexagram = BY_NUMBER.get(number);
        return hexagram == null ? BY_NUMBER.get(15) : hexagram;
    }

    public static List<Hexagram> filter(String query, String filter, Set<String> favoriteNumbers) {
        List<Hexagram> matches = new ArrayList<>();
        String normalizedQuery = normalize(query);
        for (Hexagram hexagram : HEXAGRAMS) {
            if (matchesFilter(hexagram, filter, favoriteNumbers) && matchesQuery(hexagram, normalizedQuery)) {
                matches.add(hexagram);
            }
        }
        return matches;
    }

    private static boolean matchesFilter(Hexagram hexagram, String filter, Set<String> favoriteNumbers) {
        if (FILTER_UPPER_CANON.equals(filter)) return hexagram.number <= 30;
        if (FILTER_LOWER_CANON.equals(filter)) return hexagram.number >= 31;
        if (FILTER_FAVORITES.equals(filter)) {
            return favoriteNumbers != null && favoriteNumbers.contains(String.valueOf(hexagram.number));
        }
        return true;
    }

    private static boolean matchesQuery(Hexagram hexagram, String query) {
        if (query.isEmpty()) return true;
        if (normalize(hexagram.name).contains(query)) return true;
        if (normalize(hexagram.fullName).contains(query)) return true;
        if (normalize(hexagram.summary).contains(query)) return true;
        for (String tag : hexagram.tags) {
            if (normalize(tag).contains(query)) return true;
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }


    public static Hexagram fromLines(boolean[] bottomToTop) {
        if (Arrays.equals(bottomToTop, get(15).linesBottomToTop)) {
            return get(15);
        }
        if (Arrays.equals(bottomToTop, get(29).linesBottomToTop)) {
            return get(29);
        }
        for (Hexagram hexagram : HEXAGRAMS) {
            if (Arrays.equals(hexagram.linesBottomToTop, bottomToTop)) {
                return hexagram;
            }
        }
        int index = 0;
        for (int i = 0; i < Math.min(6, bottomToTop.length); i++) {
            if (bottomToTop[i]) index += 1 << i;
        }
        return get((index % 64) + 1);
    }

    private static boolean[] bits(int value) {
        boolean[] lines = new boolean[6];
        for (int i = 0; i < 6; i++) {
            lines[i] = ((value >> i) & 1) == 1;
        }
        return lines;
    }

    private static List<Hexagram> build() {
        String[] names = {"乾","坤","屯","蒙","需","訟","師","比","小畜","履","泰","否","同人","大有","謙","豫","隨","蠱","臨","觀","噬嗑","賁","剝","復","無妄","大畜","頤","大過","坎","離","咸","恆","遯","大壯","晉","明夷","家人","睽","蹇","解","損","益","夬","姤","萃","升","困","井","革","鼎","震","艮","漸","歸妹","豐","旅","巽","兌","渙","節","中孚","小過","既濟","未濟"};
        String[] full = {"乾為天","坤為地","水雷屯","山水蒙","水天需","天水訟","地水師","水地比","風天小畜","天澤履","地天泰","天地否","天火同人","火天大有","地山謙","雷地豫","澤雷隨","山風蠱","地澤臨","風地觀","火雷噬嗑","山火賁","山地剝","地雷復","天雷無妄","山天大畜","山雷頤","澤風大過","坎為水","離為火","澤山咸","雷風恆","天山遯","雷天大壯","火地晉","地火明夷","風火家人","火澤睽","水山蹇","雷水解","山澤損","風雷益","澤天夬","天風姤","澤地萃","地風升","澤水困","水風井","澤火革","火風鼎","震為雷","艮為山","風山漸","雷澤歸妹","雷火豐","火山旅","巽為風","兌為澤","風水渙","水澤節","風澤中孚","雷山小過","水火既濟","火水未濟"};
        String[] tags = {"創造力,剛健不息","包容,厚德載物","萌芽,艱難初始","啟蒙,學習","等待,蓄勢","爭辯,界線","紀律,團隊","親比,支持","小有積蓄,溫和推進","禮節,步步謹慎","通泰,和諧","閉塞,守正","同心,合作","豐盛,光明","低姿態,內斂的力量","喜悅,順勢","順應,跟隨","整頓,除弊","靠近,照顧","觀看,洞察","決斷,明辨","修飾,文明","剝落,止損","回復,新生","真誠,無妄","蓄養,節制","滋養,口實","承壓,突破","險陷,誠信","光明,依附","感應,互動","長久,穩定","退避,保全","壯盛,節制力量","晉升,前進","受傷,藏明","家道,內外有序","差異,求同","阻難,求助","解開,釋放","減損,去繁","增益,助長","決裂,果斷","相遇,警覺","聚集,凝聚","上升,漸進","困頓,守心","源泉,更新","變革,除舊","鼎新,承載","震動,警醒","止息,安定","漸進,有序","婚合,失位","豐盛,照明","旅行,暫居","入微,柔順","喜悅,交流","渙散,重聚","節制,界線","誠信,中正","小事,謹慎","完成,守成","未完,續行"};
        List<Hexagram> list = new ArrayList<>();
        for (int i = 0; i < 64; i++) {
            String[] pair = tags[i].split(",");
            list.add(Hexagram.basic(i + 1, names[i], full[i], upperOf(full[i]), lowerOf(full[i]), bits(i), pair[0], pair[1]));
        }
        list.set(14, new Hexagram(15, "謙", "地山謙", "坤 / 地", "艮 / 山",
                new boolean[]{false, false, true, false, false, false},
                Arrays.asList("低姿態", "內斂的力量", "吉卦"),
                "謙：亨，君子有終。",
                "在滿溢的世界裡，保持內心的虛空。今天適合退一步，不強求表現，將舞台讓給他人，反而能收穫意想不到的平靜與支持。",
                "謙虛、退讓、平衡。真正的力量來自於內在的充實與外在的柔順，如同高山隱於大地之下。",
                "謙，亨。天道下濟而光明，地道卑而上行。天道虧盈而益謙，地道變盈而流謙，鬼神害盈而福謙，人道惡盈而好謙。謙尊而光，卑而不可逾，君子之終也。",
                "謙卦是《易經》六十四卦中少見全爻皆吉的卦。它提醒我們，過度的自我膨脹會引來反撲，而保持謙遜的態度，反而能獲得更多支持與資源。",
                Arrays.asList("先聽完對方需求", "拆解小步驟", "保留彈性"),
                Arrays.asList("情緒化決定", "承諾過多")));
        list.set(28, new Hexagram(29, "坎", "坎為水", "坎 / 水", "坎 / 水",
                new boolean[]{false, true, false, false, true, false},
                Arrays.asList("險陷", "誠信", "冷靜應對"),
                "習坎，有孚，維心亨，行有尚。",
                "重重險陷，只要內心誠信，就能通達；採取行動將會獲得獎賞。今日宜保持內心平靜，勇敢面對挑戰。",
                "坎卦代表水，水性向下流動，遇險則陷，也象徵智慧與柔韌。",
                "習坎，有孚，維心亨，行有尚。",
                "這是一個需要耐心與毅力的時刻。與其強行突破，不如靜下心來觀察局勢，將困境視為磨練心性的道場。",
                Arrays.asList("堅守信念", "冷靜應對"),
                Arrays.asList("輕舉妄動", "心生恐懼")));
        return Collections.unmodifiableList(list);
    }

    private static String upperOf(String fullName) {
        return fullName.length() >= 1 ? fullName.substring(0, 1) : "上";
    }

    private static String lowerOf(String fullName) {
        return fullName.length() >= 2 ? fullName.substring(fullName.length() - 1) : "下";
    }
}
