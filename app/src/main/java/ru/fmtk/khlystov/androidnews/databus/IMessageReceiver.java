package ru.fmtk.khlystov.androidnews.databus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface IMessageReceiver<T, R> {
    int getReceiverId();
    void onMessageSent(int senderId, @NonNull T message, @Nullable R argument);
}
