package fcu.app.i_ching.data;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocalRecordStore {
    private static final String PREFS = "i_ching_records";
    private static final String KEY_RECORDS = "records";
    private final SharedPreferences prefs;

    public enum ChangeFilter {
        ALL("全部"),
        WITH_CHANGES("有變爻"),
        WITHOUT_CHANGES("無變爻");

        public final String label;

        ChangeFilter(String label) {
            this.label = label;
        }
    }

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

    public static List<DivinationRecord> filter(List<DivinationRecord> records, String query,
                                                DivinationMethod method, ChangeFilter changeFilter) {
        List<DivinationRecord> matches = new ArrayList<>();
        String normalizedQuery = normalize(query);
        ChangeFilter resolvedChangeFilter = changeFilter == null ? ChangeFilter.ALL : changeFilter;
        if (records == null) return matches;
        for (DivinationRecord record : records) {
            if (method != null && record.method != method) continue;
            if (!matchesChangeFilter(record, resolvedChangeFilter)) continue;
            if (!matchesQuery(record, normalizedQuery)) continue;
            matches.add(record);
        }
        return matches;
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

    private static boolean matchesChangeFilter(DivinationRecord record, ChangeFilter filter) {
        if (filter == ChangeFilter.WITH_CHANGES) return !record.changingLines.isEmpty();
        if (filter == ChangeFilter.WITHOUT_CHANGES) return record.changingLines.isEmpty();
        return true;
    }

    private static boolean matchesQuery(DivinationRecord record, String query) {
        if (query.isEmpty()) return true;
        if (normalize(record.question).contains(query)) return true;
        if (normalize(record.note).contains(query)) return true;
        if (normalize(record.method.label).contains(query)) return true;
        Hexagram hexagram = HexagramRepository.get(record.hexagramNumber);
        Hexagram relating = HexagramRepository.get(record.relatingHexagramNumber);
        if (matchesHexagram(hexagram, query)) return true;
        return relating.number != hexagram.number && matchesHexagram(relating, query);
    }

    private static boolean matchesHexagram(Hexagram hexagram, String query) {
        if (normalize(hexagram.name).contains(query)) return true;
        if (normalize(hexagram.fullName).contains(query)) return true;
        if (normalize(hexagram.summary).contains(query)) return true;
        for (String tag : hexagram.tags) {
            if (normalize(tag).contains(query)) return true;
        }
        return false;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }
}
