package fcu.app.i_ching.ui;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

import fcu.app.i_ching.MainActivity;
import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;

public final class Ui {
    private Ui() {}

    public static int dp(Context context, int value) {
        return Math.round(value * context.getResources().getDisplayMetrics().density);
    }

    public static int color(Context context, @ColorRes int color) {
        return ContextCompat.getColor(context, color);
    }

    public static GradientDrawable bg(Context context, @ColorRes int fill, float radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color(context, fill));
        drawable.setCornerRadius(dp(context, (int) radiusDp));
        return drawable;
    }

    public static GradientDrawable strokeBg(Context context, @ColorRes int fill, @ColorRes int stroke, float radiusDp) {
        GradientDrawable drawable = bg(context, fill, radiusDp);
        drawable.setStroke(dp(context, 1), color(context, stroke));
        return drawable;
    }

    public static TextView text(Context context, String value, float sp, int style, @ColorRes int color, boolean serif) {
        TextView view = new TextView(context);
        view.setText(value);
        view.setTextColor(color(context, color));
        view.setTextSize(sp);
        view.setLineSpacing(dp(context, 3), 1.0f);
        view.setIncludeFontPadding(true);
        view.setTypeface(serif ? Typeface.SERIF : Typeface.SANS_SERIF, style);
        return view;
    }

    public static Button pill(Context context, String label, boolean primary) {
        Button button = new Button(context);
        button.setText(label);
        button.setAllCaps(false);
        button.setTextSize(14);
        button.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        button.setTextColor(color(context, primary ? R.color.ic_background : R.color.ic_ink));
        button.setBackground(primary
                ? bg(context, R.color.ic_ink, 999)
                : strokeBg(context, R.color.ic_background, R.color.ic_outline, 999));
        button.setMinHeight(dp(context, 48));
        button.setPadding(dp(context, 18), 0, dp(context, 18), 0);
        return button;
    }

    public static TextView chip(Context context, String label) {
        TextView chip = text(context, label, 13, Typeface.BOLD, R.color.ic_text_muted, false);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(context, 12), dp(context, 5), dp(context, 12), dp(context, 5));
        chip.setMinWidth(dp(context, 48));
        chip.setMinHeight(dp(context, 48));
        chip.setBackground(strokeBg(context, R.color.ic_surface_container_low, R.color.ic_outline, 999));
        return chip;
    }

    public static LinearLayout column(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        return layout;
    }

    public static LinearLayout row(Context context) {
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setGravity(Gravity.CENTER_VERTICAL);
        return layout;
    }

    public static void add(ViewGroup parent, View child, int width, int height) {
        parent.addView(child, new ViewGroup.LayoutParams(width, height));
    }

    public static void addWithMargins(LinearLayout parent, View child, int width, int height, int l, int t, int r, int b) {
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
        params.setMargins(dp(parent.getContext(), l), dp(parent.getContext(), t), dp(parent.getContext(), r), dp(parent.getContext(), b));
        parent.addView(child, params);
    }

    public static ScrollView scrollPage(Context context, LinearLayout content, boolean bottomNav) {
        ScrollView scroll = new ScrollView(context);
        scroll.setFillViewport(true);
        scroll.setBackgroundColor(color(context, R.color.ic_background));
        int bottom = bottomNav ? 96 : 32;
        content.setPadding(dp(context, 24), dp(context, 24), dp(context, 24), dp(context, bottom));
        scroll.addView(content, new ScrollView.LayoutParams(-1, -2));
        return scroll;
    }

    public static LinearLayout topBar(Context context, String left, View.OnClickListener leftClick, String right, View.OnClickListener rightClick) {
        LinearLayout bar = row(context);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(context, 16), dp(context, 24), dp(context, 16), dp(context, 8));
        TextView leftView = text(context, left, 24, Typeface.NORMAL, R.color.ic_ink, false);
        leftView.setGravity(Gravity.CENTER);
        leftView.setContentDescription("←".equals(left) ? "返回" : "選單");
        leftView.setOnClickListener(leftClick);
        TextView title = text(context, "I CHING", 28, Typeface.NORMAL, R.color.ic_ink, true);
        title.setGravity(Gravity.CENTER);
        title.setLetterSpacing(0.12f);
        TextView rightView = text(context, right, 24, Typeface.NORMAL, R.color.ic_ink, false);
        rightView.setGravity(Gravity.CENTER);
        rightView.setContentDescription("⚙".equals(right) ? "設定" : "切換收藏");
        rightView.setOnClickListener(rightClick);
        bar.addView(leftView, new LinearLayout.LayoutParams(dp(context, 48), dp(context, 48)));
        bar.addView(title, new LinearLayout.LayoutParams(0, -2, 1));
        bar.addView(rightView, new LinearLayout.LayoutParams(dp(context, 48), dp(context, 48)));
        return bar;
    }

    public static View pageWithChrome(MainActivity activity, LinearLayout content, String activeTab) {
        FrameLayout frame = new FrameLayout(activity);
        frame.setBackgroundColor(color(activity, R.color.ic_background));
        LinearLayout page = column(activity);
        page.addView(topBar(activity, "☰", v -> {}, "⚙", v -> activity.showProfile()), new LinearLayout.LayoutParams(-1, -2));
        page.addView(scrollPage(activity, content, true), new LinearLayout.LayoutParams(-1, 0, 1));
        frame.addView(page, new FrameLayout.LayoutParams(-1, -1));
        FrameLayout.LayoutParams navParams = new FrameLayout.LayoutParams(-1, dp(activity, 78), Gravity.BOTTOM);
        frame.addView(bottomNav(activity, activeTab), navParams);
        return frame;
    }

    public static LinearLayout bottomNav(MainActivity activity, String active) {
        LinearLayout nav = row(activity);
        nav.setGravity(Gravity.CENTER);
        nav.setPadding(dp(activity, 6), dp(activity, 8), dp(activity, 6), dp(activity, 10));
        nav.setBackground(strokeBg(activity, R.color.ic_surface, R.color.ic_outline, 20));
        addTab(activity, nav, "今日", "◎", active.equals("今日"), () -> activity.showDaily(false));
        addTab(activity, nav, "占卜", "✦", active.equals("占卜"), activity::showQuestion);
        addTab(activity, nav, "紀錄", "↺", active.equals("紀錄"), activity::showRecords);
        addTab(activity, nav, "學習", "書", active.equals("學習"), activity::showLearnCenter);
        addTab(activity, nav, "我的", "人", active.equals("我的"), activity::showProfile);
        return nav;
    }

    private static void addTab(Context context, LinearLayout nav, String label, String icon, boolean selected, Runnable action) {
        LinearLayout item = column(context);
        item.setId(bottomNavId(label));
        item.setGravity(Gravity.CENTER);
        item.setPadding(dp(context, 6), dp(context, 4), dp(context, 6), dp(context, 4));
        item.setBackground(selected ? bg(context, R.color.ic_gold_container, 18) : null);
        TextView iconView = text(context, icon, 18, Typeface.BOLD, selected ? R.color.ic_gold : R.color.ic_text_muted, false);
        iconView.setGravity(Gravity.CENTER);
        TextView labelView = text(context, label, 11, Typeface.BOLD, selected ? R.color.ic_gold : R.color.ic_text_muted, false);
        labelView.setGravity(Gravity.CENTER);
        item.addView(iconView, new LinearLayout.LayoutParams(-1, -2));
        item.addView(labelView, new LinearLayout.LayoutParams(-1, -2));
        item.setContentDescription(label + (selected ? "，目前分頁" : "分頁"));
        item.setOnClickListener(v -> action.run());
        nav.addView(item, new LinearLayout.LayoutParams(0, -1, 1));
    }

    private static int bottomNavId(String label) {
        if ("今日".equals(label)) return R.id.bottom_nav_daily;
        if ("占卜".equals(label)) return R.id.bottom_nav_divination;
        if ("紀錄".equals(label)) return R.id.bottom_nav_records;
        if ("學習".equals(label)) return R.id.bottom_nav_learn;
        if ("我的".equals(label)) return R.id.bottom_nav_profile;
        return View.NO_ID;
    }

    public static LinearLayout card(Context context) {
        LinearLayout card = column(context);
        card.setPadding(dp(context, 22), dp(context, 22), dp(context, 22), dp(context, 22));
        card.setBackground(strokeBg(context, R.color.ic_surface, R.color.ic_outline, 20));
        return card;
    }

    public static View hexagramView(Context context, Hexagram hexagram, int widthDp, int lineHeightDp, boolean useGoldForChanging) {
        return new HexagramView(context, hexagram, widthDp, lineHeightDp, useGoldForChanging);
    }

    public static EditText bottomInput(Context context, String hint, int minLines) {
        EditText edit = new EditText(context);
        edit.setHint(hint);
        edit.setTextColor(color(context, R.color.ic_ink));
        edit.setHintTextColor(color(context, R.color.ic_outline_strong));
        edit.setTextSize(16);
        edit.setMinLines(minLines);
        edit.setGravity(Gravity.TOP | Gravity.START);
        edit.setPadding(dp(context, 8), dp(context, 8), dp(context, 8), dp(context, 8));
        GradientDrawable underline = strokeBg(context, android.R.color.transparent, R.color.ic_outline, 0);
        edit.setBackground(underline);
        return edit;
    }

    public static LinearLayout chipsRow(Context context, String... labels) {
        LinearLayout wrapper = row(context);
        wrapper.setGravity(Gravity.CENTER);
        for (String label : labels) {
            addWithMargins(wrapper, chip(context, label), -2, -2, 4, 4, 4, 4);
        }
        return wrapper;
    }

    public static HorizontalScrollView horizontalChips(Context context, LinearLayout row) {
        HorizontalScrollView scroll = new HorizontalScrollView(context);
        scroll.setHorizontalScrollBarEnabled(false);
        scroll.addView(row);
        return scroll;
    }
}
