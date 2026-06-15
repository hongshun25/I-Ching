package fcu.app.i_ching.ui;

import android.content.Intent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28)
public class ResultFragmentTest {
    @Test
    public void shareChooserWrapsTextSendIntent() {
        Intent chooser = ResultFragment.createShareChooserIntent("《易經占卜》\n問題：分享 intent 測試問題");

        assertEquals(Intent.ACTION_CHOOSER, chooser.getAction());
        Intent sendIntent = chooser.getParcelableExtra(Intent.EXTRA_INTENT);
        assertEquals(Intent.ACTION_SEND, sendIntent.getAction());
        assertEquals("text/plain", sendIntent.getType());
        assertEquals("《易經占卜》\n問題：分享 intent 測試問題",
                sendIntent.getStringExtra(Intent.EXTRA_TEXT));
    }
}
