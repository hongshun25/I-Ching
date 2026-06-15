package fcu.app.i_ching.ui;

import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;
import fcu.app.i_ching.data.HexagramRepository;
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
        Hexagram hexagram = HexagramRepository.get(night ? 29 : 15);
        bindDaily(activity, hexagram, DailyCardPresentation.from(hexagram, night));
        return Ui.pageWithChrome(activity, binding.getRoot(), "今日");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void bindDaily(MainActivity activity, Hexagram hexagram, DailyCardPresentation presentation) {
        binding.dailyGreeting.setText(presentation.greetingText);
        binding.dailyGreeting.setGravity(presentation.centeredGreeting ? Gravity.CENTER : Gravity.START);
        binding.dailyGreeting.setTextColor(Ui.color(requireContext(),
                presentation.centeredGreeting ? R.color.ic_gold : R.color.ic_text_muted));
        binding.dailyGreeting.setTextSize(presentation.centeredGreeting ? 14 : 18);

        binding.dailyChips.removeAllViews();
        addChip(binding.dailyChips, hexagram.upper);
        addChip(binding.dailyChips, hexagram.lower);
        ViewGroup.LayoutParams params = binding.dailyHexagram.getLayoutParams();
        params.width = Ui.dp(requireContext(), presentation.hexagramWidthDp);
        binding.dailyHexagram.setLayoutParams(params);
        binding.dailyHexagram.configure(hexagram, presentation.hexagramWidthDp, presentation.lineHeightDp, false);
        binding.dailyTitle.setText(presentation.titleText);
        binding.dailyJudgment.setText(presentation.judgmentText);
        binding.dailyJudgment.setTextSize(presentation.centeredGreeting ? 18 : 24);
        binding.dailySummary.setText(presentation.summaryText);

        binding.dailyCastButton.setOnClickListener(v -> activity.showQuestion());
        binding.dailyRecordsButton.setOnClickListener(v -> activity.showRecords());
        binding.dailyLearnButton.setOnClickListener(v -> activity.showHexagramDetail(hexagram.number));
    }

    private void addChip(ViewGroup parent, String label) {
        TextView chip = Ui.chip(requireContext(), label);
        chip.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        parent.addView(chip);
    }
}
