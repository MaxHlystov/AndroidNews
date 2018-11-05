package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.lang.ref.WeakReference;

import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.utils.AssetsReader;

public class OfflineNewsSupplier implements INewsSupplier {

    @NonNull
    private WeakReference<Context> weakContext;

    public OfflineNewsSupplier(@NonNull Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
    }

    @Nullable
    @Override
    public String get() throws IOException {
        Context context = weakContext.get();
        if (context == null) throw new IOException("Application context is wrong.");
        String offlineText = AssetsReader.readFromAssetFile(
                R.raw.offline_news_example,
                context);
        if (offlineText != null) {
            return offlineText;
        }
        throw new IOException("Error reading assets file.");
    }

}
