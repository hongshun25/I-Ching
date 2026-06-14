package fcu.app.i_ching.data;

public enum Trigram {
    QIAN("乾", "天"), DUI("兌", "澤"), LI("離", "火"), ZHEN("震", "雷"),
    XUN("巽", "風"), KAN("坎", "水"), GEN("艮", "山"), KUN("坤", "地");

    public final String name;
    public final String image;

    Trigram(String name, String image) {
        this.name = name;
        this.image = image;
    }
}
