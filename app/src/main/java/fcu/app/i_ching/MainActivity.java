package fcu.app.i_ching;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.annotation.IdRes;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import fcu.app.i_ching.data.DivinationMethod;
import fcu.app.i_ching.data.DivinationResult;
import fcu.app.i_ching.data.RecordRepository;
import fcu.app.i_ching.data.SettingsStore;

public class MainActivity extends AppCompatActivity {
    private SettingsStore settingsStore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        settingsStore = new SettingsStore(this);
        AppCompatDelegate.setDefaultNightMode(settingsStore.isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        RecordRepository.get(this).migrateFromLegacyPrefsIfNeeded(null);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, 0, systemBars.right, 0);
            return insets;
        });
    }

    public SettingsStore settings() { return settingsStore; }

    public void completeOnboarding() {
        settingsStore.setOnboardingComplete(true);
        navigateClearingStack(R.id.authFragment, null);
    }

    public void enterLocalMode() {
        settingsStore.setOnboardingComplete(true);
        navigateClearingStack(R.id.dailyFragment, null);
    }

    public void routeAfterSplash() {
        navigateClearingStack(settingsStore.isOnboardingComplete() ? R.id.dailyFragment : R.id.onboardingFragment, null);
    }

    public void showDaily(boolean addToBackStack) {
        if (addToBackStack) navigate(R.id.dailyFragment, null);
        else navigateTopLevel(R.id.dailyFragment, null);
    }

    public void showQuestion() { navigateTopLevel(R.id.questionFragment, null); }

    public void showMethod(String question) {
        navigate(R.id.methodFragment, NavigationArgs.method(question));
    }

    public void showRitual(DivinationMethod method) {
        showRitual(NavigationArgs.DEFAULT_QUESTION, method);
    }

    public void showRitual(String question, DivinationMethod method) {
        navigate(R.id.ritualFragment, NavigationArgs.ritual(question, method));
    }

    public void showResult() {
        showResult(NavigationArgs.DEFAULT_QUESTION, DivinationMethod.COINS);
    }

    public void showResult(String question, DivinationMethod method) {
        DivinationResult result = DivinationResult.create(
                NavigationArgs.normalizeQuestion(question),
                method == null ? DivinationMethod.COINS : method
        );
        navigate(R.id.resultFragment, NavigationArgs.result(result));
    }

    public void showRecords() { navigateTopLevel(R.id.recordsFragment, null); }
    public void showLearnCenter() { navigateTopLevel(R.id.learnCenterFragment, null); }

    public void showHexagramDetail(int number) {
        navigate(R.id.hexagramDetailFragment, NavigationArgs.hexagramDetail(number));
    }

    public void showProfile() { navigateTopLevel(R.id.profileSettingsFragment, null); }

    private void navigate(@IdRes int destinationId, @Nullable Bundle args) {
        NavController controller = navController();
        if (controller.getCurrentDestination() != null
                && controller.getCurrentDestination().getId() == destinationId) {
            return;
        }
        controller.navigate(destinationId, args);
    }

    private void navigateTopLevel(@IdRes int destinationId, @Nullable Bundle args) {
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.dailyFragment, false)
                .build();
        navController().navigate(destinationId, args, options);
    }

    private void navigateClearingStack(@IdRes int destinationId, @Nullable Bundle args) {
        NavOptions options = new NavOptions.Builder()
                .setLaunchSingleTop(true)
                .setPopUpTo(R.id.main_graph, true)
                .build();
        navController().navigate(destinationId, args, options);
    }

    private NavController navController() {
        NavHostFragment host = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        if (host == null) throw new IllegalStateException("NavHostFragment is not attached");
        return host.getNavController();
    }

}
