package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.WorkerThread;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;

public class RecordRepository {
    public interface Callback<T> {
        void onComplete(T result);
    }

    private static final String LEGACY_PREFS = "i_ching_records";
    private static final String LEGACY_KEY_RECORDS = "records";
    private static final String MIGRATION_PREFS = "i_ching_record_migration";
    private static final String KEY_ROOM_MIGRATED = "roomMigrated";

    private static volatile RecordRepository instance;

    private final DivinationRecordDao dao;
    private final SharedPreferences legacyPrefs;
    private final SharedPreferences migrationPrefs;
    private final AppExecutors executors;

    public static RecordRepository get(Context context) {
        if (instance == null) {
            synchronized (RecordRepository.class) {
                if (instance == null) {
                    Context appContext = context.getApplicationContext();
                    instance = new RecordRepository(
                            IChingDatabase.get(appContext).recordDao(),
                            appContext.getSharedPreferences(LEGACY_PREFS, Context.MODE_PRIVATE),
                            appContext.getSharedPreferences(MIGRATION_PREFS, Context.MODE_PRIVATE),
                            AppExecutors.get()
                    );
                }
            }
        }
        return instance;
    }

    RecordRepository(DivinationRecordDao dao, SharedPreferences legacyPrefs, SharedPreferences migrationPrefs) {
        this(dao, legacyPrefs, migrationPrefs, AppExecutors.get());
    }

    RecordRepository(DivinationRecordDao dao, SharedPreferences legacyPrefs, SharedPreferences migrationPrefs, AppExecutors executors) {
        this.dao = dao;
        this.legacyPrefs = legacyPrefs;
        this.migrationPrefs = migrationPrefs;
        this.executors = executors;
    }

    public LiveData<List<DivinationRecord>> records() {
        return Transformations.map(dao.records(), RecordRepository::entitiesToRecords);
    }

    @WorkerThread
    public List<DivinationRecord> recordsNow() {
        return entitiesToRecords(dao.recordsNow());
    }

    @WorkerThread
    public void addOrUpdate(DivinationRecord record) {
        dao.upsert(DivinationRecordEntity.fromRecord(record));
    }

    public void addOrUpdate(DivinationRecord record, Callback<DivinationRecord> callback) {
        execute(() -> {
            addOrUpdate(record);
            return record;
        }, callback, null);
    }

    @WorkerThread
    public boolean updateNote(long id, String note) {
        return dao.updateNote(id, note == null ? "" : note) > 0;
    }

    public void updateNote(long id, String note, Callback<Boolean> callback) {
        execute(() -> updateNote(id, note), callback, false);
    }

    @WorkerThread
    public boolean delete(long id) {
        return dao.deleteById(id) > 0;
    }

    public void delete(long id, Callback<Boolean> callback) {
        execute(() -> delete(id), callback, false);
    }

    @WorkerThread
    public void deleteAll() {
        dao.deleteAll();
    }

    public void deleteAll(Callback<Boolean> callback) {
        execute(() -> {
            deleteAll();
            return true;
        }, callback, false);
    }

    @WorkerThread
    public DivinationRecord find(long id) {
        DivinationRecordEntity entity = dao.find(id);
        return entity == null ? null : entity.toRecord();
    }

    public void find(long id, Callback<DivinationRecord> callback) {
        execute(() -> find(id), callback, null);
    }

    @WorkerThread
    public String exportJson() {
        return exportJson(recordsNow());
    }

    public void exportJson(Callback<String> callback) {
        execute(this::exportJson, callback, "[]");
    }

    @WorkerThread
    public String exportText() {
        return exportText(recordsNow());
    }

    public void exportText(Callback<String> callback) {
        execute(this::exportText, callback, "易經占卜紀錄\n目前無法匯出紀錄。\n");
    }

    @WorkerThread
    public void migrateFromLegacyPrefsIfNeeded() {
        migrateFromLegacyPrefs();
    }

    public void migrateFromLegacyPrefsIfNeeded(Callback<Boolean> callback) {
        execute(this::migrateFromLegacyPrefs, callback, false);
    }

    private boolean migrateFromLegacyPrefs() {
        if (migrationPrefs.getBoolean(KEY_ROOM_MIGRATED, false)) return true;
        String json = legacyPrefs.getString(LEGACY_KEY_RECORDS, "[]");
        try {
            List<DivinationRecord> legacyRecords = parseLegacyRecords(json);
            for (DivinationRecord record : legacyRecords) {
                addOrUpdate(record);
            }
            migrationPrefs.edit().putBoolean(KEY_ROOM_MIGRATED, true).apply();
            return true;
        } catch (JSONException ignored) {
            return false;
        }
    }

    public static List<DivinationRecord> parseLegacyRecords(String value) throws JSONException {
        return DivinationRecord.fromJsonArray(value);
    }

    public static String exportJson(List<DivinationRecord> records) {
        try {
            return DivinationRecord.toJsonArray(records == null ? new ArrayList<>() : records).toString(2);
        } catch (JSONException e) {
            return "[]";
        }
    }

    public static String exportText(List<DivinationRecord> records) {
        StringBuilder builder = new StringBuilder("易經占卜紀錄\n");
        List<DivinationRecord> source = records == null ? new ArrayList<>() : records;
        if (source.isEmpty()) {
            builder.append("目前沒有占卜紀錄。\n");
            return builder.toString();
        }
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.TAIWAN);
        for (int i = 0; i < source.size(); i++) {
            DivinationRecord record = source.get(i);
            Hexagram hexagram = HexagramRepository.get(record.hexagramNumber);
            Hexagram relating = HexagramRepository.get(record.relatingHexagramNumber);
            if (i > 0) builder.append("\n---\n");
            builder.append(format.format(new Date(record.createdAt))).append("\n");
            builder.append("問題：").append(record.question == null ? "" : record.question).append("\n");
            builder.append("占法：").append(record.method == null ? DivinationMethod.COINS.label : record.method.label).append("\n");
            builder.append("本卦：第").append(hexagram.number).append("卦｜").append(hexagram.fullName).append("\n");
            builder.append("之卦：第").append(relating.number).append("卦｜").append(relating.fullName).append("\n");
            builder.append("變爻：").append(changingLineText(record)).append("\n");
            if (record.note != null && !record.note.isEmpty()) {
                builder.append("筆記：").append(record.note).append("\n");
            }
        }
        return builder.toString();
    }

    private static String changingLineText(DivinationRecord record) {
        if (record.changingLines == null || record.changingLines.isEmpty()) return "無";
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < record.changingLines.size(); i++) {
            if (i > 0) builder.append("、");
            builder.append(record.changingLines.get(i));
        }
        return builder.toString();
    }

    private static List<DivinationRecord> entitiesToRecords(List<DivinationRecordEntity> entities) {
        List<DivinationRecord> records = new ArrayList<>();
        if (entities == null) return records;
        for (DivinationRecordEntity entity : entities) {
            records.add(entity.toRecord());
        }
        return records;
    }

    private <T> void execute(Callable<T> task, Callback<T> callback, T fallback) {
        executors.diskIo().execute(() -> {
            T result;
            try {
                result = task.call();
            } catch (Exception e) {
                result = fallback;
            }
            if (callback != null) {
                T finalResult = result;
                executors.mainThread().execute(() -> callback.onComplete(finalResult));
            }
        });
    }
}
