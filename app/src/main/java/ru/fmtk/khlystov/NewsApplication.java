package ru.fmtk.khlystov;

import android.app.Application;
import android.util.Log;

import io.reactivex.plugins.RxJavaPlugins;

public class NewsApplication extends Application {

    public static final String LOG_TAG = "NewsApp";

    @Override
    public void onCreate() {
        super.onCreate();
        RxJavaPlugins.setErrorHandler((Throwable e) -> {
            Log.e(LOG_TAG, "Global error handler", e);
        });
    }
}
