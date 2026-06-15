package fcu.app.i_ching.data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DivinationResult {
    public final String question;
    public final DivinationMethod method;
    public final Hexagram hexagram;
    public final int[] lineValues;
    public final List<Integer> changingLines;
    public final long createdAt;

    public DivinationResult(String question, DivinationMethod method, Hexagram hexagram, int[] lineValues,
                            List<Integer> changingLines, long createdAt) {
        this.question = question;
        this.method = method;
        this.hexagram = hexagram;
        this.lineValues = lineValues == null ? new int[0] : lineValues;
        this.changingLines = changingLines == null ? new ArrayList<>() : new ArrayList<>(changingLines);
        this.createdAt = createdAt;
    }

    public static DivinationResult create(String question, DivinationMethod method) {
        return new DivinationEngine().cast(question, method);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject object = new JSONObject();
        object.put("question", question);
        object.put("method", method.name());
        object.put("hexagramNumber", hexagram.number);
        object.put("lineValues", intArrayToJson(lineValues));
        object.put("changingLines", integerListToJson(changingLines));
        object.put("createdAt", createdAt);
        return object;
    }

    public String toJsonString() {
        try {
            return toJson().toString();
        } catch (JSONException e) {
            return "";
        }
    }

    public static DivinationResult fromJsonString(String value) throws JSONException {
        return fromJson(new JSONObject(value == null || value.isEmpty() ? "{}" : value));
    }

    public static DivinationResult fromJson(JSONObject object) throws JSONException {
        int hexagramNumber = object.optInt("hexagramNumber", 15);
        return new DivinationResult(
                object.optString("question"),
                methodFromName(object.optString("method", DivinationMethod.COINS.name())),
                HexagramRepository.get(hexagramNumber),
                jsonToIntArray(object.optJSONArray("lineValues")),
                jsonToIntegerList(object.optJSONArray("changingLines")),
                object.optLong("createdAt", System.currentTimeMillis())
        );
    }

    static JSONArray intArrayToJson(int[] values) throws JSONException {
        JSONArray array = new JSONArray();
        if (values == null) return array;
        for (int value : values) array.put(value);
        return array;
    }

    static int[] jsonToIntArray(JSONArray array) throws JSONException {
        if (array == null) return new int[0];
        int[] values = new int[array.length()];
        for (int i = 0; i < array.length(); i++) values[i] = array.getInt(i);
        return values;
    }

    static JSONArray integerListToJson(List<Integer> values) throws JSONException {
        JSONArray array = new JSONArray();
        if (values == null) return array;
        for (Integer value : values) array.put(value == null ? 0 : value);
        return array;
    }

    static List<Integer> jsonToIntegerList(JSONArray array) throws JSONException {
        List<Integer> values = new ArrayList<>();
        if (array == null) return values;
        for (int i = 0; i < array.length(); i++) values.add(array.getInt(i));
        return values;
    }

    static DivinationMethod methodFromName(String value) {
        try {
            return DivinationMethod.valueOf(value);
        } catch (IllegalArgumentException | NullPointerException e) {
            return DivinationMethod.COINS;
        }
    }
}
