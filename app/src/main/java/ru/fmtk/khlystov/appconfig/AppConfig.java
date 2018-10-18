package ru.fmtk.khlystov.appconfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class AppConfig {

    @NonNull
    private static final String ALTERNATIVE_CONFIG = "NewsListActivity_AltConfig";
    @NonNull
    private static final String CONFIG_GET_ONLINE_NEWS = "NewsListActivity_GetOnlineNews";

    private boolean getOnlineNews = true;

    public AppConfig(Context context) {
        restore(context);
    }

    public void save(@NonNull Context context) {
        SharedPreferences.Editor shp = context.getSharedPreferences(ALTERNATIVE_CONFIG,
                Context.MODE_PRIVATE).edit();
        shp.putBoolean(CONFIG_GET_ONLINE_NEWS, getOnlineNews);
        shp.apply();
    }

    public boolean isGetOnlineNews() {
        return getOnlineNews;
    }

    public void setGetOnlineNews(boolean getOnlineNews) {
        this.getOnlineNews = getOnlineNews;
    }

    private void restore(@NonNull Context context) {
        SharedPreferences shp = context.getSharedPreferences(ALTERNATIVE_CONFIG, Context.MODE_PRIVATE);
        getOnlineNews = shp.getBoolean(CONFIG_GET_ONLINE_NEWS, getOnlineNews);
    }
}
