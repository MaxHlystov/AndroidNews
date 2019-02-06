package ru.fmtk.khlystov.androidnews.databus;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class DataBus<T, R> implements IDataBus<T, R> {

    @NonNull
    private final ConcurrentMap<Integer, IMessageReceiver<T, R>> subscribers =
            new ConcurrentHashMap<>();

    @Override
    public void subscribe(int subscriberId, @NonNull IMessageReceiver<T, R> subscriber) {
        subscribers.put(subscriberId, subscriber);
    }

    @Override
    public void unsubscribe(int subscriberId) {
        subscribers.remove(subscriberId);
    }

    @Override
    public void sendTo(@NonNull IMessageReceiver<T, R> sender,
                       @NonNull IMessageReceiver<T, R> receiver,
                       @NonNull T message,
                       @Nullable R argument) {
        int senderId = sender.getReceiverId();
        int receiverId = receiver.getReceiverId();
        if (subscribers.containsKey(receiverId)) {
            receiver.onMessageSent(senderId, message, argument);
        }
    }

    @Override
    public void sendToAll(@NonNull IMessageReceiver<T, R> sender,
                          @NonNull T message,
                          @Nullable R argument) {
        int senderId = sender.getReceiverId();
        for (Map.Entry<Integer, IMessageReceiver<T, R>> entry : subscribers.entrySet()) {
            int receiverId = entry.getKey();
            if (receiverId != senderId) {
                entry.getValue().onMessageSent(receiverId, message, argument);
            }
        }
    }
}
