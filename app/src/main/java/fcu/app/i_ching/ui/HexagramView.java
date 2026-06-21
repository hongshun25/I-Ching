package fcu.app.i_ching.ui;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.List;

import fcu.app.i_ching.R;
import fcu.app.i_ching.data.Hexagram;

public class HexagramView extends View {
    private static final boolean[] EDIT_MODE_LINES = {false, false, true, false, false, false};

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Hexagram hexagram;
    private int widthDp = 72;
    private int lineHeightDp = 8;
    private int lineGapDp = 6;
    private boolean gold;
    private boolean inkStyle;
    private int lineColor;
    private int changingLineColor;
    private final boolean[] changingLinePositions = new boolean[6];

    public HexagramView(Context context) {
        super(context);
        init(null);
    }

    public HexagramView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public HexagramView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public HexagramView(Context context, Hexagram hexagram, int widthDp, int lineHeightDp, boolean gold) {
        super(context);
        init(null);
        configure(hexagram, widthDp, lineHeightDp, gold);
    }

    private void init(@Nullable AttributeSet attrs) {
        lineColor = color(R.color.ic_ink);
        changingLineColor = color(R.color.ic_gold);
        if (attrs != null) {
            TypedArray values = getContext().obtainStyledAttributes(attrs, R.styleable.HexagramView);
            widthDp = values.getInt(R.styleable.HexagramView_hexagramWidthDp, widthDp);
            lineHeightDp = values.getInt(R.styleable.HexagramView_hexagramLineHeightDp, lineHeightDp);
            lineGapDp = values.getInt(R.styleable.HexagramView_hexagramLineGapDp, lineGapDp);
            gold = values.getBoolean(R.styleable.HexagramView_hexagramGoldLines, gold);
            inkStyle = values.getBoolean(R.styleable.HexagramView_hexagramInkStyle, inkStyle);
            lineColor = values.getColor(R.styleable.HexagramView_hexagramLineColor, lineColor);
            changingLineColor = values.getColor(R.styleable.HexagramView_hexagramChangingLineColor, changingLineColor);
            values.recycle();
        }
        paint.setStyle(Paint.Style.FILL);
        setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
    }

    public void configure(Hexagram value, int widthDp, int lineHeightDp, boolean gold) {
        this.hexagram = value;
        this.widthDp = widthDp;
        this.lineHeightDp = lineHeightDp;
        this.gold = gold;
        for (int i = 0; i < changingLinePositions.length; i++) {
            changingLinePositions[i] = false;
        }
        if (value != null) {
            setContentDescription("第" + value.number + "卦 " + value.fullName);
        }
        requestLayout();
        invalidate();
    }

    public void setChangingLines(@Nullable List<Integer> positionsBottomToTop) {
        boolean[] mask = changingLineMask(positionsBottomToTop);
        for (int i = 0; i < changingLinePositions.length; i++) {
            changingLinePositions[i] = mask[i];
        }
        invalidate();
    }

    public void clearChangingLines() {
        setChangingLines(null);
    }

    boolean isChangingLineHighlightedForTest(int positionBottomToTop) {
        return positionBottomToTop >= 1
                && positionBottomToTop <= changingLinePositions.length
                && changingLinePositions[positionBottomToTop - 1];
    }

    static boolean[] changingLineMaskForTest(@Nullable List<Integer> positionsBottomToTop) {
        return changingLineMask(positionsBottomToTop);
    }

    private static boolean[] changingLineMask(@Nullable List<Integer> positionsBottomToTop) {
        boolean[] mask = new boolean[6];
        if (positionsBottomToTop != null) {
            for (Integer position : positionsBottomToTop) {
                if (position != null && position >= 1 && position <= 6) {
                    mask[position - 1] = true;
                }
            }
        }
        return mask;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int desiredWidth = dp(widthDp);
        int desiredHeight = dp(8 + (lineHeightDp + lineGapDp) * 6);
        setMeasuredDimension(resolveSize(desiredWidth, widthMeasureSpec), resolveSize(desiredHeight, heightMeasureSpec));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean[] lines = hexagram == null ? null : hexagram.linesBottomToTop;
        if (lines == null && isInEditMode()) {
            lines = EDIT_MODE_LINES;
        }
        if (lines == null) return;
        float targetWidth = Math.min(getWidth(), dp(widthDp));
        float left = (getWidth() - targetWidth) / 2f;
        float lineHeight = dp(lineHeightDp);
        float gap = dp(lineGapDp);
        float y = dp(4);
        float radius = dp(inkStyle ? 3 : 2);
        float brokenGap = Math.max(dp(8), targetWidth / 5f);
        for (int i = 5; i >= 0; i--) {
            paint.setColor(gold || changingLinePositions[i] ? changingLineColor : lineColor);
            paint.setAlpha(inkStyle ? 225 : 255);
            boolean yang = lines[i];
            if (yang) {
                canvas.drawRoundRect(left, y, left + targetWidth, y + lineHeight, radius, radius, paint);
            } else {
                float segmentWidth = (targetWidth - brokenGap) / 2f;
                canvas.drawRoundRect(left, y, left + segmentWidth, y + lineHeight, radius, radius, paint);
                canvas.drawRoundRect(left + segmentWidth + brokenGap, y, left + targetWidth, y + lineHeight, radius, radius, paint);
            }
            y += lineHeight + gap;
        }
        paint.setAlpha(255);
    }

    private int color(int colorRes) {
        return ContextCompat.getColor(getContext(), colorRes);
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
