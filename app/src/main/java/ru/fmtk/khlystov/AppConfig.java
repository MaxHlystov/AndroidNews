package ru.fmtk.khlystov;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.ref.WeakReference;
import java.util.Locale;

public class AppConfig {

    @NonNull
    public static final String defaultNewsSection = "home";

    @NonNull
    private static final String PREF_ALTERNATIVE_CONFIG = "ru.fmtk.khlystov.appconfig.alt_config";
    @NonNull
    private static final String PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG =
            PREF_ALTERNATIVE_CONFIG + ".need_fetch_news_from_online_flag";
    @NonNull
    private static final String PREF_NEWS_SECTION =
            PREF_ALTERNATIVE_CONFIG + ".news_section";

    @NonNull
    private final WeakReference<SharedPreferences> sharedPreferencesWeakReference;

    private boolean needFetchNewsFromOnlineFlag = true;

    @Nullable
    private String news_section;

    public AppConfig(@NonNull Context context) {
        sharedPreferencesWeakReference = new WeakReference<>(
                context.getSharedPreferences(PREF_ALTERNATIVE_CONFIG, Context.MODE_PRIVATE));
        news_section = defaultNewsSection;
        restore();
    }

    public synchronized void save() {
        SharedPreferences shp = sharedPreferencesWeakReference.get();
        if (shp != null) {
            shp.edit()
                    .putBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag)
                    .putString(PREF_NEWS_SECTION, news_section)
                    .apply();
        }
    }

    public boolean isNeedFetchNewsFromOnlineFlag() {
        return needFetchNewsFromOnlineFlag;
    }

    public void setNeedFetchNewsFromOnlineFlag(boolean needFetchNewsFromOnlineFlag) {
        this.needFetchNewsFromOnlineFlag = needFetchNewsFromOnlineFlag;
    }

    @NonNull
    public String getNews_section() {
        if (news_section != null) return news_section;
        return defaultNewsSection;
    }

    public void setNews_section(@Nullable String news_section) {
        this.news_section = news_section != null ? news_section : defaultNewsSection;
    }

    private void restore() {
        SharedPreferences shp = sharedPreferencesWeakReference.get();
        if (shp != null) {
            needFetchNewsFromOnlineFlag = shp.getBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag);
            news_section = shp.getString(PREF_NEWS_SECTION, defaultNewsSection);
        }
    }
}
