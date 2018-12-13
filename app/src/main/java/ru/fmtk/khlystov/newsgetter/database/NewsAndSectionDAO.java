package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public abstract class NewsAndSectionDAO {

    @Query("SELECT " +
            "   news.*, " +
            "   news_section.web_id AS web_id " +
            "FROM news " +
            "   INNER JOIN news_section " +
            "       ON news.section_id = news_section.id")
    public abstract Single<List<NewsAndSectionEntity>> findAll();

    @Query("SELECT " +
            "   news.*, " +
            "   news_section.web_id AS web_id " +
            "FROM news " +
            "   INNER JOIN news_section " +
            "       ON news.section_id = news_section.id " +
            "WHERE news.title = :title AND news.author = :author " +
            "LIMIT 1")
    public abstract Single<NewsAndSectionEntity> getArticleByTitleAndAuthor(String title, String author);
}
