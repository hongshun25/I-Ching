package fcu.app.i_ching;

import android.os.Bundle;

import androidx.annotation.Nullable;

import org.json.JSONException;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;

public final class NavigationArgs {
    public static final String ARG_QUESTION = "question";
    public static final String ARG_METHOD = "method";
    public static final String ARG_RESULT_JSON = "resultJson";
    public static final String ARG_HEXAGRAM_NUMBER = "number";
    public static final String ARG_RECORD_ID = "recordId";
    public static final String DEFAULT_QUESTION = "我目前在工作上最需要調整的是什麼？";
    public static final long NO_RECORD_ID = -1L;

    private NavigationArgs() {}

    public static Bundle questionDraft(String draft) {
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, normalizeDraftQuestion(draft));
        return args;
    }

    public static Bundle method(String question) {
        Bundle args = new Bundle();
        args.putString(ARG_QUESTION, normalizeQuestion(question));
        return args;
    }

    public static Bundle ritual(String question, @Nullable DivinationMethod method) {
        Bundle args = method(question);
        args.putString(ARG_METHOD, normalizeMethod(method).name());
        return args;
    }

    public static Bundle result(DivinationResult result) {
        Bundle args = new Bundle();
        args.putString(ARG_RESULT_JSON, result == null ? "" : result.toJsonString());
        return args;
    }

    public static Bundle hexagramDetail(int number) {
        Bundle args = new Bundle();
        args.putInt(ARG_HEXAGRAM_NUMBER, number);
        return args;
    }

    public static String question(@Nullable Bundle args) {
        return normalizeQuestion(args == null ? null : args.getString(ARG_QUESTION));
    }

    public static String draftQuestion(@Nullable Bundle args) {
        return normalizeDraftQuestion(args == null ? null : args.getString(ARG_QUESTION));
    }

    public static DivinationMethod method(@Nullable Bundle args) {
        String value = args == null ? null : args.getString(ARG_METHOD);
        try {
            return value == null ? DivinationMethod.COINS : DivinationMethod.valueOf(value);
        } catch (IllegalArgumentException e) {
            return DivinationMethod.COINS;
        }
    }

    public static DivinationResult result(@Nullable Bundle args) {
        String snapshot = args == null ? null : args.getString(ARG_RESULT_JSON);
        if (snapshot != null && !snapshot.isEmpty()) {
            try {
                return DivinationResult.fromJsonString(snapshot);
            } catch (JSONException ignored) {
            }
        }
        return DivinationResult.create(DEFAULT_QUESTION, DivinationMethod.COINS);
    }

    public static int hexagramNumber(@Nullable Bundle args) {
        return args == null ? 15 : args.getInt(ARG_HEXAGRAM_NUMBER, 15);
    }

    public static long recordId(@Nullable Bundle args) {
        return args == null ? NO_RECORD_ID : args.getLong(ARG_RECORD_ID, NO_RECORD_ID);
    }

    public static void putRecordId(@Nullable Bundle args, long recordId) {
        if (args != null) args.putLong(ARG_RECORD_ID, recordId);
    }

    public static String normalizeQuestion(String question) {
        return question == null || question.trim().isEmpty() ? DEFAULT_QUESTION : question.trim();
    }

    private static String normalizeDraftQuestion(String question) {
        return question == null ? "" : question.trim();
    }

    private static DivinationMethod normalizeMethod(@Nullable DivinationMethod method) {
        return method == null ? DivinationMethod.COINS : method;
    }
}
