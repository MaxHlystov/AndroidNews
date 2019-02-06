package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.androidnews.BuildConfig;
import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;


public class WebNewsGetter {

    private static final boolean THROW_ERROR = false;

    @Nullable
    private static NewsSection section;

    @Nullable
    private static Single<List<Article>> newsObserver = null;

    @NonNull
    public static Single<List<Article>> updateNewsFromNYT(@NonNull NewsSection section) {
        if (newsObserver == null
                || WebNewsGetter.section == null
                || !Objects.equals(WebNewsGetter.section, section)) {
            setNewsObserver(section);
        }
        return newsObserver;
    }

    private static void setNewsObserver(@NonNull NewsSection section) {
        WebNewsGetter.section = section;
        Single<DTONewsResponse> dTONewsObserver =
                NYTNetworkAPI.createOnlineRequest(section.getID());
        if (BuildConfig.DEBUG && THROW_ERROR) {
            dTONewsObserver = dTONewsObserver.doOnSuccess(
                    it -> {
                        throw new IOException("Test exception!");
                    });
        }
        newsObserver = dTONewsObserver
                .subscribeOn(Schedulers.io())
                .map(DTONewsConverter::convertToNewsResponse)
                .subscribeOn(Schedulers.computation());
                //.observeOn(AndroidSchedulers.mainThread());
    }

    private WebNewsGetter() {
        throw new IllegalAccessError("WebNewsGetter's constructor invocation.");
    }
}
