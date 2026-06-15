package fcu.app.i_ching.data;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class LocalRecordStoreFilterTest {
    @Test
    public void querySearchesQuestionNoteHexagramAndTags() {
        List<DivinationRecord> records = records();

        assertEquals(1, LocalRecordStore.filter(records, "工作節奏", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records, "整理需求", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records, "地山謙", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records, "吉卦", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records, "豐盛", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records, "重重險陷", null, LocalRecordStore.ChangeFilter.ALL).size());
    }

    @Test
    public void methodFilterNarrowsRecords() {
        assertEquals(1, LocalRecordStore.filter(records(), "", DivinationMethod.COINS, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records(), "", DivinationMethod.SIMPLE, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(0, LocalRecordStore.filter(records(), "", DivinationMethod.YARROW, LocalRecordStore.ChangeFilter.ALL).size());
    }

    @Test
    public void changingFilterNarrowsRecords() {
        assertEquals(2, LocalRecordStore.filter(records(), "", null, LocalRecordStore.ChangeFilter.ALL).size());
        assertEquals(1, LocalRecordStore.filter(records(), "", null, LocalRecordStore.ChangeFilter.WITH_CHANGES).size());
        assertEquals(1, LocalRecordStore.filter(records(), "", null, LocalRecordStore.ChangeFilter.WITHOUT_CHANGES).size());
    }

    private List<DivinationRecord> records() {
        return Arrays.asList(
                new DivinationRecord(1L, "工作節奏是否要調整？", 15, 55, DivinationMethod.COINS,
                        new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4), 1L, "先整理需求"),
                new DivinationRecord(2L, "旅行前要注意什麼？", 29, 29, DivinationMethod.SIMPLE,
                        new int[]{8, 7, 8, 8, 7, 8}, Collections.emptyList(), 2L, "保持冷靜")
        );
    }
}
