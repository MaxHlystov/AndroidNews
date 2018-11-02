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
import ru.fmtk.khlystov.newsgetter.webapi.DTONewsResponse;
import ru.fmtk.khlystov.newsgetter.webapi.NYTNetworkAPI;
import ru.fmtk.khlystov.utils.AssetsReader;

import java.io.IOException;


public class NewsGetter {

    private static final boolean throwError = false;

    @Nullable
    private static String section;

    private static boolean online;

    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @Nullable String section,
                                                       boolean online) {
        if (newsObserver != null
                && NewsGetter.section != null
                && NewsGetter.section.equals(section)
                && NewsGetter.online == online) {
            return newsObserver;
        }
        NewsGetter.section = section == null ? AppConfig.defaultNewsSection : section;
        NewsGetter.online = online;
        Gson gson = new Gson();

        Single<DTONewsResponse> dTOnewsObserver;
        if (online) {
            dTOnewsObserver = NYTNetworkAPI.createOnlineRequest(section);
        } else {
            dTOnewsObserver = Single.create((SingleEmitter<String> singleEmitter) -> {
                getOfflineNews(singleEmitter, context);
            })
                    .map((String it) -> gson.fromJson(it, DTONewsResponse.class));
        }
        newsObserver = dTOnewsObserver
                .doOnSuccess(it -> {
                    if (throwError) throw new IOException("Test exception!");
                })
                .subscribeOn(Schedulers.io())
                .map(NewsConverter::convertToNewsResponse)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread());
        return newsObserver;
    }

    private static void getOfflineNews(@NonNull SingleEmitter<String> emitter, @NonNull Context context) {
        String offlineText = AssetsReader.ReadFromAssetFile(
                "offline_news_example.json",
                context);
        if (offlineText != null) {
            emitter.onSuccess(offlineText);
        } else {
            emitter.onError(new IOException("Error reading assets file."));
        }
    }

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }
}
