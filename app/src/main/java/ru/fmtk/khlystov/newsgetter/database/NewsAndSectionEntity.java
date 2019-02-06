package ru.fmtk.khlystov.newsgetter.database;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Embedded;
import android.support.annotation.NonNull;

public class NewsAndSectionEntity {

    @Embedded
    @NonNull
    public final NewsEntity newsEntity;

    @ColumnInfo(name = "web_id")
    @NonNull
    public String webId;

    public NewsAndSectionEntity(@NonNull NewsEntity newsEntity, @NonNull String webId) {
        this.newsEntity = newsEntity;
        this.webId = webId;
    }

    @NonNull
    public NewsEntity getNewsEntity() {
        return newsEntity;
    }

    @NonNull
    public String getWebId() {
        return webId;
    }

    public void setSection(@NonNull SectionEntity sectionEntity) {
        this.webId = sectionEntity.getWebId();
        newsEntity.sectionId = sectionEntity.getId();
    }
}
