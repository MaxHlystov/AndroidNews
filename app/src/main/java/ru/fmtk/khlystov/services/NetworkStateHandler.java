package ru.fmtk.khlystov.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import ru.fmtk.khlystov.utils.NetworkUtils;

public class NetworkStateHandler {

    @NonNull
    private static final String LOG_TAG = "NewsAppNetworkStateHand";

    @Nullable
    private static NetworkStateHandler instance;

    @NonNull
    private NetworkStateHandler.networkReceiver networkReceiver = new networkReceiver();

    @NonNull
    private Subject<Boolean> mNetworkState = BehaviorSubject.createDefault(
            NetworkUtils.isNetworkAvailable());

    @NonNull
    public static NetworkStateHandler getInstance() {
        if (instance == null) {
            instance = new NetworkStateHandler();
        }
        return instance;
    }

    public NetworkStateHandler.networkReceiver getNetworkReceiver() {
        return networkReceiver;
    }

    public Single<Boolean> getOnlineNetwork() {
        return mNetworkState
                .subscribeOn(Schedulers.io())
                .filter(online -> online)
                .firstOrError();
    }

    private NetworkStateHandler() {
    }

    private class networkReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(LOG_TAG, "onReceive");
            mNetworkState.onNext(NetworkUtils.isNetworkAvailable());
        }
    }

}
