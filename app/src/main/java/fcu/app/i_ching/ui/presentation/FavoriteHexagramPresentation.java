package fcu.app.i_ching.ui.presentation;

public final class FavoriteHexagramPresentation {
    public final String symbol;
    public final String contentDescription;

    private FavoriteHexagramPresentation(String symbol, String contentDescription) {
        this.symbol = symbol;
        this.contentDescription = contentDescription;
    }

    public static FavoriteHexagramPresentation from(int hexagramNumber, boolean favorite) {
        return new FavoriteHexagramPresentation(
                favorite ? "♥" : "♡",
                (favorite ? "取消收藏" : "加入收藏") + "第" + hexagramNumber + "卦"
        );
    }
}
