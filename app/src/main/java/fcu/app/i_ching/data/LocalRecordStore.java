package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class LocalRecordStore {
    private static final String PREFS = "i_ching_records";
    private static final String KEY_RECORDS = "records";
    private final SharedPreferences prefs;

    public LocalRecordStore(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public List<DivinationRecord> all() {
        try {
            return DivinationRecord.fromJsonArray(prefs.getString(KEY_RECORDS, "[]"));
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    public void add(DivinationRecord record) {
        save(upsert(all(), record));
    }

    public boolean updateNote(long id, String note) {
        List<DivinationRecord> records = all();
        boolean updated = updateNote(records, id, note);
        if (updated) save(records);
        return updated;
    }

    public boolean delete(long id) {
        List<DivinationRecord> records = all();
        boolean deleted = delete(records, id);
        if (deleted) save(records);
        return deleted;
    }

    public DivinationRecord find(long id) {
        return find(all(), id);
    }

    public void save(List<DivinationRecord> records) {
        try {
            prefs.edit().putString(KEY_RECORDS, DivinationRecord.toJsonArray(records).toString()).apply();
        } catch (JSONException ignored) {
        }
    }

    public void clear() {
        prefs.edit().remove(KEY_RECORDS).apply();
    }

    static List<DivinationRecord> upsert(List<DivinationRecord> records, DivinationRecord record) {
        List<DivinationRecord> updated = new ArrayList<>();
        updated.add(record);
        for (DivinationRecord existing : records) {
            if (existing.id != record.id) updated.add(existing);
        }
        return updated;
    }

    static boolean updateNote(List<DivinationRecord> records, long id, String note) {
        for (int i = 0; i < records.size(); i++) {
            DivinationRecord record = records.get(i);
            if (record.id == id) {
                records.set(i, record.withNote(note));
                return true;
            }
        }
        return false;
    }

    static boolean delete(List<DivinationRecord> records, long id) {
        for (int i = 0; i < records.size(); i++) {
            if (records.get(i).id == id) {
                records.remove(i);
                return true;
            }
        }
        return false;
    }

    static DivinationRecord find(List<DivinationRecord> records, long id) {
        for (DivinationRecord record : records) {
            if (record.id == id) return record;
        }
        return null;
    }
}
