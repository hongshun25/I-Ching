package fcu.app.i_ching.data;

import androidx.room.TypeConverter;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class RecordTypeConverters {
    @TypeConverter
    public static String lineValuesToJson(int[] values) {
        try {
            return DivinationResult.intArrayToJson(values).toString();
        } catch (JSONException e) {
            return "[]";
        }
    }

    @TypeConverter
    public static int[] jsonToLineValues(String value) {
        try {
            return DivinationResult.jsonToIntArray(new JSONArray(emptyArrayIfBlank(value)));
        } catch (JSONException e) {
            return new int[0];
        }
    }

    @TypeConverter
    public static String changingLinesToJson(List<Integer> values) {
        try {
            return DivinationResult.integerListToJson(values).toString();
        } catch (JSONException e) {
            return "[]";
        }
    }

    @TypeConverter
    public static List<Integer> jsonToChangingLines(String value) {
        try {
            return DivinationResult.jsonToIntegerList(new JSONArray(emptyArrayIfBlank(value)));
        } catch (JSONException e) {
            return new ArrayList<>();
        }
    }

    private static String emptyArrayIfBlank(String value) {
        return value == null || value.trim().isEmpty() ? "[]" : value;
    }
}
