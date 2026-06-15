package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.data.SettingsStore;

public class LearnCenterFragment extends Fragment {
    private static final String STATE_FILTER = "filter";
    private static final String STATE_QUERY = "query";

    private final List<TextView> filterChips = new ArrayList<>();
    private String activeFilter = HexagramRepository.FILTER_ALL;
    private EditText searchInput;
    private LinearLayout listContainer;

    @Nullable @Override
    public View onCreateView(@NonNull android.view.LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        filterChips.clear();
        if (savedInstanceState != null) {
            activeFilter = savedInstanceState.getString(STATE_FILTER, HexagramRepository.FILTER_ALL);
        }
        LinearLayout content = Ui.column(requireContext());
        content.addView(Ui.text(requireContext(), "六十四卦", 38, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        content.addView(Ui.text(requireContext(), "探索易經六十四卦的深層智慧，每一卦皆象徵自然與人生的變化規律。", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        searchInput = Ui.bottomInput(requireContext(), "搜尋卦名或關鍵字...", 1);
        searchInput.setContentDescription("搜尋六十四卦");
        if (savedInstanceState != null) searchInput.setText(savedInstanceState.getString(STATE_QUERY, ""));
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { renderList(activity); }
            @Override public void afterTextChanged(Editable s) {}
        });
        Ui.addWithMargins(content, searchInput, -1, Ui.dp(requireContext(), 54), 0, 24, 0, 14);
        LinearLayout filters = Ui.row(requireContext());
        filters.setGravity(Gravity.START);
        addFilterChip(filters, HexagramRepository.FILTER_ALL, activity);
        addFilterChip(filters, HexagramRepository.FILTER_UPPER_CANON, activity);
        addFilterChip(filters, HexagramRepository.FILTER_LOWER_CANON, activity);
        addFilterChip(filters, HexagramRepository.FILTER_FAVORITES, activity);
        content.addView(Ui.horizontalChips(requireContext(), filters));
        listContainer = Ui.column(requireContext());
        content.addView(listContainer, new LinearLayout.LayoutParams(-1, -2));
        updateChipStyles();
        renderList(activity);
        return Ui.pageWithChrome(activity, content, "學習");
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(STATE_FILTER, activeFilter);
        if (searchInput != null) outState.putString(STATE_QUERY, searchInput.getText().toString());
    }

    private void addFilterChip(LinearLayout filters, String label, MainActivity activity) {
        TextView chip = Ui.chip(requireContext(), label);
        chip.setContentDescription("篩選" + label);
        chip.setOnClickListener(v -> {
            activeFilter = label;
            updateChipStyles();
            renderList(activity);
        });
        filterChips.add(chip);
        Ui.addWithMargins(filters, chip, -2, -2, 0, 0, 8, 0);
    }

    private void updateChipStyles() {
        for (TextView chip : filterChips) {
            boolean selected = activeFilter.contentEquals(chip.getText());
            chip.setTextColor(Ui.color(requireContext(), selected ? R.color.ic_background : R.color.ic_text_muted));
            chip.setBackground(selected
                    ? Ui.bg(requireContext(), R.color.ic_ink, 999)
                    : Ui.strokeBg(requireContext(), R.color.ic_surface_container_low, R.color.ic_outline, 999));
            chip.setSelected(selected);
        }
    }

    private void renderList(MainActivity activity) {
        if (listContainer == null || searchInput == null) return;
        listContainer.removeAllViews();
        List<Hexagram> hexagrams = HexagramRepository.filter(
                searchInput.getText().toString(),
                activeFilter,
                activity.settings().favoriteHexagrams()
        );
        if (hexagrams.isEmpty()) {
            TextView empty = Ui.text(requireContext(), "沒有符合的卦象", 16, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false);
            empty.setGravity(Gravity.CENTER);
            Ui.addWithMargins(listContainer, empty, -1, -2, 0, 28, 0, 0);
            return;
        }
        for (Hexagram hex : hexagrams) addHexagramCard(activity, listContainer, hex);
    }

    private void addHexagramCard(MainActivity activity, LinearLayout parent, Hexagram hex) {
        SettingsStore settings = activity.settings();
        LinearLayout card = Ui.card(requireContext());
        LinearLayout row = Ui.row(requireContext());
        row.addView(Ui.hexagramView(requireContext(), hex, 34, 4, false), new LinearLayout.LayoutParams(Ui.dp(requireContext(), 46), -2));
        LinearLayout texts = Ui.column(requireContext());
        texts.addView(Ui.text(requireContext(), "第" + hex.number + "卦", 12, android.graphics.Typeface.NORMAL, R.color.ic_text_muted, false));
        texts.addView(Ui.text(requireContext(), hex.name, 24, android.graphics.Typeface.NORMAL, R.color.ic_ink, true));
        row.addView(texts, new LinearLayout.LayoutParams(0, -2, 1));
        boolean favorite = settings.isFavorite(hex.number);
        TextView fav = Ui.text(requireContext(), favorite ? "♥" : "♡", 26, android.graphics.Typeface.NORMAL, favorite ? R.color.ic_gold : R.color.ic_outline_strong, false);
        fav.setGravity(Gravity.CENTER);
        updateFavoriteDescription(fav, hex.number, favorite);
        fav.setOnClickListener(v -> {
            boolean on = settings.toggleFavorite(hex.number);
            if (HexagramRepository.FILTER_FAVORITES.equals(activeFilter)) {
                renderList(activity);
            } else {
                fav.setText(on ? "♥" : "♡");
                fav.setTextColor(Ui.color(requireContext(), on ? R.color.ic_gold : R.color.ic_outline_strong));
                updateFavoriteDescription(fav, hex.number, on);
            }
        });
        row.addView(fav, new LinearLayout.LayoutParams(Ui.dp(requireContext(), 48), Ui.dp(requireContext(), 48)));
        card.addView(row);
        card.addView(Ui.chipsRow(requireContext(), hex.tags.toArray(new String[0])));
        card.setOnClickListener(v -> activity.showHexagramDetail(hex.number));
        Ui.addWithMargins(parent, card, -1, -2, 0, 14, 0, 0);
    }

    private void updateFavoriteDescription(TextView view, int hexagramNumber, boolean favorite) {
        view.setContentDescription((favorite ? "取消收藏" : "加入收藏") + "第" + hexagramNumber + "卦");
    }
}
