package fcu.app.i_ching;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class AssetManifestTest {
    @Test
    public void manifestEntriesReferenceCommittedFilesWithMatchingChecksums() throws Exception {
        File manifest = repoFile("tools/assets/asset_manifest.json");
        JSONArray assets = new JSONArray(Files.readString(manifest.toPath(), StandardCharsets.UTF_8));
        assertTrue("asset manifest should not be empty", assets.length() > 0);

        Set<String> targets = new HashSet<>();
        for (int i = 0; i < assets.length(); i++) {
            JSONObject asset = assets.getJSONObject(i);
            String path = asset.getString("targetPath");
            targets.add(path);
            assertFalse(asset.getString("name").isEmpty());
            assertFalse(asset.getString("sourceUrl").isEmpty());
            assertFalse(asset.getString("license").isEmpty());
            assertFalse(asset.getString("checksumSha256").isEmpty());
            File target = repoFile(path);
            assertTrue(path + " should exist", target.isFile());
            assertEquals(path, asset.getString("checksumSha256"), sha256(target));
        }

        assertTrue(targets.contains("app/src/main/res/font/noto_sans_tc.ttf"));
        assertTrue(targets.contains("app/src/main/res/font/noto_serif_tc.ttf"));
        assertTrue(targets.contains("app/src/main/res/drawable-nodpi/bg_paper_texture_light.webp"));
        assertTrue(targets.contains("app/src/main/res/drawable-nodpi/art_scholar_waterfall.webp"));
    }

    private static File repoFile(String path) {
        File fromRoot = new File(path);
        if (fromRoot.exists()) return fromRoot;
        return new File(".." + File.separator + path);
    }

    private static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = Files.readAllBytes(file.toPath());
        byte[] hashed = digest.digest(bytes);
        StringBuilder builder = new StringBuilder();
        for (byte value : hashed) {
            builder.append(String.format("%02x", value));
        }
        return builder.toString();
    }
}
