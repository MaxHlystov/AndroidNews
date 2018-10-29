package ru.fmtk.khlystov.androidnews;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;

import java.util.Locale;

public class ContextUtils {

    @NonNull
    public static Locale getCurrentLocale(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        }
        else {
            return context.getResources().getConfiguration().locale;
        }
    }

    public static boolean isHorizontalOrientation(@NonNull Context context) {
        return getOrientation(context) == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static int getOrientation(@NonNull Context context) {
        return context.getResources().getConfiguration().orientation;
    }
}
