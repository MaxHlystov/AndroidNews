package ru.fmtk.khlystov.newsgetter.database;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsSection;

public class DBStorage {

    @NonNull
    public static Single<List<Article>> getArticles(@NonNull AppDatabase appDatabase) {
        return appDatabase.newsAndSectionDAO().findAll()
                .map(DBNewsConverter::articlesFromDB)
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    public static Completable saveArticles(@NonNull AppDatabase appDatabase,
                                           @NonNull List<Article> articles) {

        return Completable.fromCallable(() -> {
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
            return true;
        })
                .subscribeOn(Schedulers.io());
    }

    @NonNull
    private static List<NewsSection> getUsedSections(List<Article> articles) {
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