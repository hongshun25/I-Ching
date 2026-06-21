package fcu.app.i_ching;

import android.content.Context;
import android.content.res.Configuration;
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
import fcu.app.i_ching.data.AccountStore;

public class MainActivity extends AppCompatActivity {
    private SettingsStore settingsStore;
    private AccountStore accountStore;

    @Override
    protected void attachBaseContext(Context newBase) {
        SettingsStore store = new SettingsStore(newBase);
        Configuration configuration = new Configuration(newBase.getResources().getConfiguration());
        configuration.fontScale = configuration.fontScale * store.fontScale().multiplier;
        super.attachBaseContext(newBase.createConfigurationContext(configuration));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        accountStore = AccountStore.get(this);
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
    public AccountStore accounts() { return accountStore; }

    public void completeOnboarding() {
        settingsStore.setOnboardingComplete(true);
        navigateClearingStack(R.id.authFragment, null);
    }

    public void enterLocalMode() {
        accountStore.useGuest();
        settingsStore.setOnboardingComplete(true);
        applyNightMode();
        navigateClearingStack(R.id.dailyFragment, null);
    }

    public void enterAuthenticatedMode() {
        settingsStore.setOnboardingComplete(true);
        applyNightMode();
        navigateClearingStack(R.id.dailyFragment, null);
    }

    public void routeAfterSplash() {
        navigateClearingStack(settingsStore.isOnboardingComplete() ? R.id.dailyFragment : R.id.onboardingFragment, null);
    }

    public void showAuth() {
        applyNightMode();
        navigateClearingStack(R.id.authFragment, null);
    }

    public void showDaily(boolean addToBackStack) {
        if (addToBackStack) navigate(R.id.dailyFragment, null);
        else navigateTopLevel(R.id.dailyFragment, null);
    }

    public void showQuestion() { showQuestion(""); }

    public void showQuestion(String draft) {
        navigateTopLevel(R.id.questionFragment, NavigationArgs.questionDraft(draft));
    }

    public void showMethod(String question) {
        navigate(R.id.methodFragment, NavigationArgs.method(question));
    }

    public void showRitual(DivinationMethod method) {
        showRitual(NavigationArgs.DEFAULT_QUESTION, method);
    }

    public void showRitual(String question, DivinationMethod method) {
        navigate(R.id.ritualFragment, NavigationArgs.ritual(question, method));
    }

    public void showYarrowCasting(String question) {
        navigate(R.id.yarrowCastingFragment, NavigationArgs.method(question));
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

    public void showResult(DivinationResult result) {
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

    private void applyNightMode() {
        AppCompatDelegate.setDefaultNightMode(settingsStore.isDarkMode()
                ? AppCompatDelegate.MODE_NIGHT_YES
                : AppCompatDelegate.MODE_NIGHT_NO);
    }

}
