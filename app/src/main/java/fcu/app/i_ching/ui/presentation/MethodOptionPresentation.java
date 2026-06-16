package fcu.app.i_ching.ui.presentation;

import fcu.app.i_ching.R;
import fcu.app.i_ching.data.DivinationMethod;

public final class MethodOptionPresentation {
    public final DivinationMethod method;
    public final boolean selected;
    public final int iconRes;
    public final String titleText;
    public final String detailText;
    public final String contentDescription;

    private MethodOptionPresentation(DivinationMethod method, boolean selected, int iconRes, String titleText,
                                     String detailText, String contentDescription) {
        this.method = method;
        this.selected = selected;
        this.iconRes = iconRes;
        this.titleText = titleText;
        this.detailText = detailText;
        this.contentDescription = contentDescription;
    }

    public static MethodOptionPresentation from(DivinationMethod method, DivinationMethod selected) {
        String detail;
        int iconRes;
        switch (method) {
            case SIMPLE:
                detail = "適合快速反思，直接得出一卦，無變爻。";
                iconRes = R.drawable.ic_simple_hexagram_24;
                break;
            case YARROW:
                detail = "節奏較慢，十八變而成卦。適合重大決策前的深度提問與冥想沉澱。";
                iconRes = R.drawable.ic_yarrow_24;
                break;
            case COINS:
            default:
                detail = "模擬傳統銅錢占法，六次投擲，可產生變爻，體現事物變動之機。";
                iconRes = R.drawable.ic_coins_24;
                break;
        }
        boolean isSelected = method == selected;
        return new MethodOptionPresentation(
                method,
                isSelected,
                iconRes,
                method.label,
                detail,
                method.label + (isSelected ? "，已選擇" : "，未選擇")
        );
    }
}
