package fcu.app.i_ching.ui;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

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
        topBar.topBarLeftAction.setText("☰");
        topBar.topBarLeftAction.setContentDescription(activity.getString(R.string.nav_menu));
        topBar.topBarLeftAction.setOnClickListener(v -> {});
        topBar.topBarTitle.setText(R.string.brand_title);
        topBar.topBarRightAction.setText("⚙");
        topBar.topBarRightAction.setContentDescription(activity.getString(R.string.nav_settings));
        topBar.topBarRightAction.setOnClickListener(v -> activity.showProfile());

        bindTab(activity, bottomNav.bottomNavDaily, TAB_DAILY, "◎",
                TAB_DAILY.equals(activeTab), () -> activity.showDaily(false));
        bindTab(activity, bottomNav.bottomNavDivination, TAB_DIVINATION, "✦",
                TAB_DIVINATION.equals(activeTab), activity::showQuestion);
        bindTab(activity, bottomNav.bottomNavRecords, TAB_RECORDS, "↺",
                TAB_RECORDS.equals(activeTab), activity::showRecords);
        bindTab(activity, bottomNav.bottomNavLearn, TAB_LEARN, "書",
                TAB_LEARN.equals(activeTab), activity::showLearnCenter);
        bindTab(activity, bottomNav.bottomNavProfile, TAB_PROFILE, "人",
                TAB_PROFILE.equals(activeTab), activity::showProfile);
    }

    private static void bindTab(Context context, LinearLayout item, String label, String icon,
                                boolean selected, Runnable action) {
        TextView iconView = (TextView) item.getChildAt(0);
        TextView labelView = (TextView) item.getChildAt(1);
        item.setBackground(selected ? ContextCompat.getDrawable(context, R.drawable.bg_bottom_tab_selected) : null);
        iconView.setText(icon);
        iconView.setTextColor(Ui.color(context, selected ? R.color.ic_gold : R.color.ic_text_muted));
        labelView.setText(label);
        labelView.setTextColor(Ui.color(context, selected ? R.color.ic_gold : R.color.ic_text_muted));
        item.setContentDescription(label + (selected ? "，目前分頁" : "分頁"));
        item.setOnClickListener(v -> action.run());
    }
}
