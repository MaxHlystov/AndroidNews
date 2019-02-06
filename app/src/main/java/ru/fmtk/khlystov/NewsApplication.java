package ru.fmtk.khlystov;

import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import io.reactivex.exceptions.UndeliverableException;
import io.reactivex.plugins.RxJavaPlugins;
import ru.fmtk.khlystov.services.NetworkStateHandler;
import ru.fmtk.khlystov.services.StartUpdateManager;


public class NewsApplication extends Application {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsApplication";

    @NonNull
    private static final String NEWS_UPDATES_WORK_TAG = "NewsApplication_NEWS_UPDATES_WORK_TAG";

    private static final long UPDATE_NEWS_HOURS_INTERVAL = 3;

    @Nullable
    private static NewsApplication newsApplication;

    @Override
    public void onCreate() {
        super.onCreate();
        newsApplication = this;
        setGlobalRxErrorHandlers();
        registerNetworkReciever();
        subscribeNewsUpdates();
    }

    public static Context getContext() {
        return newsApplication;
    }

    private void registerNetworkReciever() {
        // TODO: Разобраться, исправить
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
            NetworkRequest request = new NetworkRequest.Builder()
                    .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
                    .build();
            Intent intent = this.registerReceiver(NetworkStateHandler.getInstance().getNetworkReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
            manager.registerNetworkCallback(request,
                    PendingIntent.getBroadcast(
                            getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT));
        } else {
            registerReceiver(NetworkStateHandler.getInstance().getNetworkReceiver(),
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }
    }

    private void subscribeNewsUpdates() {
        Constraints updateConstraints = new Constraints.Builder()
                .setRequiresCharging(true)
                .setRequiredNetworkType(NetworkType.NOT_ROAMING)
                .build();
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(StartUpdateManager.class,
                UPDATE_NEWS_HOURS_INTERVAL, TimeUnit.HOURS)
                .setConstraints(updateConstraints)
                .addTag(NEWS_UPDATES_WORK_TAG)
                .build();
        WorkManager.getInstance().enqueueUniquePeriodicWork(NEWS_UPDATES_WORK_TAG,
                ExistingPeriodicWorkPolicy.KEEP, workRequest);
        WorkManager.getInstance().getWorkInfoByIdLiveData(workRequest.getId())
                .observeForever(workInfo -> {
                    Log.d(LOG_TAG, "Work manager onChanged. State is " + workInfo.getState());
                });
    }

    private void setGlobalRxErrorHandlers() {
        RxJavaPlugins.setErrorHandler(e -> {
            if (e instanceof UndeliverableException) {
                e = e.getCause();
            }
            if (e instanceof IOException) {
                // fine, irrelevant network problem or API that throws on cancellation
                return;
            }
            if (e instanceof InterruptedException) {
                // fine, some blocking code was interrupted by a disposeIfNotNull call
                return;
            }
            if ((e instanceof NullPointerException) || (e instanceof IllegalArgumentException)) {
                // that's likely a bug in the application
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            if (e instanceof IllegalStateException) {
                // that's a bug in RxJava or in a custom operator
                Thread.currentThread().getUncaughtExceptionHandler()
                        .uncaughtException(Thread.currentThread(), e);
                return;
            }
            Log.w(LOG_TAG, "Undeliverable exception received, not sure what to do", e);
        });
    }
}
