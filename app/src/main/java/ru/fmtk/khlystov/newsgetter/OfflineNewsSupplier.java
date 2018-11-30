package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;
import java.lang.ref.WeakReference;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.newsgetter.webapi.DTONewsResponse;
import ru.fmtk.khlystov.utils.RawFilesReader;

public class OfflineNewsSupplier {

    @NonNull
    private final WeakReference<Context> weakContext;

    @NonNull
    private final Gson gson;

    public OfflineNewsSupplier(@NonNull Context context) {
        weakContext = new WeakReference<>(context.getApplicationContext());
        gson = new Gson();
    }

    @NonNull
    public Single<DTONewsResponse> getOfflineNewsObserver() {
        return Single.create((SingleEmitter<String> singleEmitter) -> {
            Context context = weakContext.get();
            if (context == null) throw new IOException("Application context is wrong.");
            String offlineText = RawFilesReader.readFromRawFile(
                    R.raw.offline_news_example,
                    context);
            if (offlineText != null) {
                singleEmitter.onSuccess(offlineText);
            } else {
                singleEmitter.onError(new IOException("Error reading assets file."));
            }
        })
                .map((String it) -> gson.fromJson(it, DTONewsResponse.class));
    }
}
