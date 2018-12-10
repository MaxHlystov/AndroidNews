package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;


@Dao
public interface SectionDAO {
    @Query("SELECT * FROM news_section")
    List<SectionEntity> getAll();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(SectionEntity newsSectionEntity);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long[] insertAll(List<SectionEntity> newsSections);

    @Delete
    void delete(SectionEntity sectionEntity);

    @Query("DELETE FROM news_section")
    int deleteAll();

    @Query("SELECT * FROM news_section WHERE web_id = :web_id LIMIT 1")
    List<SectionEntity> findByWebId(String web_id);

    @Query("SELECT * FROM news_section WHERE web_id in(:webIds)")
    List<SectionEntity> findAllByWebIds(List<String> webIds);

    @Query("SELECT * FROM news_section WHERE id in(:id)")
    List<SectionEntity> findAllByIds(List<Integer> id);
}
