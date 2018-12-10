package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;

import java.util.List;

import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface NewsAndSectionDAO {

    @Transaction
    @Query("SELECT news.*, news_section.web_id AS web_id FROM news INNER JOIN news_section ON news.section_id = news_section.id")
    Single<List<NewsAndSectionEntity>> findAll();
}
