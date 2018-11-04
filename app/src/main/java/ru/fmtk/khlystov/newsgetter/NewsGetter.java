package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.newsgetter.webapi.DTONewsResponse;
import ru.fmtk.khlystov.newsgetter.webapi.NYTNetworkAPI;
import ru.fmtk.khlystov.utils.AssetsReader;

import java.io.IOException;


public class NewsGetter {

    private static final boolean THROW_ERROR = false;

    @Nullable
    private static String section;

    private static boolean online;

    @Nullable
    private static Gson gson = null;

    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @Nullable String section,
                                                       boolean online) {
        if (newsObserver == null
                || NewsGetter.section == null
                || !NewsGetter.section.equals(section)
                || NewsGetter.online != online) {
            setNewsObserver(context, section, online);
        }
        return newsObserver;
    }

    @Nullable
    public static void setNewsObserver(@NonNull Context context,
                                       @Nullable String section,
                                       boolean online) {
        NewsGetter.section = section == null ? AppConfig.DEFAULT_NEWS_SECTION : section;
        NewsGetter.online = online;
        if (!online && gson == null) gson = new Gson();
        Single<DTONewsResponse> dTONewsObserver;
        if (online) {
            dTONewsObserver = NYTNetworkAPI.createOnlineRequest(section);
        } else {
            dTONewsObserver = getOfflineNewsObserver(context);
        }
        newsObserver = dTONewsObserver
                .doOnSuccess(it -> {
                    if (THROW_ERROR) throw new IOException("Test exception!");
                })
                .subscribeOn(Schedulers.io())
                .map(NewsConverter::convertToNewsResponse)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Single<DTONewsResponse> getOfflineNewsObserver(@NonNull Context context) {
        return Single.create((SingleEmitter<String> singleEmitter) -> {
            String offlineText = AssetsReader.readFromAssetFile(
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

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }
}
