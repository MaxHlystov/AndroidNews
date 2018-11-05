package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

import java.util.concurrent.TimeUnit;

public class NewsGetter {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsGetter";

    private static final long IDLE_TIME_SECONDS = 4;

    @NonNull
    private static final String COUNTRY_CODE_BY_DEFAULT = "us";


    @Nullable
    private static Gson gson = null;

    @Nullable
    private static String countryCode;

    private static boolean online;

    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @Nullable String countryCode,
                                                       boolean online) {
        Log.d(LOG_TAG, "On getNewsObserver " + Thread.currentThread() + "; news observer: " + newsObserver);
        if (newsObserver == null
                || NewsGetter.countryCode == null
                || !NewsGetter.countryCode.equals(countryCode)
                || NewsGetter.online != online) {
            setNewsObserver(context, countryCode, online);
        } else {
            Log.d(LOG_TAG, "Takes existing observer");
        }
        Log.d(LOG_TAG, "We use observer: " + newsObserver);
        return newsObserver;
    }

    private static void setNewsObserver(@NonNull Context context,
                                        @Nullable String countryCode,
                                        boolean online) {
        Log.d(LOG_TAG, "Init call");
        NewsGetter.countryCode = countryCode == null ? COUNTRY_CODE_BY_DEFAULT : countryCode;
        NewsGetter.online = online;
        if (gson == null) gson = new Gson();
        INewsSupplier newsSupplier;
        if (online) {
            newsSupplier = new OnlineNewsSupplier(NewsGetter.countryCode);
        } else {
            newsSupplier = new OfflineNewsSupplier(context);
        }
        newsObserver = Single.fromCallable(newsSupplier::get)
                .doOnSuccess(it -> {
                    Log.d(LOG_TAG, "An operation is complete");
                })
                // In accordance to the item 5 step 5 hw 4
                .delay(IDLE_TIME_SECONDS, TimeUnit.SECONDS)
                .map((String it) -> gson.fromJson(it, NewsResponse.class))
                .subscribeOn(Schedulers.io())
                .cache()
                .observeOn(AndroidSchedulers.mainThread());
    }
}
