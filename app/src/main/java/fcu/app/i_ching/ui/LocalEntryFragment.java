package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.databinding.FragmentLocalEntryBinding;

public class LocalEntryFragment extends Fragment {
    private FragmentLocalEntryBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) requireActivity();
        binding = FragmentLocalEntryBinding.inflate(inflater, container, false);
        InsetsHelper.applyFullscreenScrollInsets(binding.getRoot());
        binding.localModeButton.setOnClickListener(v -> activity.enterLocalMode());
        binding.localEntryDailyPreviewButton.setOnClickListener(v -> activity.enterLocalMode());
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        binding = null;
        super.onDestroyView();
    }
}
