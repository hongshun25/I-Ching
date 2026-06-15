package fcu.app.i_ching.ui.presentation;

public final class RitualPresentation {
    public final boolean reduceMotion;
    public final long finishDelayMs;
    public final long pressAnimationMs;
    public final long releaseAnimationMs;
    public final float pressedHeaderAlpha;
    public final float pressedFocusAlpha;
    public final float pressedFocusScale;
    public final boolean cancelOnRelease;

    private RitualPresentation(boolean reduceMotion, long finishDelayMs,
                               long pressAnimationMs, long releaseAnimationMs,
                               float pressedHeaderAlpha, float pressedFocusAlpha,
                               float pressedFocusScale, boolean cancelOnRelease) {
        this.reduceMotion = reduceMotion;
        this.finishDelayMs = finishDelayMs;
        this.pressAnimationMs = pressAnimationMs;
        this.releaseAnimationMs = releaseAnimationMs;
        this.pressedHeaderAlpha = pressedHeaderAlpha;
        this.pressedFocusAlpha = pressedFocusAlpha;
        this.pressedFocusScale = pressedFocusScale;
        this.cancelOnRelease = cancelOnRelease;
    }

    public static RitualPresentation from(boolean reduceMotion) {
        if (reduceMotion) {
            return new RitualPresentation(
                    true,
                    450L,
                    120L,
                    120L,
                    0.85f,
                    0.9f,
                    1.03f,
                    false
            );
        }
        return new RitualPresentation(
                false,
                3000L,
                3000L,
                300L,
                0.45f,
                0.65f,
                1.45f,
                true
        );
    }
}
