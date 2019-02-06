package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.fmtk.khlystov.newsgetter.database.AppDatabase;
import ru.fmtk.khlystov.newsgetter.database.DBGetway;
import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.newsgetter.model.ArticleIdentificator;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;
import ru.fmtk.khlystov.newsgetter.webapi.WebNewsGetter;

public class NewsGetway {

    @NonNull
    public static Completable retrieveOnlineNews(@NonNull Context context,
                                                 @NonNull NewsSection newsSection) {
        return retrieveOnlineNewsWithDelay(context, newsSection, 0, TimeUnit.MILLISECONDS);
    }

    @NonNull
    public static Completable retrieveOnlineNewsWithDelay(@NonNull Context context,
                                                          @NonNull NewsSection newsSection,
                                                          long delay,
                                                          @NonNull TimeUnit timeUnit) {
        return WebNewsGetter.updateNewsFromNYT(newsSection)
                .delay(delay, timeUnit)
                .flatMapCompletable((List<Article> articles) -> {
                    return DBGetway.saveArticles(AppDatabase.getAppDatabase(context), articles);
                });
    }

    @NonNull
    public static Single<List<Article>> getArticles(@NonNull Context context) {
        return DBGetway.getArticles(AppDatabase.getAppDatabase(context));
    }

    @NonNull
    public static Single<Article> getArticleById(@NonNull Context context,
                                                 @NonNull ArticleIdentificator articleIdentificator) {
        return DBGetway.getArticleById(AppDatabase.getAppDatabase(context), articleIdentificator);
    }

    @NonNull
    public static Completable updateArticle(@NonNull Context context,
                                            @NonNull ArticleIdentificator articleIdentificator,
                                            @Nullable Article article) {
        return DBGetway.updateArticle(AppDatabase.getAppDatabase(context),
                articleIdentificator,
                article);
    }

    @NonNull
    public static Completable deleteArticleById(@NonNull Context context,
                                                @NonNull ArticleIdentificator articleIdentificator) {
        return DBGetway.deleteArticleById(AppDatabase.getAppDatabase(context),
                articleIdentificator);
    }
}
