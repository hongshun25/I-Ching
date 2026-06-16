package fcu.app.i_ching.ui;

import android.view.MenuItem;

import androidx.core.view.MenuItemCompat;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.databinding.IncludeBottomNavBinding;
import fcu.app.i_ching.databinding.IncludeTopBarBinding;

final class NavigationChrome {
    static final String TAB_DAILY = "今日";
    static final String TAB_DIVINATION = "占卜";
    static final String TAB_RECORDS = "紀錄";
    static final String TAB_LEARN = "學習";
    static final String TAB_PROFILE = "我的";

    private NavigationChrome() {}

    static void bind(MainActivity activity, IncludeTopBarBinding topBar,
                     IncludeBottomNavBinding bottomNav, String activeTab) {
        bindTopBar(activity, topBar);
        bindBottomNav(activity, bottomNav, activeTab);
    }

    private static void bindTopBar(MainActivity activity, IncludeTopBarBinding topBar) {
        MaterialToolbar toolbar = topBar.getRoot();
        toolbar.setTitle(R.string.brand_title);
        toolbar.setNavigationIcon(R.drawable.ic_menu_24);
        toolbar.setNavigationContentDescription(R.string.nav_menu);
        toolbar.setNavigationOnClickListener(v -> {});
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.top_bar_settings);
        MenuItem settings = toolbar.getMenu().findItem(R.id.top_bar_right_action);
        if (settings != null) {
            MenuItemCompat.setContentDescription(settings, activity.getString(R.string.nav_settings));
        }
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.top_bar_right_action) {
                activity.showProfile();
                return true;
            }
            return false;
        });
    }

    private static void bindBottomNav(MainActivity activity, IncludeBottomNavBinding bottomNav,
                                      String activeTab) {
        BottomNavigationView navigation = bottomNav.getRoot();
        navigation.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.bottom_nav_daily) {
                if (!TAB_DAILY.equals(activeTab)) activity.showDaily(false);
                return true;
            }
            if (id == R.id.bottom_nav_divination) {
                if (!TAB_DIVINATION.equals(activeTab)) activity.showQuestion();
                return true;
            }
            if (id == R.id.bottom_nav_records) {
                if (!TAB_RECORDS.equals(activeTab)) activity.showRecords();
                return true;
            }
            if (id == R.id.bottom_nav_learn) {
                if (!TAB_LEARN.equals(activeTab)) activity.showLearnCenter();
                return true;
            }
            if (id == R.id.bottom_nav_profile) {
                if (!TAB_PROFILE.equals(activeTab)) activity.showProfile();
                return true;
            }
            return false;
        });
        navigation.setSelectedItemId(tabId(activeTab));
    }

    private static int tabId(String activeTab) {
        if (TAB_DIVINATION.equals(activeTab)) return R.id.bottom_nav_divination;
        if (TAB_RECORDS.equals(activeTab)) return R.id.bottom_nav_records;
        if (TAB_LEARN.equals(activeTab)) return R.id.bottom_nav_learn;
        if (TAB_PROFILE.equals(activeTab)) return R.id.bottom_nav_profile;
        return R.id.bottom_nav_daily;
    }
}
