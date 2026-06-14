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
        List<DivinationRecord> records = all();
        records.add(0, record);
        save(records);
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
}
