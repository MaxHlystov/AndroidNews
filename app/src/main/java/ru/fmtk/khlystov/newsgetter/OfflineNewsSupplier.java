package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;

import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.utils.RawFilesReader;

public class OfflineNewsSupplier implements INewsSupplier {

    @NonNull
    private static final String LOG_TAG = "NewsAppOfflineSupplier";

    @NonNull
    private final WeakReference<Context> weakContext;

    public OfflineNewsSupplier(@NonNull Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
    }

    @Nullable
    @Override
    public String get() throws IOException {
        Context context = weakContext.get();
        if (context == null) throw new IOException("Application context is wrong.");
        Log.d(LOG_TAG, "---------- Start to get offline news ------------");
        String offlineText = RawFilesReader.readFromRawFile(
                R.raw.offline_news_example,
                context);
        if (offlineText != null) {
            return offlineText;
        }
        throw new IOException("Error reading assets file.");
    }

}
