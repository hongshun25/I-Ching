package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import androidx.lifecycle.LiveData;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class RecordRepositoryTest {
    @Test
    public void entityMapperPreservesRecordFields() {
        DivinationRecord record = sampleRecord();

        DivinationRecord restored = DivinationRecordEntity.fromRecord(record).toRecord();

        assertEquals(AccountStore.GUEST_ACCOUNT_ID, DivinationRecordEntity.fromRecord(record).accountId);
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
    public void exportEmptyAndNullRecordsUseStableFallbackContent() {
        assertEquals("[]", RecordRepository.exportJson(Collections.emptyList()));
        assertEquals("[]", RecordRepository.exportJson((List<DivinationRecord>) null));

        assertEquals("易經占卜紀錄\n目前沒有占卜紀錄。\n", RecordRepository.exportText(Collections.emptyList()));
        assertEquals("易經占卜紀錄\n目前沒有占卜紀錄。\n", RecordRepository.exportText((List<DivinationRecord>) null));
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
    public void legacyMigrationImportsPrefsOnceAndSetsFlag() throws Exception {
        Context context = RuntimeEnvironment.getApplication();
        SharedPreferences legacyPrefs = context.getSharedPreferences("record_repository_test_legacy", Context.MODE_PRIVATE);
        SharedPreferences migrationPrefs = context.getSharedPreferences("record_repository_test_migration", Context.MODE_PRIVATE);
        legacyPrefs.edit().clear().commit();
        migrationPrefs.edit().clear().commit();

        JSONArray legacyRecords = new JSONArray();
        legacyRecords.put(sampleRecord().toJson());
        legacyPrefs.edit().putString("records", legacyRecords.toString()).commit();

        FakeDao dao = new FakeDao(Collections.emptyList());
        RecordRepository repository = new RecordRepository(dao, legacyPrefs, migrationPrefs, AppExecutors.direct());

        repository.migrateFromLegacyPrefsIfNeeded();

        assertTrue(migrationPrefs.getBoolean("roomMigrated", false));
        assertEquals(1, dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).size());
        assertEquals(7L, dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).get(0).id);
        assertEquals(1, dao.upsertCount);

        repository.migrateFromLegacyPrefsIfNeeded();

        assertEquals(1, dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).size());
        assertEquals(1, dao.upsertCount);
    }

    @Test
    public void repositoryOperationsUseCurrentAccountOnly() {
        FakeDao dao = new FakeDao(Arrays.asList(
                DivinationRecordEntity.fromRecord(sampleRecord(), AccountStore.GUEST_ACCOUNT_ID),
                DivinationRecordEntity.fromRecord(new DivinationRecord(7L, "帳號紀錄", 29, 29,
                        DivinationMethod.SIMPLE, new int[]{8, 7, 8, 8, 7, 8}, Collections.emptyList(),
                        8L, ""), "account-a")
        ));
        RecordRepository repository = new RecordRepository(
                dao,
                null,
                null,
                AppExecutors.direct(),
                () -> "account-a"
        );

        assertEquals(1, repository.recordsNow().size());
        assertEquals("帳號紀錄", repository.recordsNow().get(0).question);
        assertTrue(repository.updateNote(7L, "帳號筆記"));
        assertEquals("先整理需求", dao.find(AccountStore.GUEST_ACCOUNT_ID, 7L).note);
        assertEquals("帳號筆記", dao.find("account-a", 7L).note);

        repository.deleteAll();

        assertEquals(1, dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).size());
        assertTrue(dao.recordsNow("account-a").isEmpty());
    }

    @Test
    public void guestTransferMovesRecordsToNewAccount() {
        FakeDao dao = new FakeDao(Collections.singletonList(
                DivinationRecordEntity.fromRecord(sampleRecord(), AccountStore.GUEST_ACCOUNT_ID)
        ));
        RecordRepository repository = new RecordRepository(
                dao,
                null,
                null,
                AppExecutors.direct(),
                () -> "account-a"
        );

        repository.transferGuestRecordsTo("account-a");

        assertTrue(dao.recordsNow(AccountStore.GUEST_ACCOUNT_ID).isEmpty());
        assertEquals(1, dao.recordsNow("account-a").size());
    }

    @Test
    public void exportedSchemaPreservesVersionTwoAccountScopedRecordTable() throws Exception {
        JSONObject schema = new JSONObject(new String(Files.readAllBytes(schemaFile().toPath()), StandardCharsets.UTF_8));
        JSONObject database = schema.getJSONObject("database");
        JSONObject entity = database.getJSONArray("entities").getJSONObject(0);

        assertEquals(2, database.getInt("version"));
        assertEquals("divination_records", entity.getString("tableName"));
        assertTrue(entity.getString("createSql").contains("`accountId` TEXT NOT NULL"));
        assertTrue(entity.getString("createSql").contains("PRIMARY KEY(`accountId`, `id`)"));
        assertTrue(entity.getString("createSql").contains("`relatingHexagramNumber` INTEGER NOT NULL"));
        assertTrue(entity.getString("createSql").contains("`lineValues` TEXT NOT NULL"));
        assertTrue(entity.getString("createSql").contains("`changingLines` TEXT NOT NULL"));
    }

    @Test
    public void asyncExportJsonUsesCallback() throws Exception {
        FakeDao dao = new FakeDao(Collections.singletonList(
                DivinationRecordEntity.fromRecord(sampleRecord(), AccountStore.GUEST_ACCOUNT_ID)));
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
        File rootPath = new File("app/schemas/fcu.app.i_ching.data.IChingDatabase/2.json");
        if (rootPath.isFile()) return rootPath;
        return new File("schemas/fcu.app.i_ching.data.IChingDatabase/2.json");
    }

    private static class FakeDao implements DivinationRecordDao {
        private final List<DivinationRecordEntity> records;
        private int upsertCount;

        FakeDao(List<DivinationRecordEntity> records) {
            this.records = new ArrayList<>(records);
        }

        @Override
        public LiveData<List<DivinationRecordEntity>> records(String accountId) {
            return null;
        }

        @Override
        public List<DivinationRecordEntity> recordsNow(String accountId) {
            List<DivinationRecordEntity> matches = new ArrayList<>();
            for (DivinationRecordEntity entity : records) {
                if (entity.accountId.equals(accountId)) matches.add(entity);
            }
            return matches;
        }

        @Override
        public DivinationRecordEntity find(String accountId, long id) {
            for (DivinationRecordEntity entity : records) {
                if (entity.accountId.equals(accountId) && entity.id == id) return entity;
            }
            return null;
        }

        @Override
        public void upsert(DivinationRecordEntity record) {
            upsertCount++;
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).id == record.id) {
                    records.set(i, record);
                    return;
                }
            }
            records.add(record);
        }

        @Override
        public int updateNote(String accountId, long id, String note) {
            for (int i = 0; i < records.size(); i++) {
                DivinationRecordEntity entity = records.get(i);
                if (entity.accountId.equals(accountId) && entity.id == id) {
                    records.set(i, DivinationRecordEntity.fromRecord(entity.toRecord().withNote(note), accountId));
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public int deleteById(String accountId, long id) {
            for (int i = 0; i < records.size(); i++) {
                DivinationRecordEntity entity = records.get(i);
                if (entity.accountId.equals(accountId) && entity.id == id) {
                    records.remove(i);
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public void deleteAll() {
            records.clear();
        }

        @Override
        public void deleteAll(String accountId) {
            for (int i = records.size() - 1; i >= 0; i--) {
                if (records.get(i).accountId.equals(accountId)) records.remove(i);
            }
        }

        @Override
        public int moveAccount(String sourceAccountId, String targetAccountId) {
            int count = 0;
            for (DivinationRecordEntity entity : records) {
                if (entity.accountId.equals(sourceAccountId)) {
                    entity.accountId = targetAccountId;
                    count++;
                }
            }
            return count;
        }
    }
}
