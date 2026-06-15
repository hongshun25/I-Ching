package fcu.app.i_ching.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.core.content.ContextCompat;

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

    public static TextView chip(Context context, String label) {
        TextView chip = new TextView(context);
        chip.setText(label);
        chip.setTextColor(color(context, R.color.ic_text_muted));
        chip.setTextSize(13);
        chip.setLineSpacing(dp(context, 3), 1.0f);
        chip.setIncludeFontPadding(true);
        chip.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
        chip.setGravity(Gravity.CENTER);
        chip.setPadding(dp(context, 12), dp(context, 5), dp(context, 12), dp(context, 5));
        chip.setMinWidth(dp(context, 48));
        chip.setMinHeight(dp(context, 48));
        chip.setBackground(strokeBg(context, R.color.ic_surface_container_low, R.color.ic_outline, 999));
        return chip;
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
}
