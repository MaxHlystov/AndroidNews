package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import java.util.List;


@Dao
public abstract class NewsDAO {
    @Query("SELECT * FROM news")
    public abstract List<NewsEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insert(NewsEntity newsEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public abstract void insertAll(List<NewsEntity> newsEntities);

    @Update
    public abstract void update(NewsEntity newsEntity);

    @Delete
    public abstract void delete(NewsEntity newsEntity);

    @Query("DELETE FROM news")
    public abstract int deleteAll();

    @Query("DELETE FROM news WHERE title = :title AND author = :author")
    public abstract int deleteByTitleAndAuthor(String title, String author);

    @Transaction
    public void updateData(List<NewsEntity> newsEntities) {
        deleteAll();
        insertAll(newsEntities);
    }

    @Query("SELECT * FROM news WHERE title = :title AND author = :author")
    public abstract NewsEntity findByTitleAndAuthor(String title, String author);
}
