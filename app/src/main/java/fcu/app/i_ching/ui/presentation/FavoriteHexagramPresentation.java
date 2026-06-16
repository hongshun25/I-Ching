package fcu.app.i_ching.ui.presentation;

import fcu.app.i_ching.R;

public final class FavoriteHexagramPresentation {
    public final boolean favorite;
    public final int iconRes;
    public final String contentDescription;

    private FavoriteHexagramPresentation(boolean favorite, int iconRes, String contentDescription) {
        this.favorite = favorite;
        this.iconRes = iconRes;
        this.contentDescription = contentDescription;
    }

    public static FavoriteHexagramPresentation from(int hexagramNumber, boolean favorite) {
        return new FavoriteHexagramPresentation(
                favorite,
                favorite ? R.drawable.ic_favorite_24 : R.drawable.ic_favorite_border_24,
                (favorite ? "取消收藏" : "加入收藏") + "第" + hexagramNumber + "卦"
        );
    }
}
