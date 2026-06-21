package fcu.app.i_ching.ui.presentation;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.HexagramRepository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ResultPresentationTest {
    @Test
    public void changedLinesUseTraditionalPositionNamesAndLineText() {
        DivinationResult result = new DivinationResult(
                "是否適合調整工作節奏？",
                DivinationMethod.COINS,
                HexagramRepository.get(15),
                HexagramRepository.get(55),
                new int[]{6, 8, 7, 6, 8, 8},
                Arrays.asList(1, 4),
                7L
        );

        ResultPresentation presentation = ResultPresentation.from(result);

        assertEquals("變爻：初爻、四爻", presentation.changingSummary);
        assertTrue(presentation.changedLineText.contains("初六"));
        assertTrue(presentation.changedLineText.contains("六四"));
        assertTrue(presentation.blindSpotText.contains("變爻顯示轉折"));
        assertTrue(presentation.shareText.contains("本卦：第15卦｜地山謙"));
        assertTrue(presentation.shareText.contains("之卦：第55卦｜雷火豐"));
    }

    @Test
    public void staticResultStatesNoChangingLines() {
        DivinationResult result = new DivinationResult(
                "今天如何安頓自己？",
                DivinationMethod.SIMPLE,
                HexagramRepository.get(15),
                HexagramRepository.get(15),
                new int[]{8, 8, 7, 8, 8, 8},
                Collections.emptyList(),
                8L
        );

        ResultPresentation presentation = ResultPresentation.from(result);

        assertEquals("本次無變爻，之卦與本卦相同。", presentation.changingSummary);
        assertEquals("", presentation.changedLineText);
        assertTrue(presentation.blindSpotText.contains("穩定"));
        assertTrue(presentation.shareText.contains("本次無變爻，之卦與本卦相同。"));
    }
}
