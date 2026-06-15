package fcu.app.i_ching.ui.presentation;

import fcu.app.i_ching.data.Hexagram;

public final class HexagramListItemPresentation {
    public final String numberText;
    public final String nameText;
    public final String trigramsText;
    public final String tagsText;
    public final String favoriteSymbol;
    public final String favoriteContentDescription;

    private HexagramListItemPresentation(String numberText, String nameText, String trigramsText,
                                         String tagsText, String favoriteSymbol,
                                         String favoriteContentDescription) {
        this.numberText = numberText;
        this.nameText = nameText;
        this.trigramsText = trigramsText;
        this.tagsText = tagsText;
        this.favoriteSymbol = favoriteSymbol;
        this.favoriteContentDescription = favoriteContentDescription;
    }

    public static HexagramListItemPresentation from(Hexagram hexagram, boolean favorite) {
        FavoriteHexagramPresentation favoritePresentation =
                FavoriteHexagramPresentation.from(hexagram.number, favorite);
        return new HexagramListItemPresentation(
                "第" + hexagram.number + "卦",
                hexagram.name,
                "上" + hexagram.upper + "　下" + hexagram.lower,
                String.join("　", hexagram.tags),
                favoritePresentation.symbol,
                favoritePresentation.contentDescription
        );
    }
}
