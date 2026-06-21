package fcu.app.i_ching.ui;

import android.view.View;
import android.view.ViewGroup;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public final class InsetsHelper {
    private InsetsHelper() {}

    public static void applyFullscreenInsets(View view) {
        applyInsets(view, true, true, true);
    }

    public static void applyFullscreenScrollInsets(ViewGroup scrollView) {
        scrollView.setClipToPadding(false);
        applyFullscreenInsets(scrollView);
    }

    public static void applyHorizontalInsets(View view) {
        applyInsets(view, true, false, false);
    }

    public static void applyBottomInsets(View view) {
        applyInsets(view, true, false, true);
    }

    private static void applyInsets(View view, boolean horizontal, boolean top, boolean bottom) {
        int start = ViewCompat.getPaddingStart(view);
        int topPadding = view.getPaddingTop();
        int end = ViewCompat.getPaddingEnd(view);
        int bottomPadding = view.getPaddingBottom();
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, insets) -> {
            Insets bars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            ViewCompat.setPaddingRelative(
                    v,
                    start + (horizontal ? bars.left : 0),
                    topPadding + (top ? bars.top : 0),
                    end + (horizontal ? bars.right : 0),
                    bottomPadding + (bottom ? bars.bottom : 0)
            );
            return insets;
        });
        ViewCompat.requestApplyInsets(view);
    }
}
