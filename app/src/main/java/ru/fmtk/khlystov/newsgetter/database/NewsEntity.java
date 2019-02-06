package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.Nullable;

import java.util.Date;


@Entity(tableName = "news",
        indices = {@Index(value = "section_id")},
        foreignKeys = {@ForeignKey(
                entity = SectionEntity.class,
                parentColumns = "id",
                childColumns = "section_id",
                onDelete = ForeignKey.CASCADE,
                onUpdate = ForeignKey.CASCADE)})
public class NewsEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "section_id")
    public int sectionId;

    @Nullable
    @ColumnInfo(name = "title")
    public String title;

    @Nullable
    @ColumnInfo(name = "published_at")
    @TypeConverters({DateConverter.class})
    public Date publishedAt;

    @Nullable
    @ColumnInfo(name = "subsection")
    public String subsection;

    @Nullable
    @ColumnInfo(name = "author")
    public String author;

    @Nullable
    @ColumnInfo(name = "description")
    public String description;

    @Nullable
    @ColumnInfo(name = "content")
    public String content;

    @Nullable
    @ColumnInfo(name = "url")
    public String url;

    @Nullable
    @ColumnInfo(name = "url_to_image")
    public String urlToImage;
}
