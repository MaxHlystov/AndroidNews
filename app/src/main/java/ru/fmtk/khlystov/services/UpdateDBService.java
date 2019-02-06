package ru.fmtk.khlystov.services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import ru.fmtk.khlystov.androidnews.MainActivity;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.newsgetter.NewsGetway;
import ru.fmtk.khlystov.newsgetter.model.NewsSection;
import ru.fmtk.khlystov.utils.RxJavaUtils;

public class UpdateDBService extends Service {

    @NonNull
    private static String LOG_TAG = "NewsAppUpdateDBService";

    @NonNull
    private static final String UPDATE_DB_SERVICE_SECTION = "UPDATE_DB_SERVICE_SECTION_KEY";

    @NonNull
    private static final String UPDATE_DB_SERVICE_STOP_ACTION = "UPDATE_DB_SERVICE_STOP_ACTION";

    @NonNull
    private static final String CHANNEL_ID = "1";

    private static final int UPDATE_DB_SERVICE_NOTIFICATION_ID = 333;

    private static final int UPDATE_DB_SERVICE_UPDATE_DELAY_SEC = 10;

    private static final int UPDATE_DB_SERVICE_WAIT_FOR_CONNECTION_MIN = 1;

    @Nullable
    private Disposable disposableUpdateNews = null;

    public static void start(@Nullable Context context, @Nullable NewsSection newsSection) {
        Log.d(LOG_TAG, "Update static invokation.");
        if (context != null && newsSection != null) {
            Intent intent = new Intent(context, UpdateDBService.class);
            intent.putExtra(UPDATE_DB_SERVICE_SECTION, newsSection.getID());
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent);
            } else {
                context.startService(intent);
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if (intent != null) {
            if (intent.getAction() == UPDATE_DB_SERVICE_STOP_ACTION) {
                ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).cancel(
                        UPDATE_DB_SERVICE_NOTIFICATION_ID);
                stopUpdate();
            } else {
                startUpdate(intent);
            }
            showForegroundNotification();
        }
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
        disposableUpdateNews = null;
        super.onDestroy();
    }

    private void startUpdate(Intent intent) {
        Log.d(LOG_TAG, "Local startUpdate");
        NewsSection newsSection = NewsSection.getByID(intent.getStringExtra(UPDATE_DB_SERVICE_SECTION));
        if (newsSection != null) {
            RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
            disposableUpdateNews = NetworkStateHandler.getInstance().getOnlineNetwork()
                    .timeout(UPDATE_DB_SERVICE_WAIT_FOR_CONNECTION_MIN, TimeUnit.MINUTES)
                    .flatMapCompletable(aLong -> NewsGetway.retrieveOnlineNewsWithDelay(
                            getApplicationContext(),
                            newsSection,
                            UPDATE_DB_SERVICE_UPDATE_DELAY_SEC,
                            TimeUnit.SECONDS))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::onNewsLoadingComplete,
                            this::showErrorLoading);
        }
    }

    private void stopUpdate() {
        Log.d(LOG_TAG, "stopUpdate");
        RxJavaUtils.disposeIfNotNull(disposableUpdateNews);
        disposableUpdateNews = null;
        stopSelf();
    }

    private void onNewsLoadingComplete() {
        Log.d(LOG_TAG, "onNewsLoadingComplete");
        notifyAndStop(getString(R.string.update_db_service__news_updated_successfully));
    }

    private void showErrorLoading(@Nullable Throwable throwable) {
        Log.e(LOG_TAG, getString(R.string.news_list_fragment__error_loading_news), throwable);
        notifyAndStop(getString(R.string.update_db_service__error_news_update));
    }

    private void notifyAndStop(@NonNull String message) {
        showEndNotification(message);
        stopUpdate();
    }

    private void showForegroundNotification() {
        Log.d(LOG_TAG, "Show foreground notification.");
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent piStop = PendingIntent.getService(this, 0,
                new Intent(this, UpdateDBService.class)
                        .setAction(UPDATE_DB_SERVICE_STOP_ACTION),
                PendingIntent.FLAG_CANCEL_CURRENT);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.update_db_service__notification_title))
                .setContentText(getString(R.string.update_db_service__news_updating))
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT))
                .addAction(R.drawable.ic_stop_black_24dp, getString(R.string.update_db_service__stop), piStop)
                .setOngoing(true);
        if (notificationBuilder != null) {
            startForeground(UPDATE_DB_SERVICE_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public void showEndNotification(@NonNull String text) {
        Log.d(LOG_TAG, "showotification: " + text);
        Intent notificationIntent = new Intent(getApplicationContext(), MainActivity.class);
        //notificationIntent.setFlags(Intent.FLAG_ACTIVITY_ | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                getApplicationContext(), CHANNEL_ID);
        notificationBuilder.setSmallIcon(R.mipmap.ic_launcher_round)
                .setContentTitle(getString(R.string.update_db_service__notification_title))
                .setContentText(text)
                .setContentIntent(PendingIntent.getActivity(this, 0,
                        notificationIntent, PendingIntent.FLAG_CANCEL_CURRENT));
        if (notificationBuilder != null) {
            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).notify(
                    UPDATE_DB_SERVICE_NOTIFICATION_ID,
                    notificationBuilder.build());
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        NotificationManager mNotificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE);
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel mChannel = new NotificationChannel(
                CHANNEL_ID,
                getString(R.string.update_db_service__notifications_channel_name),
                importance);
        // Configure the notification channel.
        mChannel.setDescription(getString(R.string.update_db_service__notifications_channel_descr));
        mChannel.enableLights(true);
        // Sets the notification light color for notifications posted to this
        // channel, if the device supports this feature.
        mChannel.setLightColor(Color.RED);

        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});

        mNotificationManager.createNotificationChannel(mChannel);
    }
}
