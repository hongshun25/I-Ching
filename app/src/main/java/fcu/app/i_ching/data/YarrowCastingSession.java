package fcu.app.i_ching.data;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class YarrowCastingSession {
    public static final int TOTAL_LINES = 6;
    public static final int CHANGES_PER_LINE = 3;
    public static final int TOTAL_CHANGES = TOTAL_LINES * CHANGES_PER_LINE;

    private static final int STARTING_STALKS = 49;
    private static final YarrowPattern[] PATTERNS = new YarrowPattern[]{
            new YarrowPattern(6, new int[]{9, 8, 8}),
            new YarrowPattern(7, new int[]{5, 8, 8}),
            new YarrowPattern(7, new int[]{9, 4, 8}),
            new YarrowPattern(7, new int[]{9, 8, 4}),
            new YarrowPattern(7, new int[]{5, 8, 8}),
            new YarrowPattern(7, new int[]{5, 8, 8}),
            new YarrowPattern(8, new int[]{5, 4, 8}),
            new YarrowPattern(8, new int[]{5, 8, 4}),
            new YarrowPattern(8, new int[]{9, 4, 4}),
            new YarrowPattern(8, new int[]{5, 4, 8}),
            new YarrowPattern(8, new int[]{5, 4, 8}),
            new YarrowPattern(8, new int[]{5, 8, 4}),
            new YarrowPattern(8, new int[]{5, 8, 4}),
            new YarrowPattern(9, new int[]{5, 4, 4}),
            new YarrowPattern(9, new int[]{5, 4, 4}),
            new YarrowPattern(9, new int[]{5, 4, 4})
    };

    public static class Step {
        public final int lineNumber;
        public final int changeNumber;
        public final int removedStalks;
        public final int remainingStalks;
        public final int completedChanges;
        public final boolean lineCompleted;
        public final int completedLineValue;

        Step(int lineNumber, int changeNumber, int removedStalks, int remainingStalks,
             int completedChanges, boolean lineCompleted, int completedLineValue) {
            this.lineNumber = lineNumber;
            this.changeNumber = changeNumber;
            this.removedStalks = removedStalks;
            this.remainingStalks = remainingStalks;
            this.completedChanges = completedChanges;
            this.lineCompleted = lineCompleted;
            this.completedLineValue = completedLineValue;
        }
    }

    private final Random random;
    private final int[] lineValues = new int[TOTAL_LINES];
    private int currentLine;
    private int currentChange;
    private int currentRemaining = STARTING_STALKS;
    private YarrowPattern pendingPattern;

    public YarrowCastingSession() {
        this(new Random());
    }

    public YarrowCastingSession(Random random) {
        this.random = random == null ? new Random() : random;
    }

    public Step completeNextChange() {
        if (isComplete()) {
            return lastStep();
        }
        if (currentChange == 0 || pendingPattern == null) {
            pendingPattern = patternFromRoll(random.nextInt(PATTERNS.length));
        }
        int removed = pendingPattern.removedStalks[currentChange];
        currentRemaining -= removed;
        int lineNumber = currentLine + 1;
        int changeNumber = currentChange + 1;
        boolean lineCompleted = currentChange == CHANGES_PER_LINE - 1;
        int completedLineValue = 0;
        if (lineCompleted) {
            completedLineValue = pendingPattern.lineValue;
            lineValues[currentLine] = completedLineValue;
            currentLine++;
            currentChange = 0;
            currentRemaining = STARTING_STALKS;
            pendingPattern = null;
        } else {
            currentChange++;
        }
        return new Step(
                lineNumber,
                changeNumber,
                removed,
                lineCompleted ? completedLineValue * 4 : currentRemaining,
                completedChanges(),
                lineCompleted,
                completedLineValue
        );
    }

    public void quickComplete() {
        while (!isComplete()) {
            completeNextChange();
        }
    }

    public boolean isComplete() {
        return currentLine >= TOTAL_LINES;
    }

    public int completedChanges() {
        return (currentLine * CHANGES_PER_LINE) + currentChange;
    }

    public int currentLineNumber() {
        return Math.min(currentLine + 1, TOTAL_LINES);
    }

    public int currentChangeNumber() {
        return isComplete() ? CHANGES_PER_LINE : currentChange + 1;
    }

    public int[] lineValues() {
        return Arrays.copyOf(lineValues, lineValues.length);
    }

    public List<Integer> changingLines() {
        return HexagramRepository.changingLinesFromValues(lineValues());
    }

    public DivinationResult result(String question) {
        if (!isComplete()) {
            throw new IllegalStateException("Yarrow session is not complete");
        }
        return DivinationResult.fromLineValues(question, DivinationMethod.YARROW, lineValues(), System.currentTimeMillis());
    }

    public static int lineValueFromRoll(int roll) {
        return patternFromRoll(roll).lineValue;
    }

    private Step lastStep() {
        return new Step(TOTAL_LINES, CHANGES_PER_LINE, 0, lineValues[TOTAL_LINES - 1] * 4,
                TOTAL_CHANGES, true, lineValues[TOTAL_LINES - 1]);
    }

    private static YarrowPattern patternFromRoll(int roll) {
        return PATTERNS[Math.floorMod(roll, PATTERNS.length)];
    }

    private static class YarrowPattern {
        final int lineValue;
        final int[] removedStalks;

        YarrowPattern(int lineValue, int[] removedStalks) {
            this.lineValue = lineValue;
            this.removedStalks = removedStalks;
        }
    }
}
