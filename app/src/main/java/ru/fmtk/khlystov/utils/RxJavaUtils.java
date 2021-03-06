package ru.fmtk.khlystov.utils;

import android.support.annotation.Nullable;

import io.reactivex.disposables.Disposable;

public class RxJavaUtils {

    public static void disposeIfNotNull(@Nullable Disposable disposable) {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
