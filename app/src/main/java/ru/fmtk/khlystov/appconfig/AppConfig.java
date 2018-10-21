package ru.fmtk.khlystov.appconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public class AppConfig {

    @NonNull
    private static final String PREF_ALTERNATIVE_CONFIG = "ru.fmtk.khlystov.appconfig.alt_config";
    @NonNull
    private static final String PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG =
            "ru.fmtk.khlystov.appconfig.need_fetch_news_from_online_flag";

    @NonNull
    private WeakReference<SharedPreferences> sharedPreferencesWeakReference;

    private boolean needFetchNewsFromOnlineFlag = true;

    public AppConfig(@NonNull Context context) {
        sharedPreferencesWeakReference = new WeakReference<>(
                context.getSharedPreferences(PREF_ALTERNATIVE_CONFIG, Context.MODE_PRIVATE));
        restore();
    }

    public void save() {
        SharedPreferences shp = sharedPreferencesWeakReference.get();
        if (shp != null) {
            shp.edit()
                    .putBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag)
                    .apply();
        }
    }

    public boolean isNeedFetchNewsFromOnlineFlag() {
        return needFetchNewsFromOnlineFlag;
    }

    public void setNeedFetchNewsFromOnlineFlag(boolean needFetchNewsFromOnlineFlag) {
        this.needFetchNewsFromOnlineFlag = needFetchNewsFromOnlineFlag;
    }

    private void restore() {
        SharedPreferences shp = sharedPreferencesWeakReference.get();
        if (shp != null) {
            needFetchNewsFromOnlineFlag = shp.getBoolean(PREF_NEED_FETCH_NEWS_FROM_ONLINE_FLAG, needFetchNewsFromOnlineFlag);
        }
    }
}
