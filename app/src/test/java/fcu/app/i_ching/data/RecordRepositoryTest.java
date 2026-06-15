package fcu.app.i_ching.data;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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

    @Test
    public void exportedSchemaPreservesVersionOneRecordTable() throws Exception {
        JSONObject schema = new JSONObject(new String(Files.readAllBytes(schemaFile().toPath()), StandardCharsets.UTF_8));
        JSONObject database = schema.getJSONObject("database");
        JSONObject entity = database.getJSONArray("entities").getJSONObject(0);

        assertEquals(1, database.getInt("version"));
        assertEquals("divination_records", entity.getString("tableName"));
        assertTrue(entity.getString("createSql").contains("`relatingHexagramNumber` INTEGER NOT NULL"));
        assertTrue(entity.getString("createSql").contains("`lineValues` TEXT NOT NULL"));
        assertTrue(entity.getString("createSql").contains("`changingLines` TEXT NOT NULL"));
    }

    @Test
    public void asyncExportJsonUsesCallback() throws Exception {
        FakeDao dao = new FakeDao(Collections.singletonList(DivinationRecordEntity.fromRecord(sampleRecord())));
        RecordRepository repository = new RecordRepository(dao, null, null, AppExecutors.direct());
        final String[] callbackValue = new String[1];

        repository.exportJson(value -> callbackValue[0] = value);

        JSONArray array = new JSONArray(callbackValue[0]);
        assertEquals(1, array.length());
        assertEquals(7L, array.getJSONObject(0).getLong("id"));
    }

    private DivinationRecord sampleRecord() {
        return new DivinationRecord(7L, "是否適合調整工作節奏？", 15, 55, DivinationMethod.COINS,
                new int[]{6, 8, 7, 6, 8, 8}, Arrays.asList(1, 4), 7L, "先整理需求");
    }

    private File schemaFile() {
        File rootPath = new File("app/schemas/fcu.app.i_ching.data.IChingDatabase/1.json");
        if (rootPath.isFile()) return rootPath;
        return new File("schemas/fcu.app.i_ching.data.IChingDatabase/1.json");
    }

    private static class FakeDao implements DivinationRecordDao {
        private final List<DivinationRecordEntity> records;

        FakeDao(List<DivinationRecordEntity> records) {
            this.records = records;
        }

        @Override
        public LiveData<List<DivinationRecordEntity>> records() {
            return null;
        }

        @Override
        public List<DivinationRecordEntity> recordsNow() {
            return records;
        }

        @Override
        public DivinationRecordEntity find(long id) {
            for (DivinationRecordEntity entity : records) {
                if (entity.id == id) return entity;
            }
            return null;
        }

        @Override
        public void upsert(DivinationRecordEntity record) {
        }

        @Override
        public int updateNote(long id, String note) {
            return find(id) == null ? 0 : 1;
        }

        @Override
        public int deleteById(long id) {
            return find(id) == null ? 0 : 1;
        }

        @Override
        public void deleteAll() {
        }
    }
}
