package fcu.app.i_ching.data;

public enum Trigram {
    QIAN("乾", "天", new boolean[]{true, true, true}),
    DUI("兌", "澤", new boolean[]{true, true, false}),
    LI("離", "火", new boolean[]{true, false, true}),
    ZHEN("震", "雷", new boolean[]{true, false, false}),
    XUN("巽", "風", new boolean[]{false, true, true}),
    KAN("坎", "水", new boolean[]{false, true, false}),
    GEN("艮", "山", new boolean[]{false, false, true}),
    KUN("坤", "地", new boolean[]{false, false, false});

    public final String name;
    public final String image;
    private final boolean[] linesBottomToTop;

    Trigram(String name, String image, boolean[] linesBottomToTop) {
        this.name = name;
        this.image = image;
        this.linesBottomToTop = linesBottomToTop;
    }

    public String displayName() {
        return name + " / " + image;
    }

    public boolean[] linesBottomToTop() {
        return linesBottomToTop.clone();
    }
}
