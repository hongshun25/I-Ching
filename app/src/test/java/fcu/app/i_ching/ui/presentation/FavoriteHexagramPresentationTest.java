package fcu.app.i_ching.ui.presentation;

import org.junit.Test;

import fcu.app.i_ching.R;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FavoriteHexagramPresentationTest {
    @Test
    public void favoriteStateControlsIconAndTalkBackLabel() {
        FavoriteHexagramPresentation favorite = FavoriteHexagramPresentation.from(15, true);
        FavoriteHexagramPresentation normal = FavoriteHexagramPresentation.from(15, false);

        assertTrue(favorite.favorite);
        assertEquals(R.drawable.ic_favorite_24, favorite.iconRes);
        assertEquals("取消收藏第15卦", favorite.contentDescription);
        assertFalse(normal.favorite);
        assertEquals(R.drawable.ic_favorite_border_24, normal.iconRes);
        assertEquals("加入收藏第15卦", normal.contentDescription);
    }
}
