package fcu.app.i_ching.data;

import org.json.JSONObject;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class DivinationPersistenceTest {
    @Test
    public void divinationResultRoundTripPreservesSnapshot() throws Exception {
        DivinationResult result = sampleResult();

        DivinationResult restored = DivinationResult.fromJsonString(result.toJsonString());

        assertEquals("是否適合調整工作節奏？", restored.question);
        assertEquals(DivinationMethod.COINS, restored.method);
        assertEquals(15, restored.hexagram.number);
        assertArrayEquals(new int[]{6, 7, 8, 9, 7, 8}, restored.lineValues);
        assertEquals(Arrays.asList(1, 4), restored.changingLines);
        assertEquals(123456789L, restored.createdAt);
    }

    @Test
    public void divinationRecordRoundTripPreservesResultAndNote() throws Exception {
        DivinationRecord record = DivinationRecord.fromResult(sampleResult(), "先整理需求");

        DivinationRecord restored = DivinationRecord.fromJson(record.toJson());

        assertEquals(123456789L, restored.id);
        assertEquals("是否適合調整工作節奏？", restored.question);
        assertEquals(15, restored.hexagramNumber);
        assertEquals(DivinationMethod.COINS, restored.method);
        assertArrayEquals(new int[]{6, 7, 8, 9, 7, 8}, restored.lineValues);
        assertEquals(Arrays.asList(1, 4), restored.changingLines);
        assertEquals(123456789L, restored.createdAt);
        assertEquals("先整理需求", restored.note);
    }

    @Test
    public void oldRecordJsonFallsBackForNewSnapshotFields() throws Exception {
        JSONObject oldJson = new JSONObject();
        oldJson.put("id", 77L);
        oldJson.put("question", "舊紀錄");
        oldJson.put("hexagramNumber", 29);
        oldJson.put("method", "UNKNOWN");
        oldJson.put("createdAt", 66L);
        oldJson.put("note", "舊筆記");

        DivinationRecord restored = DivinationRecord.fromJson(oldJson);

        assertEquals(77L, restored.id);
        assertEquals("舊紀錄", restored.question);
        assertEquals(29, restored.hexagramNumber);
        assertEquals(DivinationMethod.COINS, restored.method);
        assertArrayEquals(new int[0], restored.lineValues);
        assertTrue(restored.changingLines.isEmpty());
        assertEquals(66L, restored.createdAt);
        assertEquals("舊筆記", restored.note);
    }

    @Test
    public void recordUpsertUpdateAndDeleteUseStableIds() {
        DivinationResult result = sampleResult();
        List<DivinationRecord> records = new ArrayList<>();

        records = LocalRecordStore.upsert(records, DivinationRecord.fromResult(result, ""));
        records = LocalRecordStore.upsert(records, DivinationRecord.fromResult(result, "補上筆記"));

        assertEquals(1, records.size());
        assertEquals(123456789L, records.get(0).id);
        assertEquals("補上筆記", records.get(0).note);
        assertNotNull(LocalRecordStore.find(records, 123456789L));

        assertTrue(LocalRecordStore.updateNote(records, 123456789L, "再次更新"));
        assertEquals("再次更新", records.get(0).note);

        assertTrue(LocalRecordStore.delete(records, 123456789L));
        assertTrue(records.isEmpty());
        assertNull(LocalRecordStore.find(records, 123456789L));
    }

    private DivinationResult sampleResult() {
        return new DivinationResult(
                "是否適合調整工作節奏？",
                DivinationMethod.COINS,
                HexagramRepository.get(15),
                new int[]{6, 7, 8, 9, 7, 8},
                Arrays.asList(1, 4),
                123456789L
        );
    }
}
