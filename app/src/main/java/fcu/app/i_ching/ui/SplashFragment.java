package fcu.app.i_ching.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.data.HexagramRepository;
import fcu.app.i_ching.databinding.FragmentSplashBinding;

public class SplashFragment extends Fragment {
    private final Handler handler = new Handler(Looper.getMainLooper());
    private FragmentSplashBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentSplashBinding.inflate(inflater, container, false);
        binding.splashHexagram.configure(HexagramRepository.get(15), 108, 10, false);
        InsetsHelper.applyFullscreenInsets(binding.getRoot());
        handler.postDelayed(() -> {
            if (isAdded()) ((MainActivity) requireActivity()).routeAfterSplash();
        }, 3500);
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        handler.removeCallbacksAndMessages(null);
        binding = null;
        super.onDestroyView();
    }
}
