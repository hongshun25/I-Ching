package fcu.app.i_ching.data;

import java.util.ArrayList;
import java.util.List;

public class DivinationResult {
    public final String question;
    public final DivinationMethod method;
    public final Hexagram hexagram;
    public final int[] lineValues;
    public final List<Integer> changingLines;
    public final long createdAt;

    public DivinationResult(String question, DivinationMethod method, Hexagram hexagram, int[] lineValues,
                            List<Integer> changingLines, long createdAt) {
        this.question = question;
        this.method = method;
        this.hexagram = hexagram;
        this.lineValues = lineValues;
        this.changingLines = new ArrayList<>(changingLines);
        this.createdAt = createdAt;
    }

    public static DivinationResult create(String question, DivinationMethod method) {
        return new DivinationEngine().cast(question, method);
    }
}
