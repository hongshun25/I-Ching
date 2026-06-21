package fcu.app.i_ching.ui;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.chip.Chip;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DailyInsightProvider;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.databinding.FragmentDailyBinding;
import fcu.app.i_ching.ui.presentation.DailyCardPresentation;

public class DailyFragment extends Fragment {
    private FragmentDailyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = FragmentDailyBinding.inflate(inflater, container, false);
        boolean night = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;
        DailyInsightProvider.DailyInsight insight = new DailyInsightProvider().today();
        Hexagram hexagram = insight.hexagram;
        NavigationChrome.bind(activity, binding.topBar, binding.bottomNav, NavigationChrome.TAB_DAILY);
        bindDaily(activity, hexagram, DailyCardPresentation.from(hexagram, insight.dateText, night));
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bindDaily(MainActivity activity, Hexagram hexagram, DailyCardPresentation presentation) {
        binding.dailyGreeting.setText(presentation.greetingText);
        binding.dailyGreeting.setGravity(presentation.centeredGreeting ? Gravity.CENTER : Gravity.START);
        binding.dailyGreeting.setTextColor(ContextCompat.getColor(requireContext(),
                presentation.centeredGreeting ? R.color.ic_gold : R.color.ic_text_muted));
        binding.dailyGreeting.setTextSize(presentation.centeredGreeting ? 14 : 18);

        binding.dailyChips.removeAllViews();
        addChip(binding.dailyChips, hexagram.upper);
        addChip(binding.dailyChips, hexagram.lower);
        ViewGroup.LayoutParams params = binding.dailyHexagram.getLayoutParams();
        params.width = dp(presentation.hexagramWidthDp);
        binding.dailyHexagram.setLayoutParams(params);
        binding.dailyHexagram.configure(hexagram, presentation.hexagramWidthDp, presentation.lineHeightDp, false);
        binding.dailyTitle.setText(presentation.titleText);
        binding.dailyJudgment.setText(presentation.judgmentText);
        binding.dailyJudgment.setTextSize(presentation.centeredGreeting ? 18 : 24);
        binding.dailySummary.setText(presentation.summaryText);
        binding.dailyDoItems.setText(String.join("\n", hexagram.doItems));
        binding.dailyAvoidItems.setText(String.join("\n", hexagram.avoidItems));

        binding.dailyCastButton.setOnClickListener(v -> activity.showQuestion());
        binding.dailyRecordsButton.setOnClickListener(v -> activity.showRecords());
        binding.dailyLearnButton.setOnClickListener(v -> activity.showHexagramDetail(hexagram.number));
    }

    private void addChip(ViewGroup parent, String label) {
        Chip chip = (Chip) getLayoutInflater().inflate(R.layout.item_filter_chip, parent, false);
        chip.setText(label);
        parent.addView(chip);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
