package ru.fmtk.khlystov;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import ru.fmtk.khlystov.newsgetter.NewsSection;

public class AppConfig {

    @NonNull
    private static final String PREF_ALTERNATIVE_CONFIG = "ru.fmtk.khlystov.appconfig.alt_config";
    @NonNull
    private static final String PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG =
            PREF_ALTERNATIVE_CONFIG + ".need_fetch_news_from_online_flag";
    @NonNull
    private static final String PREF_NEWS_SECTION =
            PREF_ALTERNATIVE_CONFIG + ".newsSection";

    @NonNull
    private final SharedPreferences sharedPreferences;

    private boolean needFetchNewsFromOnlineFlag = true;

    @NonNull
    private NewsSection newsSection;

    public AppConfig(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_ALTERNATIVE_CONFIG,
                Context.MODE_PRIVATE);
        newsSection = NewsSection.getDefault();
        restore();
    }

    public synchronized void save() {
        sharedPreferences.edit()
                .putBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag)
                .putString(PREF_NEWS_SECTION, newsSection.getID())
                .apply();
    }

    public boolean isNeedFetchNewsFromOnlineFlag() {
        return needFetchNewsFromOnlineFlag;
    }

    public void setNeedFetchNewsFromOnlineFlag(boolean needFetchNewsFromOnlineFlag) {
        this.needFetchNewsFromOnlineFlag = needFetchNewsFromOnlineFlag;
    }

    @NonNull
    public NewsSection getNewsSection() {
        return newsSection;
    }

    public void setNewsSection(@Nullable NewsSection news_section) {
        this.newsSection = news_section != null ? news_section : NewsSection.getDefault();
    }

    private void restore() {
        needFetchNewsFromOnlineFlag = sharedPreferences.getBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag);
        newsSection = NewsSection.getByID(sharedPreferences.getString(PREF_NEWS_SECTION, ""));
    }
}
