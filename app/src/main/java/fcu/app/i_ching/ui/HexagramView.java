package fcu.app.i_ching.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;

public class HexagramView extends View {
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Hexagram hexagram;
    private int widthDp = 72;
    private int lineHeightDp = 8;
    private boolean gold;

    public HexagramView(Context context) {
        super(context);
        init();
    }

    public HexagramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public HexagramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public HexagramView(Context context, Hexagram hexagram, int widthDp, int lineHeightDp, boolean gold) {
        super(context);
        init();
        configure(hexagram, widthDp, lineHeightDp, gold);
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void configure(Hexagram value, int widthDp, int lineHeightDp, boolean gold) {
        this.hexagram = value;
        this.widthDp = widthDp;
        this.lineHeightDp = lineHeightDp;
        this.gold = gold;
        if (value != null) {
            setContentDescription("第" + value.number + "卦 " + value.fullName);
        }
        requestLayout();
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = Ui.dp(getContext(), widthDp);
        int desiredHeight = Ui.dp(getContext(), 8 + (lineHeightDp + 6) * 6);
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (hexagram == null) return;
        paint.setColor(Ui.color(getContext(), gold ? R.color.ic_gold : R.color.ic_ink));
        float targetWidth = Math.min(getWidth(), Ui.dp(getContext(), widthDp));
        float left = (getWidth() - targetWidth) / 2f;
        float lineHeight = Ui.dp(getContext(), lineHeightDp);
        float gap = Ui.dp(getContext(), 6);
        float y = Ui.dp(getContext(), 4);
        float radius = Ui.dp(getContext(), 2);
        float brokenGap = Math.max(Ui.dp(getContext(), 8), targetWidth / 5f);
        for (int i = 5; i >= 0; i--) {
            boolean yang = hexagram.linesBottomToTop[i];
            if (yang) {
                canvas.drawRoundRect(left, y, left + targetWidth, y + lineHeight, radius, radius, paint);
            } else {
                float segmentWidth = (targetWidth - brokenGap) / 2f;
                canvas.drawRoundRect(left, y, left + segmentWidth, y + lineHeight, radius, radius, paint);
                canvas.drawRoundRect(left + segmentWidth + brokenGap, y, left + targetWidth, y + lineHeight, radius, radius, paint);
            }
            y += lineHeight + gap;
        }
    }
}
