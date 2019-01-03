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
    private static final String PREF_NEED_TO_SHOW_INTRO_ACTIVITY_FLAG =
            PREF_ALTERNATIVE_CONFIG + ".need_to_show_intro_activity_flag";
    @NonNull
    private static final String PREF_NEWS_SECTION =
            PREF_ALTERNATIVE_CONFIG + ".newsSection";

    @Nullable
    private static AppConfig appConfig;

    @NonNull
    private final SharedPreferences sharedPreferences;

    @NonNull
    private NewsSection newsSection;

    private boolean needToShowIntroActivityFlag = true;

    private AppConfig(@NonNull Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_ALTERNATIVE_CONFIG,
                Context.MODE_PRIVATE);
        newsSection = NewsSection.getDefault();
        restore();
    }

    @NonNull
    public static synchronized AppConfig getAppConfig(@NonNull Context context) {
        if (appConfig == null) {
            appConfig = new AppConfig(context);
        }
        return appConfig;
    }

    @NonNull
    public synchronized NewsSection getNewsSection() {
        return newsSection;
    }

    public synchronized void setNewsSection(@Nullable NewsSection news_section) {
        this.newsSection = news_section != null ? news_section : NewsSection.getDefault();
    }

    public synchronized boolean isNeedToShowIntroActivityFlag() {
        return needToShowIntroActivityFlag;
    }

    public synchronized void setNeedToShowIntroActivityFlag(boolean needToShowIntroActivity) {
        this.needToShowIntroActivityFlag = needToShowIntroActivity;
    }

    public synchronized void save() {
        sharedPreferences.edit()
                .putString(PREF_NEWS_SECTION, newsSection.getID())
                .putBoolean(PREF_NEED_TO_SHOW_INTRO_ACTIVITY_FLAG, needToShowIntroActivityFlag)
                .apply();
    }

    private synchronized void restore() {
        newsSection = NewsSection.getByID(sharedPreferences.getString(PREF_NEWS_SECTION, ""));
        needToShowIntroActivityFlag = sharedPreferences.getBoolean(PREF_NEED_TO_SHOW_INTRO_ACTIVITY_FLAG, needToShowIntroActivityFlag);
    }
}
