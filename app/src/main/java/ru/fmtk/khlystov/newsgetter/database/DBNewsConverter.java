package ru.fmtk.khlystov.newsgetter.database;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.fmtk.khlystov.newsgetter.model.Article;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;

public class DBNewsConverter {

    @NonNull
    public static NewsAndSectionEntity articleToNewsAndSectionEntity(@NonNull Article article) {
        return new NewsAndSectionEntity(articleToNewsEntity(article),
                article.getSection().getID());
    }

    @NonNull
    public static NewsEntity articleToNewsEntity(@NonNull Article article) {
        NewsEntity newsEntity = new NewsEntity();
        newsEntity.title = article.getTitle();
        newsEntity.publishedAt = article.getPublishedAt();
        newsEntity.author = article.getAuthor();
        newsEntity.subsection = article.getSubsection();
        newsEntity.description = article.getDescription();
        newsEntity.content = article.getContent();
        newsEntity.url = article.getUrl();
        newsEntity.urlToImage = article.getUrlToImage();
        return newsEntity;
    }

    @NonNull
    public static Article articleFromDB(@NonNull NewsAndSectionEntity newsAndSectionEntity) {
        NewsEntity newsEntity = newsAndSectionEntity.getNewsEntity();
        NewsSection newsSection = NewsSection.getByID(newsAndSectionEntity.getWebId());
        return new Article.Builder(
                newsSection,
                newsEntity.title,
                newsEntity.publishedAt)
                .setAuthor(newsEntity.author)
                .setSubsection(newsEntity.subsection)
                .setDescription(newsEntity.description)
                .setContent(newsEntity.content)
                .setUrl(newsEntity.url)
                .setUrlToImage(newsEntity.urlToImage)
                .build();
    }

    @NonNull
    public static List<NewsAndSectionEntity> articlesToDB(
            @NonNull List<Article> articles,
            @NonNull Converter<NewsSection, SectionEntity> sectionToDB) {
        return convertList(
                articles,
                (Article article) -> {
                    NewsAndSectionEntity nse = articleToNewsAndSectionEntity(article);
                    nse.setSection(sectionToDB.convert(article.getSection()));
                    return nse;
                });
    }

    @NonNull
    public static List<Article> articlesFromDB(@NonNull List<NewsAndSectionEntity> newsAndSectionEntities) {
        return convertList(newsAndSectionEntities, DBNewsConverter::articleFromDB);
    }

    @NonNull
    public static SectionEntity newsSectionToDB(@NonNull NewsSection newsSection) {
        return new SectionEntity(newsSection.getID());
    }

    @NonNull
    public static <T, R> List<R> convertList(@NonNull List<T> inList,
                                             @NonNull Converter<T, R> converter) {
        List<R> resultList = new ArrayList<>(inList.size());
        for (T item : inList) {
            resultList.add(converter.convert(item));
        }
        return resultList;
    }

    @NonNull
    public static <T, K> Map<K, T> convertToMap(@NonNull List<T> inList,
                                                @NonNull Converter<T, K> converter) {

        Map<K, T> resultMap = new HashMap<>();
        for (T item : inList) {
            resultMap.put(converter.convert(item), item);
        }
        return resultMap;
    }

    public interface Converter<T, R> {
        R convert(T t);
    }
}
