package fcu.app.i_ching.data;

public class HexagramLine {
    public final int position;
    public final String label;
    public final String text;
    public final String modernHint;

    public HexagramLine(int position, String label, String text, String modernHint) {
        this.position = position;
        this.label = label;
        this.text = text;
        this.modernHint = modernHint;
    }

    public String classicalText() {
        return label + "，" + text;
    }
}
