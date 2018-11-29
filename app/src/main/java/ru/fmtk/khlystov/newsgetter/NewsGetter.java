package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.androidnews.BuildConfig;
import ru.fmtk.khlystov.newsgetter.webapi.DTONewsResponse;
import ru.fmtk.khlystov.newsgetter.webapi.NYTNetworkAPI;

import java.io.IOException;
import java.util.Objects;


public class NewsGetter {

    private static final boolean THROW_ERROR = false;

    @Nullable
    private static NewsSection section;

    private static boolean online;

    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @NonNull NewsSection section,
                                                       boolean online) {
        if (newsObserver == null
                || NewsGetter.section == null
                || !Objects.equals(NewsGetter.section, section)
                || NewsGetter.online != online) {
            setNewsObserver(context, section, online);
        }
        return newsObserver;
    }

    private static void setNewsObserver(@NonNull Context context,
                                        @NonNull NewsSection section,
                                        boolean online) {
        NewsGetter.section = section;
        NewsGetter.online = online;
        Single<DTONewsResponse> dTONewsObserver;
        if (online) {
            dTONewsObserver = NYTNetworkAPI.createOnlineRequest(section.getID());
        } else {
            dTONewsObserver = new OfflineNewsSupplier(context).getOfflineNewsObserver();
        }
        if (BuildConfig.DEBUG && THROW_ERROR) {
            dTONewsObserver = dTONewsObserver.doOnSuccess(
                    it -> {
                        throw new IOException("Test exception!");
                    });
        }
        newsObserver = dTONewsObserver
                .subscribeOn(Schedulers.io())
                .map(NewsConverter::convertToNewsResponse)
                .subscribeOn(Schedulers.computation())
                .cache()
                .observeOn(AndroidSchedulers.mainThread());
    }

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }
}
