package fcu.app.i_ching.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class RecordRepositoryTest {
    @Test
    public void entityMapperPreservesRecordFields() {
        DivinationRecord record = sampleRecord();

        DivinationRecord restored = DivinationRecordEntity.fromRecord(record).toRecord();

        assertEquals(record.id, restored.id);
        assertEquals(record.question, restored.question);
        assertEquals(record.hexagramNumber, restored.hexagramNumber);
        assertEquals(record.relatingHexagramNumber, restored.relatingHexagramNumber);
        assertEquals(record.method, restored.method);
        assertArrayEquals(record.lineValues, restored.lineValues);
        assertEquals(record.changingLines, restored.changingLines);
        assertEquals(record.createdAt, restored.createdAt);
        assertEquals(record.note, restored.note);
    }

    @Test
    public void exportJsonReturnsPortableRecordArray() throws Exception {
        String json = RecordRepository.exportJson(Collections.singletonList(sampleRecord()));

        JSONArray array = new JSONArray(json);

        assertEquals(1, array.length());
        JSONObject object = array.getJSONObject(0);
        assertEquals(7L, object.getLong("id"));
        assertEquals("是否適合調整工作節奏？", object.getString("question"));
        assertEquals(15, object.getInt("hexagramNumber"));
        assertEquals(55, object.getInt("relatingHexagramNumber"));
        assertEquals("COINS", object.getString("method"));
        assertEquals("先整理需求", object.getString("note"));
    }

    @Test
    public void exportTextIncludesReadableCoreFields() {
        String text = RecordRepository.exportText(Collections.singletonList(sampleRecord()));

        assertTrue(text.contains("易經占卜紀錄"));
        assertTrue(text.contains("問題：是否適合調整工作節奏？"));
        assertTrue(text.contains("本卦：第15卦｜地山謙"));
        assertTrue(text.contains("之卦：第55卦｜雷火豐"));
        assertTrue(text.contains("變爻：1、4"));
        assertTrue(text.contains("筆記：先整理需求"));
    }

    @Test
    public void legacyParserKeepsBackwardCompatibleDerivation() throws Exception {
        JSONObject oldJson = new JSONObject();
        oldJson.put("id", 88L);
        oldJson.put("question", "舊紀錄含爻值");
        oldJson.put("hexagramNumber", 15);
        oldJson.put("method", "COINS");
        oldJson.put("lineValues", DivinationResult.intArrayToJson(new int[]{6, 8, 7, 6, 8, 8}));
        oldJson.put("createdAt", 88L);
        JSONArray array = new JSONArray();
        array.put(oldJson);

        List<DivinationRecord> records = RecordRepository.parseLegacyRecords(array.toString());

        assertEquals(1, records.size());
        assertEquals(55, records.get(0).relatingHexagramNumber);
        assertEquals(Arrays.asList(1, 4), records.get(0).changingLines);
    }

    private DivinationRecord sampleRecord() {
        return new DivinationRecord(7L, "是否適合調整工作節奏？", 15, 55, DivinationMethod.COINS,
                new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4), 7L, "先整理需求");
    }
}
