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
    public final int relatingHexagramNumber;
    public final DivinationMethod method;
    public final int[] lineValues;
    public final List<Integer> changingLines;
    public final long createdAt;
    public String note;

    public DivinationRecord(long id, String question, int hexagramNumber, DivinationMethod method,
                            int[] lineValues, List<Integer> changingLines, long createdAt, String note) {
        this(id, question, hexagramNumber, inferRelatingNumber(hexagramNumber, lineValues, changingLines), method,
                lineValues, changingLines, createdAt, note);
    }

    public DivinationRecord(long id, String question, int hexagramNumber, int relatingHexagramNumber,
                            DivinationMethod method, int[] lineValues, List<Integer> changingLines,
                            long createdAt, String note) {
        this.id = id;
        this.question = question;
        this.hexagramNumber = hexagramNumber;
        this.relatingHexagramNumber = relatingHexagramNumber;
        this.method = method;
        this.lineValues = lineValues == null ? new int[0] : lineValues;
        this.changingLines = changingLines == null ? new ArrayList<>() : new ArrayList<>(changingLines);
        this.createdAt = createdAt;
        this.note = note;
    }

    public DivinationRecord(long id, String question, int hexagramNumber, DivinationMethod method, long createdAt, String note) {
        this(id, question, hexagramNumber, method, new int[0], new ArrayList<>(), createdAt, note);
    }

    public static DivinationRecord fromResult(DivinationResult result, String note) {
        return new DivinationRecord(result.createdAt, result.question, result.hexagram.number,
                result.relatingHexagramNumber, result.method, result.lineValues, result.changingLines,
                result.createdAt, note);
    }

    public DivinationRecord withNote(String value) {
        return new DivinationRecord(id, question, hexagramNumber, relatingHexagramNumber, method,
                lineValues, changingLines, createdAt, value);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("id", id);
        object.put("question", question);
        object.put("hexagramNumber", hexagramNumber);
        object.put("relatingHexagramNumber", relatingHexagramNumber);
        object.put("method", method.name());
        object.put("lineValues", DivinationResult.intArrayToJson(lineValues));
        object.put("changingLines", DivinationResult.integerListToJson(changingLines));
        object.put("createdAt", createdAt);
        object.put("note", note == null ? "" : note);
        return object;
    }

    public static DivinationRecord fromJson(JSONObject object) throws JSONException {
        long createdAt = object.optLong("createdAt", object.optLong("id", System.currentTimeMillis()));
        int hexagramNumber = object.optInt("hexagramNumber", 15);
        int[] lineValues = DivinationResult.jsonToIntArray(object.optJSONArray("lineValues"));
        List<Integer> changingLines = object.has("changingLines")
                ? DivinationResult.jsonToIntegerList(object.optJSONArray("changingLines"))
                : HexagramRepository.changingLinesFromValues(lineValues);
        int relatingNumber = object.has("relatingHexagramNumber")
                ? object.optInt("relatingHexagramNumber", hexagramNumber)
                : inferRelatingNumber(hexagramNumber, lineValues, changingLines);
        return new DivinationRecord(
                object.optLong("id", createdAt),
                object.optString("question"),
                hexagramNumber,
                relatingNumber,
                DivinationResult.methodFromName(object.optString("method", DivinationMethod.COINS.name())),
                lineValues,
                changingLines,
                createdAt,
                object.optString("note")
        );
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

    private static int inferRelatingNumber(int hexagramNumber, int[] lineValues, List<Integer> changingLines) {
        boolean[] lines = HexagramRepository.linesFromValues(lineValues);
        if (lines.length == 6) return HexagramRepository.relatingFrom(lines, changingLines).number;
        return hexagramNumber;
    }
}
