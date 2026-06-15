package fcu.app.i_ching.ui.presentation;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class FavoriteHexagramPresentationTest {
    @Test
    public void favoriteStateControlsSymbolAndTalkBackLabel() {
        FavoriteHexagramPresentation favorite = FavoriteHexagramPresentation.from(15, true);
        FavoriteHexagramPresentation normal = FavoriteHexagramPresentation.from(15, false);

        assertEquals("♥", favorite.symbol);
        assertEquals("取消收藏第15卦", favorite.contentDescription);
        assertEquals("♡", normal.symbol);
        assertEquals("加入收藏第15卦", normal.contentDescription);
    }
}
