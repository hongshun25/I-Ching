package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
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
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.LocalRecordStore;

public class RecordsFragment extends Fragment {
    private static final String STATE_QUERY = "query";
    private static final String STATE_METHOD = "method";
    private static final String STATE_CHANGE_FILTER = "changeFilter";
    private static final String METHOD_ALL = "ALL";

    private final List<TextView> methodChips = new ArrayList<>();
    private final List<TextView> changeChips = new ArrayList<>();
    private String activeMethod = METHOD_ALL;
    private LocalRecordStore.ChangeFilter activeChangeFilter = LocalRecordStore.ChangeFilter.ALL;
    private EditText searchInput;
    private LinearLayout listContainer;
    private List<DivinationRecord> allRecords = new ArrayList<>();
    private RecordsViewModel viewModel;

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        viewModel = new ViewModelProvider(this).get(RecordsViewModel.class);
        methodChips.clear();
        changeChips.clear();
        if (savedInstanceState != null) {
            activeMethod = savedInstanceState.getString(STATE_METHOD, METHOD_ALL);
            activeChangeFilter = changeFilterFromName(savedInstanceState.getString(STATE_CHANGE_FILTER));
        }

        LinearLayout content = Ui.column(requireContext());
        content.addView(Ui.text(requireContext(), "占卜紀錄", 36, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        searchInput = Ui.bottomInput(requireContext(), "搜尋問題、筆記、卦名或標籤...", 1);
        searchInput.setContentDescription("搜尋占卜紀錄");
        if (savedInstanceState != null) searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderList(activity); }
            @Override public void afterTextChanged(Editable s) {}
        });
        Ui.addWithMargins(content, searchInput, -1, Ui.dp(requireContext(), 54), 0, 18, 0, 12);

        LinearLayout methodRow = Ui.row(requireContext());
        methodRow.setGravity(Gravity.START);
        addMethodChip(methodRow, "全部占法", METHOD_ALL, activity);
        addMethodChip(methodRow, DivinationMethod.COINS.label, DivinationMethod.COINS.name(), activity);
        addMethodChip(methodRow, DivinationMethod.YARROW.label, DivinationMethod.YARROW.name(), activity);
        addMethodChip(methodRow, DivinationMethod.SIMPLE.label, DivinationMethod.SIMPLE.name(), activity);
        content.addView(Ui.horizontalChips(requireContext(), methodRow));

        LinearLayout changeRow = Ui.row(requireContext());
        changeRow.setGravity(Gravity.START);
        addChangeChip(changeRow, LocalRecordStore.ChangeFilter.ALL, activity);
        addChangeChip(changeRow, LocalRecordStore.ChangeFilter.WITH_CHANGES, activity);
        addChangeChip(changeRow, LocalRecordStore.ChangeFilter.WITHOUT_CHANGES, activity);
        Ui.addWithMargins(content, Ui.horizontalChips(requireContext(), changeRow), -1, -2, 0, 8, 0, 0);

        listContainer = Ui.column(requireContext());
        content.addView(listContainer, new LinearLayout.LayoutParams(-1, -2));
        updateChipStyles();
        viewModel.records().observe(getViewLifecycleOwner(), records -> {
            allRecords = records == null ? new ArrayList<>() : records;
            renderList(activity);
        });
        renderList(activity);
        return Ui.pageWithChrome(activity, content, "紀錄");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_METHOD, activeMethod);
        outState.putString(STATE_CHANGE_FILTER, activeChangeFilter.name());
        if (searchInput != null) outState.putString(STATE_QUERY, searchInput.getText().toString());
    }

    private void addEmptyState(LinearLayout parent, MainActivity activity) {
        parent.setGravity(Gravity.CENTER_HORIZONTAL);
        TextView art = Ui.text(requireContext(), "↺", 76, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false);
        art.setGravity(Gravity.CENTER);
        TextView title = Ui.text(requireContext(), "你的占卜紀錄會出現在這裡", 22, android.graphics.Typeface.NORMAL, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        TextView body = Ui.text(requireContext(), "靜心凝神，透過易經尋求指引，記錄下每一次的智慧對話。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
        body.setGravity(Gravity.CENTER);
        Button start = Ui.pill(requireContext(), "✦ 開始第一次占卜", true);
        start.setOnClickListener(v -> activity.showQuestion());
        parent.addView(art, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 140)));
        parent.addView(title);
        Ui.addWithMargins(parent, body, -1, -2, 0, 8, 0, 28);
        parent.addView(start, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
    }

    private void addMethodChip(LinearLayout row, String label, String methodName, MainActivity activity) {
        TextView chip = Ui.chip(requireContext(), label);
        chip.setContentDescription("篩選" + label + "紀錄");
        chip.setTag(methodName);
        chip.setOnClickListener(v -> {
            activeMethod = methodName;
            updateChipStyles();
            renderList(activity);
        });
        methodChips.add(chip);
        Ui.addWithMargins(row, chip, -2, -2, 0, 0, 8, 0);
    }

    private void addChangeChip(LinearLayout row, LocalRecordStore.ChangeFilter filter, MainActivity activity) {
        TextView chip = Ui.chip(requireContext(), filter.label);
        chip.setContentDescription("篩選" + filter.label + "紀錄");
        chip.setTag(filter);
        chip.setOnClickListener(v -> {
            activeChangeFilter = filter;
            updateChipStyles();
            renderList(activity);
        });
        changeChips.add(chip);
        Ui.addWithMargins(row, chip, -2, -2, 0, 0, 8, 0);
    }

    private void updateChipStyles() {
        for (TextView chip : methodChips) {
            setChipSelected(chip, activeMethod.equals(chip.getTag()));
        }
        for (TextView chip : changeChips) {
            setChipSelected(chip, activeChangeFilter == chip.getTag());
        }
    }

    private void setChipSelected(TextView chip, boolean selected) {
        chip.setTextColor(Ui.color(requireContext(), selected ? R.color.ic_background : R.color.ic_text_muted));
        chip.setBackground(selected
                ? Ui.bg(requireContext(), R.color.ic_ink, 999)
                : Ui.strokeBg(requireContext(), R.color.ic_surface_container_low, R.color.ic_outline, 999));
        chip.setSelected(selected);
    }

    private void renderList(MainActivity activity) {
        if (listContainer == null || searchInput == null) return;
        listContainer.removeAllViews();
        listContainer.setGravity(Gravity.NO_GRAVITY);
        if (allRecords.isEmpty()) {
            addEmptyState(listContainer, activity);
            return;
        }
        List<DivinationRecord> records = LocalRecordStore.filter(
                allRecords,
                searchInput.getText().toString(),
                activeMethod(),
                activeChangeFilter
        );
        if (records.isEmpty()) {
            TextView empty = Ui.text(requireContext(), "沒有符合的占卜紀錄", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
            empty.setGravity(Gravity.CENTER);
            Ui.addWithMargins(listContainer, empty, -1, -2, 0, 28, 0, 0);
            return;
        }
        for (DivinationRecord record : records) addRecordCard(activity, listContainer, record);
    }

    private void addRecordCard(MainActivity activity, LinearLayout parent, DivinationRecord record) {
        Hexagram hex = HexagramRepository.get(record.hexagramNumber);
        Hexagram relating = HexagramRepository.get(record.relatingHexagramNumber);
        LinearLayout card = Ui.card(requireContext());
        card.addView(Ui.text(requireContext(), "第" + hex.number + "卦｜" + hex.fullName, 22, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        card.addView(Ui.text(requireContext(), relationText(hex, relating), 15, android.graphics.Typeface.BOLD, R.color.ic_gold, false));
        card.addView(Ui.text(requireContext(), changingText(record), 13, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false));
        card.addView(Ui.text(requireContext(), record.question, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        card.addView(Ui.text(requireContext(), record.method.label + " · " + DateFormat.format("yyyy/MM/dd HH:mm", new Date(record.createdAt)), 12, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false));
        if (record.note != null && !record.note.isEmpty()) card.addView(Ui.text(requireContext(), record.note, 15, android.graphics.Typeface.NORMAL, R.color.ic_gold, false));
        LinearLayout actions = Ui.row(requireContext());
        Button edit = Ui.pill(requireContext(), "編輯筆記", false);
        edit.setContentDescription("編輯第" + hex.number + "卦紀錄筆記");
        edit.setOnClickListener(v -> showEditDialog(record));
        Button delete = Ui.pill(requireContext(), "刪除", false);
        delete.setContentDescription("刪除第" + hex.number + "卦紀錄");
        delete.setOnClickListener(v -> confirmDelete(record));
        Ui.addWithMargins(actions, edit, 0, Ui.dp(requireContext(), 46), 0, 16, 6, 0);
        LinearLayout.LayoutParams editParams = (LinearLayout.LayoutParams) edit.getLayoutParams();
        editParams.weight = 1;
        Ui.addWithMargins(actions, delete, 0, Ui.dp(requireContext(), 46), 6, 16, 0, 0);
        LinearLayout.LayoutParams deleteParams = (LinearLayout.LayoutParams) delete.getLayoutParams();
        deleteParams.weight = 1;
        card.addView(actions, new LinearLayout.LayoutParams(-1, -2));
        card.setOnClickListener(v -> activity.showHexagramDetail(hex.number));
        Ui.addWithMargins(parent, card, -1, -2, 0, 14, 0, 0);
    }

    private DivinationMethod activeMethod() {
        if (METHOD_ALL.equals(activeMethod)) return null;
        try {
            return DivinationMethod.valueOf(activeMethod);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private String relationText(Hexagram hex, Hexagram relating) {
        if (hex.number == relating.number) return "本卦即之卦｜第" + relating.number + "卦 " + relating.fullName;
        return "本卦 → 之卦｜第" + hex.number + "卦 " + hex.name + " → 第" + relating.number + "卦 " + relating.name;
    }

    private String changingText(DivinationRecord record) {
        if (record.changingLines.isEmpty()) return "無變爻";
        StringBuilder builder = new StringBuilder("變爻 ");
        for (int i = 0; i < record.changingLines.size(); i++) {
            if (i > 0) builder.append("、");
            builder.append(record.changingLines.get(i));
        }
        return builder.toString();
    }

    private LocalRecordStore.ChangeFilter changeFilterFromName(String name) {
        try {
            return name == null ? LocalRecordStore.ChangeFilter.ALL : LocalRecordStore.ChangeFilter.valueOf(name);
        } catch (IllegalArgumentException e) {
            return LocalRecordStore.ChangeFilter.ALL;
        }
    }

    private void showEditDialog(DivinationRecord record) {
        EditText input = Ui.bottomInput(requireContext(), "補充這次占卜的反思...", 4);
        input.setText(record.note == null ? "" : record.note);
        input.setSelection(input.getText().length());
        input.setContentDescription("編輯占卜紀錄筆記");
        new AlertDialog.Builder(requireContext())
                .setTitle("編輯筆記")
                .setView(input)
                .setNegativeButton("取消", null)
                .setPositiveButton("儲存", (dialog, which) -> {
                    viewModel.updateNote(record.id, input.getText().toString());
                    Toast.makeText(requireContext(), "筆記已更新", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    private void confirmDelete(DivinationRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除紀錄？")
                .setMessage("這筆占卜紀錄將從本機移除。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除", (dialog, which) -> {
                    viewModel.delete(record.id);
                    Toast.makeText(requireContext(), "紀錄已刪除", Toast.LENGTH_SHORT).show();
                })
                .show();
    }
}
