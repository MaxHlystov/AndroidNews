package ru.fmtk.khlystov.androidnews;

import android.support.annotation.NonNull;

public enum SocialNetwork {

    TELEGRAM("https://t.me/max1c"),
    LINKEDIN("https://linkedin.com/pub/maxim-khlystov/a7/6ba/123"),
    GITHUB("https://github.com/MaxHlystov"),
    STEPIK("https://stepik.org/leaders/knowledge");

    @NonNull
    private final String url;

    SocialNetwork(@NonNull String url) {
        this.url = url;
    }

    @NonNull
    public String getUrl() {
        return url;
    }
}
