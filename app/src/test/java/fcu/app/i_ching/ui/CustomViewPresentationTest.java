package fcu.app.i_ching.ui;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class CustomViewPresentationTest {
    @Test
    public void hexagramViewTracksChangingLineHighlights() {
        boolean[] mask = HexagramView.changingLineMaskForTest(Arrays.asList(1, 4, 9, null));

        assertTrue(mask[0]);
        assertTrue(mask[3]);
        assertFalse(mask[1]);
        assertArrayEquals(new boolean[]{false, false, false, false, false, false},
                HexagramView.changingLineMaskForTest(null));
    }

    @Test
    public void ritualFocusProgressIsClamped() {
        assertEquals(0f, RitualFocusView.clampProgressForTest(-1f), 0.001f);
        assertEquals(0.5f, RitualFocusView.clampProgressForTest(0.5f), 0.001f);
        assertEquals(1f, RitualFocusView.clampProgressForTest(2f), 0.001f);
    }
}
