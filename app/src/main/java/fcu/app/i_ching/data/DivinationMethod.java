package fcu.app.i_ching.data;

public enum DivinationMethod {
    SIMPLE("簡易占法"),
    COINS("三枚銅錢"),
    YARROW("蓍草靈感模式");

    public final String label;

    DivinationMethod(String label) {
        this.label = label;
    }
}
