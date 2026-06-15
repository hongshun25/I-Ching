package fcu.app.i_ching;

import android.os.Bundle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.util.Collections;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 34)
public class NavigationArgsTest {
    @Test
    public void methodArgsNormalizeBlankQuestion() {
        Bundle args = NavigationArgs.method("  ");

        assertEquals(NavigationArgs.DEFAULT_QUESTION, NavigationArgs.question(args));
    }

    @Test
    public void ritualArgsFallbackToCoinsForMissingMethod() {
        Bundle args = NavigationArgs.method("想確認接下來的節奏");

        assertEquals(DivinationMethod.COINS, NavigationArgs.method(args));
    }

    @Test
    public void resultArgsRoundTripJsonSnapshot() {
        Hexagram hexagram = HexagramRepository.get(15);
        DivinationResult result = new DivinationResult(
                "是否適合調整工作節奏？",
                DivinationMethod.SIMPLE,
                hexagram,
                hexagram,
                new int[]{8, 8, 7, 8, 8, 8},
                Collections.emptyList(),
                1234L
        );

        DivinationResult restored = NavigationArgs.result(NavigationArgs.result(result));

        assertEquals(result.question, restored.question);
        assertEquals(result.method, restored.method);
        assertEquals(result.hexagram.number, restored.hexagram.number);
        assertEquals(result.relatingHexagramNumber, restored.relatingHexagramNumber);
        assertEquals(result.createdAt, restored.createdAt);
    }

    @Test
    public void hexagramAndRecordIdUseStableFallbacks() {
        assertEquals(15, NavigationArgs.hexagramNumber(null));
        assertEquals(NavigationArgs.NO_RECORD_ID, NavigationArgs.recordId(null));

        Bundle args = NavigationArgs.hexagramDetail(29);
        NavigationArgs.putRecordId(args, 77L);

        assertEquals(29, NavigationArgs.hexagramNumber(args));
        assertEquals(77L, NavigationArgs.recordId(args));
    }
}
