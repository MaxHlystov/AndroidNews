package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.fmtk.khlystov.newsgetter.database.AppDatabase;
import ru.fmtk.khlystov.newsgetter.database.DBGetway;
import ru.fmtk.khlystov.newsgetter.webapi.WebNewsGetter;

public class NewsGetway {

    @NonNull
    public static Completable updateNewsFromNYT(@NonNull Context context,
                                                @NonNull NewsSection newsSection) {
        return WebNewsGetter.updateNewsFromNYT(context, newsSection)
                .map(NewsResponse::getArticles)
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
