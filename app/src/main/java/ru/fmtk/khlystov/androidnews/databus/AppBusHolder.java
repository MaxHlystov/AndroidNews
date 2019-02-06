package ru.fmtk.khlystov.androidnews.databus;

import android.support.annotation.Nullable;

import ru.fmtk.khlystov.newsgetter.model.ArticleIdentificator;

public class AppBusHolder {

    @Nullable
    private static IDataBus<AppMessages, ArticleIdentificator> dataBus = null;

    public static synchronized IDataBus<AppMessages, ArticleIdentificator> register(
            IMessageReceiver<AppMessages, ArticleIdentificator> subscriber) {
        if (dataBus == null) {
            dataBus = new DataBus<>();
        }
        dataBus.subscribe(subscriber.getReceiverId(), subscriber);
        return dataBus;
    }

    public static synchronized void unregister(
            IMessageReceiver<AppMessages, ArticleIdentificator> subscriber) {
        if (dataBus != null) {
            dataBus.unsubscribe(subscriber.getReceiverId());
        }
    }

    private AppBusHolder() {
        throw new IllegalAccessError("AppBusHolder's constructor invocation.");
    }
}
