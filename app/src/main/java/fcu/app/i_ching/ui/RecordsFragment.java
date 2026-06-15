package fcu.app.i_ching.ui;

import android.os.Bundle;
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

import java.util.Date;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationRecord;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.LocalRecordStore;

public class RecordsFragment extends Fragment {
    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        LinearLayout content = Ui.column(requireContext());
        LocalRecordStore recordStore = new LocalRecordStore(requireContext());
        List<DivinationRecord> records = recordStore.all();
        if (records.isEmpty()) {
            content.setGravity(Gravity.CENTER_HORIZONTAL);
            TextView art = Ui.text(requireContext(), "↺", 96, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false); art.setGravity(Gravity.CENTER);
            TextView title = Ui.text(requireContext(), "你的占卜紀錄會出現在這裡", 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true); title.setGravity(Gravity.CENTER);
            TextView body = Ui.text(requireContext(), "靜心凝神，透過易經尋求指引，記錄下每一次的智慧對話。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false); body.setGravity(Gravity.CENTER);
            Button start = Ui.pill(requireContext(), "✦ 開始第一次占卜", true); start.setOnClickListener(v -> activity.showQuestion());
            content.addView(art, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 180))); content.addView(title); Ui.addWithMargins(content, body, -1, -2, 0, 8, 0, 28); content.addView(start, new LinearLayout.LayoutParams(-1, Ui.dp(requireContext(), 52)));
        } else {
            content.addView(Ui.text(requireContext(), "占卜紀錄", 36, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
            for (DivinationRecord record : records) {
                Hexagram hex = HexagramRepository.get(record.hexagramNumber);
                LinearLayout card = Ui.card(requireContext());
                card.addView(Ui.text(requireContext(), "第" + hex.number + "卦｜" + hex.fullName, 22, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
                card.addView(Ui.text(requireContext(), record.question, 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
                card.addView(Ui.text(requireContext(), record.method.label + " · " + DateFormat.format("yyyy/MM/dd HH:mm", new Date(record.createdAt)), 12, android.graphics.Typeface.NORMAL, R.color.ic_outline_strong, false));
                if (record.note != null && !record.note.isEmpty()) card.addView(Ui.text(requireContext(), record.note, 15, android.graphics.Typeface.NORMAL, R.color.ic_gold, false));
                LinearLayout actions = Ui.row(requireContext());
                Button edit = Ui.pill(requireContext(), "編輯筆記", false);
                edit.setContentDescription("編輯第" + hex.number + "卦紀錄筆記");
                edit.setOnClickListener(v -> showEditDialog(recordStore, record));
                Button delete = Ui.pill(requireContext(), "刪除", false);
                delete.setContentDescription("刪除第" + hex.number + "卦紀錄");
                delete.setOnClickListener(v -> confirmDelete(recordStore, record));
                Ui.addWithMargins(actions, edit, 0, Ui.dp(requireContext(), 46), 0, 16, 6, 0);
                LinearLayout.LayoutParams editParams = (LinearLayout.LayoutParams) edit.getLayoutParams();
                editParams.weight = 1;
                Ui.addWithMargins(actions, delete, 0, Ui.dp(requireContext(), 46), 6, 16, 0, 0);
                LinearLayout.LayoutParams deleteParams = (LinearLayout.LayoutParams) delete.getLayoutParams();
                deleteParams.weight = 1;
                card.addView(actions, new LinearLayout.LayoutParams(-1, -2));
                card.setOnClickListener(v -> activity.showHexagramDetail(hex.number));
                Ui.addWithMargins(content, card, -1, -2, 0, 14, 0, 0);
            }
        }
        return Ui.pageWithChrome(activity, content, "紀錄");
    }

    private void showEditDialog(LocalRecordStore recordStore, DivinationRecord record) {
        EditText input = Ui.bottomInput(requireContext(), "補充這次占卜的反思...", 4);
        input.setText(record.note == null ? "" : record.note);
        input.setSelection(input.getText().length());
        input.setContentDescription("編輯占卜紀錄筆記");
        new AlertDialog.Builder(requireContext())
                .setTitle("編輯筆記")
                .setView(input)
                .setNegativeButton("取消", null)
                .setPositiveButton("儲存", (dialog, which) -> {
                    recordStore.updateNote(record.id, input.getText().toString());
                    Toast.makeText(requireContext(), "筆記已更新", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .show();
    }

    private void confirmDelete(LocalRecordStore recordStore, DivinationRecord record) {
        new AlertDialog.Builder(requireContext())
                .setTitle("刪除紀錄？")
                .setMessage("這筆占卜紀錄將從本機移除。")
                .setNegativeButton("取消", null)
                .setPositiveButton("刪除", (dialog, which) -> {
                    recordStore.delete(record.id);
                    Toast.makeText(requireContext(), "紀錄已刪除", Toast.LENGTH_SHORT).show();
                    refresh();
                })
                .show();
    }

    private void refresh() {
        getParentFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, new RecordsFragment())
                .commit();
    }
}
