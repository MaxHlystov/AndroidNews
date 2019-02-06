package ru.fmtk.khlystov.services;


import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;

public class StartUpdateManager extends Worker {

    @NonNull
    private static String LOG_TAG = "NewsAppStartUpdateManager";

    public StartUpdateManager(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(LOG_TAG, "Work started.");
        Context context = getApplicationContext();
        AppConfig appConfig = AppConfig.getAppConfig(getApplicationContext());
        NewsSection newsSection = appConfig.getNewsSection();
        UpdateDBService.start(context, newsSection);
        return Result.success();
    }
}
