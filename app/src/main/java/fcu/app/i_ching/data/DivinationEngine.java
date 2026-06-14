package fcu.app.i_ching.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DivinationEngine {
    private final Random random;

    public DivinationEngine() {
        this(new Random());
    }

    public DivinationEngine(Random random) {
        this.random = random;
    }

    public DivinationResult cast(String question, DivinationMethod method) {
        int[] lineValues = castLineValues(method);
        boolean[] lines = new boolean[6];
        List<Integer> changing = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            int value = lineValues[i];
            lines[i] = value == 7 || value == 9;
            if (value == 6 || value == 9) {
                changing.add(i + 1);
            }
        }
        Hexagram hexagram = method == DivinationMethod.SIMPLE
                ? HexagramRepository.get(random.nextInt(64) + 1)
                : HexagramRepository.fromLines(lines);
        return new DivinationResult(question, method, hexagram, lineValues, changing, System.currentTimeMillis());
    }

    public int[] castLineValues(DivinationMethod method) {
        int[] values = new int[6];
        for (int i = 0; i < 6; i++) {
            values[i] = castLineValue(method);
        }
        return values;
    }

    public int castLineValue(DivinationMethod method) {
        if (method == DivinationMethod.SIMPLE) {
            return random.nextBoolean() ? 7 : 8;
        }
        int roll = random.nextInt(16);
        if (method == DivinationMethod.COINS) {
            if (roll == 0) return 6;
            if (roll <= 3) return 9;
            if (roll <= 8) return 7;
            return 8;
        }
        if (roll == 0) return 6;
        if (roll <= 4) return 9;
        if (roll <= 11) return 7;
        return 8;
    }
}
