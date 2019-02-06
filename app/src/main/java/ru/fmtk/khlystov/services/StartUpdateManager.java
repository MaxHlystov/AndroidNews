package ru.fmtk.khlystov.services;


import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.fmtk.khlystov.AppConfig;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;

public class StartUpdateManager extends Worker {

    @NonNull
    private static final String LOG_TAG = "NewsAppStartUpdateManag";

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
