package fcu.app.i_ching.ui;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;

public class ProfileSettingsFragmentTest {
    @Test
    public void writeExportContentUsesUtf8AndAllowsEmptyContent() throws Exception {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        ProfileSettingsFragment.writeExportContent(stream, "易經占卜紀錄\n問題：測試\n");

        assertEquals("易經占卜紀錄\n問題：測試\n", stream.toString(StandardCharsets.UTF_8.name()));

        ByteArrayOutputStream empty = new ByteArrayOutputStream();
        ProfileSettingsFragment.writeExportContent(empty, null);

        assertEquals("", empty.toString(StandardCharsets.UTF_8.name()));
    }
}
