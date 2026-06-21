package fcu.app.i_ching.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import fcu.app.i_ching.R;

public class RitualFocusView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF arcBounds = new RectF();
    private float progressFraction;

    public RitualFocusView(Context context) {
        super(context);
        init();
    }

    public RitualFocusView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RitualFocusView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint.setStrokeCap(Paint.Cap.ROUND);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void setProgressFraction(float progressFraction) {
        this.progressFraction = clampProgress(progressFraction);
        invalidate();
    }

    public float progressFraction() {
        return progressFraction;
    }

    static float clampProgressForTest(float progressFraction) {
        return clampProgress(progressFraction);
    }

    private static float clampProgress(float progressFraction) {
        return Math.max(0f, Math.min(1f, progressFraction));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float cx = getWidth() / 2f;
        float cy = getHeight() / 2f;
        float radius = Math.min(getWidth(), getHeight()) / 2f - dp(8);

        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color(R.color.ic_surface_container_low));
        paint.setAlpha(120);
        canvas.drawCircle(cx, cy, radius * (0.82f + 0.08f * progressFraction), paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(1));
        paint.setColor(color(R.color.ic_outline_variant));
        paint.setAlpha(150);
        canvas.drawCircle(cx, cy, radius * 0.76f, paint);

        paint.setStrokeWidth(dp(4));
        paint.setColor(color(R.color.ic_gold));
        paint.setAlpha(230);
        float ringRadius = radius * 0.76f;
        arcBounds.set(cx - ringRadius, cy - ringRadius, cx + ringRadius, cy + ringRadius);
        canvas.drawArc(arcBounds, -90f, 360f * progressFraction, false, paint);

        float innerRadius = radius * (0.18f + 0.16f * progressFraction);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color(R.color.ic_gold_container));
        paint.setAlpha(170);
        canvas.drawCircle(cx, cy, innerRadius, paint);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(dp(2));
        paint.setColor(color(R.color.ic_gold));
        paint.setAlpha(220);
        canvas.drawCircle(cx, cy, innerRadius, paint);

        drawCenterHexagram(canvas, cx, cy, innerRadius);
        paint.setAlpha(255);
    }

    private void drawCenterHexagram(Canvas canvas, float cx, float cy, float radius) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(color(R.color.ic_ink));
        paint.setAlpha(170);
        float width = radius * 0.95f;
        float left = cx - width / 2f;
        float height = Math.max(dp(2), radius * 0.08f);
        float gap = height * 0.8f;
        float top = cy - (height * 6f + gap * 5f) / 2f;
        float brokenGap = width * 0.28f;
        for (int i = 0; i < 6; i++) {
            float y = top + i * (height + gap);
            if (i == 3) {
                canvas.drawRoundRect(left, y, left + width, y + height, height, height, paint);
            } else {
                float segment = (width - brokenGap) / 2f;
                canvas.drawRoundRect(left, y, left + segment, y + height, height, height, paint);
                canvas.drawRoundRect(left + segment + brokenGap, y, left + width, y + height, height, height, paint);
            }
        }
    }

    private int color(int colorRes) {
        return ContextCompat.getColor(getContext(), colorRes);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
