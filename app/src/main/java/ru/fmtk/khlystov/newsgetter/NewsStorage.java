package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Single;
import ru.fmtk.khlystov.newsgetter.database.AppDatabase;
import ru.fmtk.khlystov.newsgetter.database.DBStorage;
import ru.fmtk.khlystov.newsgetter.webapi.WebNewsGetter;

public class NewsStorage {

    @NonNull
    public static Completable updateNewsFromNYT(@NonNull Context context,
                                                @NonNull NewsSection newsSection) {
        return WebNewsGetter.updateNewsFromNYT(context, newsSection)
                .map(NewsResponse::getArticles)
                .flatMapCompletable((List<Article> articles) -> {
                    return DBStorage.saveArticles(AppDatabase.getAppDatabase(context), articles);
                });
    }

    @NonNull
    public static Single<List<Article>> getArticles(@NonNull Context context) {
        return DBStorage.getArticles(AppDatabase.getAppDatabase(context));
    }
}
