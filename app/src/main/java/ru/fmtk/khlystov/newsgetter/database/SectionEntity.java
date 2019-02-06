package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "news_section",
        indices = {@Index(value = "web_id", unique = true)})
public class SectionEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public int id;

    @ColumnInfo(name = "web_id")
    @NonNull
    public final String webId;

    public SectionEntity(@NonNull String webId) {
        this.webId = webId;
    }

    public int getId() {
        return id;
    }

    @NonNull
    public String getWebId() {
        return webId;
    }
}
