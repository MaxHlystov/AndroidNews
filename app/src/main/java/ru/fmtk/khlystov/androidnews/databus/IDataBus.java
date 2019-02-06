package ru.fmtk.khlystov.androidnews.databus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;


/**
 * Defines message bus which translate a message from one source to all subscribers.
 *
 * @param <T> type of messages.
 * @param <R> type of an argument.
 */
public interface IDataBus<T, R> {

    void subscribe(int subscriberId, @NonNull IMessageReceiver<T, R> subscriber);

    void unsubscribe(int subscriberId);

    void sendTo(@NonNull IMessageReceiver<T, R> sender, @NonNull IMessageReceiver<T, R> receiver,
                @NonNull T message, @Nullable R argument);

    void sendToAll(@NonNull IMessageReceiver<T, R> sender,
                   @NonNull T message,
                   @Nullable R argument);
}
