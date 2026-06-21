package fcu.app.i_ching;

import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import fcu.app.i_ching.data.DivinationEngine;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IChingLogicTest {
    @Test
    public void coinLineValuesUseTraditionalBuckets() {
        assertEquals(6, new DivinationEngine(new FixedRandom(0)).castLineValue(DivinationMethod.COINS));
        assertEquals(9, new DivinationEngine(new FixedRandom(3)).castLineValue(DivinationMethod.COINS));
        assertEquals(7, new DivinationEngine(new FixedRandom(8)).castLineValue(DivinationMethod.COINS));
        assertEquals(8, new DivinationEngine(new FixedRandom(15)).castLineValue(DivinationMethod.COINS));
    }

    @Test
    public void yarrowLineValuesUseTraditionalBuckets() {
        assertEquals(6, new DivinationEngine(new FixedRandom(0)).castLineValue(DivinationMethod.YARROW));
        assertEquals(7, new DivinationEngine(new FixedRandom(5)).castLineValue(DivinationMethod.YARROW));
        assertEquals(8, new DivinationEngine(new FixedRandom(12)).castLineValue(DivinationMethod.YARROW));
        assertEquals(9, new DivinationEngine(new FixedRandom(15)).castLineValue(DivinationMethod.YARROW));
    }

    @Test
    public void modestyPatternMapsToHexagramFifteen() {
        Hexagram modesty = HexagramRepository.fromLines(new boolean[]{false, false, true, false, false, false});
        assertEquals(15, modesty.number);
        assertEquals("地山謙", modesty.fullName);
    }

    @Test
    public void knownPatternsMapToCanonicalHexagrams() {
        assertEquals(1, HexagramRepository.fromLines(new boolean[]{true, true, true, true, true, true}).number);
        assertEquals(2, HexagramRepository.fromLines(new boolean[]{false, false, false, false, false, false}).number);
        assertEquals(15, HexagramRepository.fromLines(new boolean[]{false, false, true, false, false, false}).number);
        assertEquals(29, HexagramRepository.fromLines(new boolean[]{false, true, false, false, true, false}).number);
        assertEquals(63, HexagramRepository.fromLines(new boolean[]{true, false, true, false, true, false}).number);
        assertEquals(64, HexagramRepository.fromLines(new boolean[]{false, true, false, true, false, true}).number);
    }

    @Test
    public void allPatternsAreUniqueAndRoundTripThroughRepository() {
        Set<String> patterns = new HashSet<>();
        for (Hexagram hexagram : HexagramRepository.all()) {
            assertEquals(6, hexagram.linesBottomToTop.length);
            assertEquals(6, hexagram.lineTexts.size());
            assertTrue(patterns.add(Arrays.toString(hexagram.linesBottomToTop)));
            assertEquals(hexagram.number, HexagramRepository.fromLines(hexagram.linesBottomToTop).number);
        }
        assertEquals(64, patterns.size());
    }

    @Test
    public void invalidLineInputFallsBackToDesignSafeHexagram() {
        assertEquals(15, HexagramRepository.fromLines(new boolean[]{true, false, true}).number);
        assertEquals(15, HexagramRepository.fromLines(null).number);
    }

    @Test
    public void simpleCastUsesItsGeneratedStaticLines() {
        DivinationResult result = new DivinationEngine(new FixedRandom(0)).cast("今天如何安排？", DivinationMethod.SIMPLE);

        assertArrayEquals(new int[]{7, 7, 7, 7, 7, 7}, result.lineValues);
        assertTrue(result.changingLines.isEmpty());
        assertEquals(HexagramRepository.fromLineValues(result.lineValues).number, result.hexagram.number);
        assertEquals(result.hexagram.number, result.relatingHexagramNumber);
    }

    @Test
    public void changingLinesFlipIntoRelatingHexagram() {
        int[] values = new int[]{6, 8, 7, 6, 8, 8};
        List<Integer> changing = HexagramRepository.changingLinesFromValues(values);

        assertEquals(Arrays.asList(1, 4), changing);
        assertEquals(15, HexagramRepository.fromLineValues(values).number);
        assertEquals(55, HexagramRepository.relatingFromLineValues(values).number);
    }

    @Test
    public void changingCoinAndYarrowBucketsProduceRelatingHexagram() {
        assertEquals(1, new DivinationEngine(new FixedRandom(0)).cast("全變", DivinationMethod.COINS).relatingHexagramNumber);
        assertEquals(1, new DivinationEngine(new FixedRandom(0)).cast("全變", DivinationMethod.YARROW).relatingHexagramNumber);
    }

    @Test
    public void repositoryIncludesAllSixtyFourHexagrams() {
        assertEquals(64, HexagramRepository.all().size());
        assertFalse(HexagramRepository.all().get(0).tags.isEmpty());
        assertTrue(HexagramRepository.get(29).summary.contains("重重險陷"));
    }

    private static class FixedRandom extends Random {
        private final int value;
        FixedRandom(int value) { this.value = value; }
        @Override public int nextInt(int bound) { return value; }
        @Override public boolean nextBoolean() { return value % 2 == 0; }
    }
}
