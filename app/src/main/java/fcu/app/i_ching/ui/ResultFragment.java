package fcu.app.i_ching.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.json.JSONException;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.HexagramLine;

public class ResultFragment extends Fragment {
    private static final String ARG_RESULT_JSON = MainActivity.ARG_RESULT_JSON;
    private static final String ARG_RECORD_ID = "recordId";
    private static final String STATE_RECORD_ID = "recordId";
    private static final String STATE_NOTE = "note";
    public static final long NO_RECORD_ID = -1L;

    private DivinationResult result;
    private long savedRecordId = NO_RECORD_ID;
    private EditText noteInput;
    private ResultViewModel viewModel;

    public static ResultFragment newInstance(DivinationResult value) {
        Bundle args = new Bundle();
        args.putString(ARG_RESULT_JSON, value.toJsonString());
        ResultFragment fragment = new ResultFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        result = readResult();
        savedRecordId = readRecordId(savedInstanceState);
        MainActivity activity = (MainActivity) requireActivity();
        viewModel = new ViewModelProvider(this).get(ResultViewModel.class);
        DivinationRecord existingRecord = viewModel.ensureAutoSaved(result, activity.settings().isAutoSave());
        if (existingRecord != null) rememberRecordId(existingRecord.id);

        LinearLayout content = Ui.column(requireContext());
        LinearLayout questionBubble = Ui.card(requireContext());
        TextView q = Ui.text(requireContext(), "“" + result.question + "”", 16, android.graphics.Typeface.ITALIC, R.color.ic_text_muted, false); q.setGravity(Gravity.CENTER); questionBubble.addView(q);
        content.addView(questionBubble, new LinearLayout.LayoutParams(-1, -2));
        LinearLayout hero = Ui.card(requireContext()); hero.setGravity(Gravity.CENTER_HORIZONTAL);
        hero.addView(Ui.hexagramView(requireContext(), result.hexagram, 96, 8, false));
        TextView title = Ui.text(requireContext(), "第" + result.hexagram.number + "卦｜" + result.hexagram.fullName, 36, android.graphics.Typeface.BOLD, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
        TextView tags = Ui.text(requireContext(), "上" + result.hexagram.upper + "　下" + result.hexagram.lower + "　" + result.method.label, 14, android.graphics.Typeface.BOLD, R.color.ic_text_muted, false); tags.setGravity(Gravity.CENTER);
        TextView insight = Ui.text(requireContext(), result.hexagram.summary, 22, android.graphics.Typeface.NORMAL, R.color.ic_gold, true); insight.setGravity(Gravity.CENTER);
        Ui.addWithMargins(hero, title, -1, -2, 0, 18, 0, 4); hero.addView(tags); Ui.addWithMargins(hero, insight, -1, -2, 0, 18, 0, 0);
        Ui.addWithMargins(content, hero, -1, -2, 0, 18, 0, 18);
        LinearLayout relating = Ui.card(requireContext());
        relating.addView(Ui.text(requireContext(), "本卦與之卦", 18, android.graphics.Typeface.BOLD, R.color.ic_ink, true));
        relating.addView(Ui.text(requireContext(), "本卦 第" + result.hexagram.number + "卦｜" + result.hexagram.fullName
                + "  →  之卦 第" + result.relatingHexagramNumber + "卦｜" + result.relatingHexagram.fullName,
                16, android.graphics.Typeface.BOLD, R.color.ic_gold, false));
        Ui.addWithMargins(relating, Ui.text(requireContext(), changingSummary(), 15, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false), -1, -2, 0, 8, 0, 0);
        String changedLines = changedLineText();
        if (!changedLines.isEmpty()) {
            Ui.addWithMargins(relating, Ui.text(requireContext(), changedLines, 15, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false), -1, -2, 0, 10, 0, 0);
        }
        Ui.addWithMargins(content, relating, -1, -2, 0, 0, 0, 18);
        LinearLayout guides = Ui.card(requireContext());
        guides.addView(Ui.text(requireContext(), "適合做", 16, android.graphics.Typeface.BOLD, R.color.ic_gold, false));
        guides.addView(Ui.text(requireContext(), String.join("\n", result.hexagram.doItems), 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        Ui.addWithMargins(guides, Ui.text(requireContext(), "暫時避免", 16, android.graphics.Typeface.BOLD, R.color.ic_error, false), -1, -2, 0, 16, 0, 0);
        guides.addView(Ui.text(requireContext(), String.join("\n", result.hexagram.avoidItems), 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        content.addView(guides);
        LinearLayout classical = Ui.card(requireContext()); classical.addView(Ui.text(requireContext(), "古典卦象解釋", 18, android.graphics.Typeface.BOLD, R.color.ic_ink, true)); classical.addView(Ui.text(requireContext(), result.hexagram.judgment + "\n\n" + result.hexagram.classicalText, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false)); Ui.addWithMargins(content, classical, -1, -2, 0, 16, 0, 0);
        LinearLayout sharePreview = Ui.card(requireContext());
        sharePreview.addView(Ui.text(requireContext(), "可分享文字", 18, android.graphics.Typeface.BOLD, R.color.ic_ink, true));
        sharePreview.addView(Ui.text(requireContext(), shareText(), 15, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        Ui.addWithMargins(content, sharePreview, -1, -2, 0, 16, 0, 0);
        TextView noteLabel = Ui.text(requireContext(), "這個結果讓你想到什麼？", 14, android.graphics.Typeface.BOLD, R.color.ic_ink, false); Ui.addWithMargins(content, noteLabel, -1, -2, 0, 20, 0, 4);
        noteInput = Ui.bottomInput(requireContext(), "寫下你的靈感或打算採取的行動...", 3);
        noteInput.setContentDescription("占卜結果筆記");
        String restoredNote = savedInstanceState == null ? null : savedInstanceState.getString(STATE_NOTE);
        if (restoredNote != null) {
            noteInput.setText(restoredNote);
        } else if (existingRecord != null && existingRecord.note != null) {
            noteInput.setText(existingRecord.note);
        }
        content.addView(noteInput, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 96)));
        Button save = Ui.pill(requireContext(), savedRecordId == NO_RECORD_ID ? "儲存至紀錄" : "更新紀錄筆記", true);
        save.setContentDescription("儲存占卜結果筆記至紀錄");
        save.setOnClickListener(v -> saveNote(activity));
        Button share = Ui.pill(requireContext(), "分享啟示", false); share.setOnClickListener(v -> shareResult());
        Ui.addWithMargins(content, save, -1, Ui.dp(requireContext(), 52), 0, 22, 0, 10); content.addView(share, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        return Ui.scrollPage(requireContext(), content, false);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_RECORD_ID, savedRecordId);
        if (noteInput != null) outState.putString(STATE_NOTE, noteInput.getText().toString());
    }

    private DivinationResult readResult() {
        Bundle args = getArguments();
        if (args != null) {
            String snapshot = args.getString(ARG_RESULT_JSON);
            if (snapshot != null && !snapshot.isEmpty()) {
                try {
                    return DivinationResult.fromJsonString(snapshot);
                } catch (JSONException ignored) {
                }
            }
        }
        return DivinationResult.create("我目前在工作上最需要調整的是什麼？", DivinationMethod.COINS);
    }

    private long readRecordId(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) return savedInstanceState.getLong(STATE_RECORD_ID, NO_RECORD_ID);
        Bundle args = getArguments();
        return args == null ? NO_RECORD_ID : args.getLong(ARG_RECORD_ID, NO_RECORD_ID);
    }

    private void saveNote(MainActivity activity) {
        DivinationRecord record = viewModel.saveNote(result, savedRecordId, noteInput.getText().toString());
        rememberRecordId(record.id);
        Toast.makeText(requireContext(), "已儲存至紀錄", Toast.LENGTH_SHORT).show();
        activity.showRecords();
    }

    private void rememberRecordId(long recordId) {
        savedRecordId = recordId;
        Bundle args = getArguments();
        if (args != null) args.putLong(ARG_RECORD_ID, recordId);
    }

    private String changingSummary() {
        if (result.changingLines.isEmpty()) return "本次無變爻，之卦與本卦相同。";
        StringBuilder builder = new StringBuilder("變爻：");
        for (int i = 0; i < result.changingLines.size(); i++) {
            if (i > 0) builder.append("、");
            builder.append(positionName(result.changingLines.get(i)));
        }
        return builder.toString();
    }

    private String changedLineText() {
        if (result.changingLines.isEmpty()) return "";
        StringBuilder builder = new StringBuilder();
        for (Integer position : result.changingLines) {
            if (position == null || position < 1 || position > result.hexagram.lineTexts.size()) continue;
            HexagramLine line = result.hexagram.lineTexts.get(position - 1);
            if (builder.length() > 0) builder.append("\n\n");
            builder.append(line.label).append("：").append(line.text).append("\n").append(line.modernHint);
        }
        return builder.toString();
    }

    private String shareText() {
        return "《易經占卜》\n"
                + "問題：" + result.question + "\n"
                + "占法：" + result.method.label + "\n"
                + "本卦：第" + result.hexagram.number + "卦｜" + result.hexagram.fullName + "\n"
                + changingSummary() + "\n"
                + "之卦：第" + result.relatingHexagramNumber + "卦｜" + result.relatingHexagram.fullName + "\n"
                + "啟示：" + result.hexagram.summary;
    }

    private void shareResult() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, shareText());
        startActivity(Intent.createChooser(intent, "分享啟示"));
    }

    private String positionName(int position) {
        switch (position) {
            case 1: return "初爻";
            case 2: return "二爻";
            case 3: return "三爻";
            case 4: return "四爻";
            case 5: return "五爻";
            case 6: return "上爻";
            default: return "第" + position + "爻";
        }
    }
}
