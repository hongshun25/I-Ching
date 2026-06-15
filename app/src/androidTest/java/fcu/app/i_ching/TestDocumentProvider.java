package fcu.app.i_ching;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class TestDocumentProvider extends ContentProvider {
    public static final String AUTHORITY = "fcu.app.i_ching.test.documents";

    public static Uri documentUri(String name) {
        return new Uri.Builder()
                .scheme("content")
                .authority(AUTHORITY)
                .appendPath(name)
                .build();
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        String name = fileName(uri);
        return name.endsWith(".json") ? "application/json" : "text/plain";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        File file = target(uri);
        return file.exists() && file.delete() ? 1 : 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        return 0;
    }

    @NonNull
    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        File file = target(uri);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new FileNotFoundException("Cannot create test document directory");
        }
        if (mode.contains("w")) {
            return ParcelFileDescriptor.open(file,
                    ParcelFileDescriptor.MODE_CREATE
                            | ParcelFileDescriptor.MODE_TRUNCATE
                            | ParcelFileDescriptor.MODE_WRITE_ONLY);
        }
        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    private File target(Uri uri) {
        if (getContext() == null) throw new IllegalStateException("Provider context is unavailable");
        return new File(new File(getContext().getCacheDir(), "test-documents"), fileName(uri));
    }

    private static String fileName(Uri uri) {
        String value = uri.getLastPathSegment();
        if (value == null || value.trim().isEmpty()) return "export.txt";
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }
}
