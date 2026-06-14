package fcu.app.i_ching;

import org.junit.Test;

import java.util.Random;

import fcu.app.i_ching.data.DivinationEngine;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

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
    public void yarrowLineValuesUseSlowMethodBuckets() {
        assertEquals(6, new DivinationEngine(new FixedRandom(0)).castLineValue(DivinationMethod.YARROW));
        assertEquals(9, new DivinationEngine(new FixedRandom(4)).castLineValue(DivinationMethod.YARROW));
        assertEquals(7, new DivinationEngine(new FixedRandom(11)).castLineValue(DivinationMethod.YARROW));
        assertEquals(8, new DivinationEngine(new FixedRandom(15)).castLineValue(DivinationMethod.YARROW));
    }

    @Test
    public void modestyPatternMapsToHexagramFifteen() {
        Hexagram modesty = HexagramRepository.fromLines(new boolean[]{false, false, true, false, false, false});
        assertEquals(15, modesty.number);
        assertEquals("地山謙", modesty.fullName);
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
