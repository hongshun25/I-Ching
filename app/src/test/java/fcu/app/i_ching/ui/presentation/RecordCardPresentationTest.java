package fcu.app.i_ching.ui.presentation;

import org.junit.Test;

import java.util.Arrays;
import java.util.TimeZone;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RecordCardPresentationTest {
    @Test
    public void recordCardIncludesRelationChangingLinesAndActionLabels() {
        TimeZone original = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Taipei"));
        try {
            DivinationRecord record = new DivinationRecord(7L, "是否適合調整工作節奏？", 15, 55,
                    DivinationMethod.COINS, new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4),
                    7L, "先整理需求");

            RecordCardPresentation presentation = RecordCardPresentation.from(record);

            assertEquals("第15卦｜地山謙", presentation.titleText);
            assertEquals("本卦 → 之卦｜第15卦 謙 → 第55卦 豐", presentation.relationText);
            assertEquals("變爻 1、4", presentation.changingText);
            assertEquals("三枚銅錢 · 1970/01/01 08:00", presentation.metaText);
            assertTrue(presentation.hasNote());
            assertEquals("編輯第15卦紀錄筆記", presentation.editContentDescription);
            assertEquals("刪除第15卦紀錄", presentation.deleteContentDescription);
        } finally {
            TimeZone.setDefault(original);
        }
    }

    @Test
    public void recordCardHandlesStaticRecordWithoutNote() {
        DivinationRecord record = new DivinationRecord(8L, "今天如何安頓自己？", 15, 15,
                DivinationMethod.SIMPLE, new int[]{8, 8, 7, 8, 8, 8}, Arrays.asList(),
                8L, "");

        RecordCardPresentation presentation = RecordCardPresentation.from(record);

        assertEquals("本卦即之卦｜第15卦 地山謙", presentation.relationText);
        assertEquals("無變爻", presentation.changingText);
        assertFalse(presentation.hasNote());
    }
}
