package fcu.app.i_ching;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.SettingsStore;
import fcu.app.i_ching.ui.AuthFragment;
import fcu.app.i_ching.ui.DailyFragment;
import fcu.app.i_ching.ui.HexagramDetailFragment;
import fcu.app.i_ching.ui.LearnCenterFragment;
import fcu.app.i_ching.ui.MethodFragment;
import fcu.app.i_ching.ui.OnboardingFragment;
import fcu.app.i_ching.ui.ProfileSettingsFragment;
import fcu.app.i_ching.ui.QuestionFragment;
import fcu.app.i_ching.ui.RecordsFragment;
import fcu.app.i_ching.ui.ResultFragment;
import fcu.app.i_ching.ui.RitualFragment;
import fcu.app.i_ching.ui.SplashFragment;

public class MainActivity extends AppCompatActivity {
    private String pendingQuestion = "我目前在工作上最需要調整的是什麼？";
    private DivinationMethod pendingMethod = DivinationMethod.COINS;
    private SettingsStore settingsStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsStore = new SettingsStore(this);
        AppCompatDelegate.setDefaultNightMode(settingsStore.isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });
        if (savedInstanceState == null) {
            replace(new SplashFragment(), false);
        }
    }

    public SettingsStore settings() { return settingsStore; }

    public void completeOnboarding() {
        settingsStore.setOnboardingComplete(true);
        replace(new AuthFragment(), true);
    }

    public void enterLocalMode() {
        settingsStore.setOnboardingComplete(true);
        showDaily(false);
    }

    public void routeAfterSplash() {
        replace(settingsStore.isOnboardingComplete() ? new DailyFragment() : new OnboardingFragment(), false);
    }

    public void showDaily(boolean addToBackStack) { replace(new DailyFragment(), addToBackStack); }
    public void showQuestion() { replace(new QuestionFragment(), true); }

    public void showMethod(String question) {
        pendingQuestion = question == null || question.trim().isEmpty()
                ? "我目前在工作上最需要調整的是什麼？"
                : question.trim();
        replace(new MethodFragment(), true);
    }

    public void showRitual(DivinationMethod method) {
        pendingMethod = method;
        replace(new RitualFragment(), true);
    }

    public void showResult() {
        DivinationResult result = DivinationResult.create(pendingQuestion, pendingMethod);
        replace(ResultFragment.newInstance(result), true);
    }

    public void showRecords() { replace(new RecordsFragment(), true); }
    public void showLearnCenter() { replace(new LearnCenterFragment(), true); }
    public void showHexagramDetail(int number) { replace(HexagramDetailFragment.newInstance(number), true); }
    public void showProfile() { replace(new ProfileSettingsFragment(), true); }

    private void replace(Fragment fragment, boolean addToBackStack) {
        androidx.fragment.app.FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment);
        if (addToBackStack) { transaction.addToBackStack(fragment.getClass().getSimpleName()); }
        transaction.commit();
    }
}
