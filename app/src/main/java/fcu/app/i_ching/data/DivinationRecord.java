package fcu.app.i_ching.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DivinationRecord {
    public final long id;
    public final String question;
    public final int hexagramNumber;
    public final DivinationMethod method;
    public final long createdAt;
    public String note;

    public DivinationRecord(long id, String question, int hexagramNumber, DivinationMethod method, long createdAt, String note) {
        this.id = id;
        this.question = question;
        this.hexagramNumber = hexagramNumber;
        this.method = method;
        this.createdAt = createdAt;
        this.note = note;
    }

    public static DivinationRecord fromResult(DivinationResult result, String note) {
        return new DivinationRecord(System.currentTimeMillis(), result.question, result.hexagram.number, result.method, result.createdAt, note);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("question", question);
        object.put("hexagramNumber", hexagramNumber);
        object.put("method", method.name());
        object.put("createdAt", createdAt);
        object.put("note", note == null ? "" : note);
        return object;
    }

    public static DivinationRecord fromJson(JSONObject object) throws JSONException {
        return new DivinationRecord(object.getLong("id"), object.optString("question"), object.getInt("hexagramNumber"),
                DivinationMethod.valueOf(object.optString("method", DivinationMethod.COINS.name())),
                object.optLong("createdAt"), object.optString("note"));
    }

    public static JSONArray toJsonArray(List<DivinationRecord> records) throws JSONException {
        JSONArray array = new JSONArray();
        for (DivinationRecord record : records) array.put(record.toJson());
        return array;
    }

    public static List<DivinationRecord> fromJsonArray(String value) throws JSONException {
        List<DivinationRecord> records = new ArrayList<>();
        JSONArray array = new JSONArray(value == null || value.isEmpty() ? "[]" : value);
        for (int i = 0; i < array.length(); i++) records.add(fromJson(array.getJSONObject(i)));
        return records;
    }
}
