package fcu.app.i_ching.data;

import org.junit.Test;

import java.util.Random;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class YarrowCastingSessionTest {
    @Test
    public void lineValueBucketsMatchTraditionalYarrowDistribution() {
        int six = 0;
        int seven = 0;
        int eight = 0;
        int nine = 0;
        for (int roll = 0; roll < 16; roll++) {
            int value = YarrowCastingSession.lineValueFromRoll(roll);
            if (value == 6) six++;
            if (value == 7) seven++;
            if (value == 8) eight++;
            if (value == 9) nine++;
        }

        assertEquals(1, six);
        assertEquals(5, seven);
        assertEquals(7, eight);
        assertEquals(3, nine);
    }

    @Test
    public void sessionAdvancesThroughEighteenChangesAndSixLines() {
        YarrowCastingSession session = new YarrowCastingSession(new SequenceRandom(0, 5, 12, 15, 1, 13));

        for (int i = 1; i <= YarrowCastingSession.TOTAL_CHANGES; i++) {
            YarrowCastingSession.Step step = session.completeNextChange();
            assertEquals(i, step.completedChanges);
            if (i % 3 == 0) {
                assertTrue(step.lineCompleted);
                assertTrue(step.completedLineValue >= 6 && step.completedLineValue <= 9);
            } else {
                assertFalse(step.lineCompleted);
            }
        }

        assertTrue(session.isComplete());
        assertArrayEquals(new int[]{6, 7, 8, 9, 7, 9}, session.lineValues());
    }

    @Test
    public void quickCompleteProducesRepositoryConsistentResult() {
        YarrowCastingSession session = new YarrowCastingSession(new SequenceRandom(0, 5, 12, 15, 1, 13));

        session.quickComplete();
        DivinationResult result = session.result("近期自我成長的重點");

        assertEquals(DivinationMethod.YARROW, result.method);
        assertEquals(HexagramRepository.fromLineValues(result.lineValues).number, result.hexagram.number);
        assertEquals(HexagramRepository.relatingFromLineValues(result.lineValues).number, result.relatingHexagramNumber);
    }

    private static class SequenceRandom extends Random {
        private final int[] values;
        private int index;

        SequenceRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            int value = values[index % values.length];
            index++;
            return Math.floorMod(value, bound);
        }
    }
}
