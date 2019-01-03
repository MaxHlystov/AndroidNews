package ru.fmtk.khlystov.newsgetter.database;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.ArticleIdentificator;
import ru.fmtk.khlystov.newsgetter.NewsSection;

public class DBGetway {

    @NonNull
    public static Single<List<Article>> getArticles(@NonNull AppDatabase appDatabase) {
        return appDatabase.newsAndSectionDAO().findAll()
                .map(DBNewsConverter::articlesFromDB)
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Single<Article> getArticleById(@NonNull AppDatabase appDatabase,
                                                 @NonNull ArticleIdentificator articleIdentificator) {
        return appDatabase.newsAndSectionDAO().getArticleByTitleAndAuthor(
                articleIdentificator.getTitle(),
                articleIdentificator.getAuthor())
                .map(DBNewsConverter::articleFromDB)
                .subscribeOn(Schedulers.io());
    }

    public static Completable updateArticle(@NonNull AppDatabase appDatabase,
                                                @NonNull ArticleIdentificator articleIdentificator,
                                                @Nullable Article article) {
        if(article == null) {
            return deleteArticleById(appDatabase, articleIdentificator);
        }
        return Completable.create((CompletableEmitter emitter) -> {
            NewsEntity newsEntityToSave = DBNewsConverter.articleToNewsEntity(article);
            SectionEntity sectionEntity = appDatabase.newsSectionDAO().findByWebId(
                    article.getSection().getID());
            NewsEntity newsEntityFromDB = appDatabase.newsDAO().findByTitleAndAuthor(
                    articleIdentificator.getTitle(),
                    articleIdentificator.getAuthor());
            newsEntityToSave.id = newsEntityFromDB.id;
            newsEntityToSave.sectionId = sectionEntity.id;
            appDatabase.newsDAO().update(newsEntityToSave);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Completable deleteArticleById(@NonNull AppDatabase appDatabase,
                                                @NonNull ArticleIdentificator articleIdentificator) {
        return Completable.create((CompletableEmitter emitter) -> {
            appDatabase.newsDAO().deleteByTitleAndAuthor(
                    articleIdentificator.getTitle(),
                    articleIdentificator.getAuthor());
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Completable saveArticles(@NonNull AppDatabase appDatabase,
                                           @NonNull List<Article> articles) {
        return Completable.create((CompletableEmitter emitter) -> {
            List<SectionEntity> usedSections = DBNewsConverter.convertList(
                    getUsedSections(articles), DBNewsConverter::newsSectionToDB);
            List<String> newsSectionsWebIds = DBNewsConverter.convertList(
                    usedSections, SectionEntity::getWebId);
            SectionDAO newsSectionDAO = appDatabase.newsSectionDAO();
            newsSectionDAO.insertAll(usedSections);
            List<SectionEntity> sectionsEntities = newsSectionDAO.findAllByWebIds(newsSectionsWebIds);
            Map<String, SectionEntity> sectionsCache = DBNewsConverter.convertToMap(
                    sectionsEntities, SectionEntity::getWebId);
            List<NewsAndSectionEntity> newsAndSectionEntities = DBNewsConverter.articlesToDB(
                    articles,
                    (NewsSection section) -> sectionsCache.get(section.getID()));
            List<NewsEntity> newsEntities = DBNewsConverter.convertList(
                    newsAndSectionEntities, NewsAndSectionEntity::getNewsEntity);
            appDatabase.newsDAO().updateData(newsEntities);
            emitter.onComplete();
        })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private static List<NewsSection> getUsedSections(@NonNull List<Article> articles) {
        EnumSet<NewsSection> usedSections = EnumSet.noneOf(NewsSection.class);
        for (Article article : articles) {
            usedSections.add(article.getSection());
        }
        List<NewsSection> sections = new ArrayList<>(usedSections.size());
        int idx = 0;
        for (NewsSection newsSection : usedSections) {
            sections.add(idx, newsSection);
        }
        return sections;
    }
}