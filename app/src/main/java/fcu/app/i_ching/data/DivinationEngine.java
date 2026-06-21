package fcu.app.i_ching.data;

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
        boolean[] lines = HexagramRepository.linesFromValues(lineValues);
        List<Integer> changing = HexagramRepository.changingLinesFromValues(lineValues);
        Hexagram hexagram = HexagramRepository.fromLines(lines);
        Hexagram relating = HexagramRepository.relatingFrom(lines, changing);
        return new DivinationResult(question, method, hexagram, relating, lineValues, changing, System.currentTimeMillis());
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
        return YarrowCastingSession.lineValueFromRoll(roll);
    }
}
