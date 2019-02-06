package ru.fmtk.khlystov.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;

import java.util.Locale;

public class ContextUtils {

    @NonNull
    public static Locale getCurrentLocale(@NonNull Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0);
        } else {
            //noinspection deprecation
            return context.getResources().getConfiguration().locale;
        }
    }

    public static boolean isHorizontalOrientation(@NonNull Context context) {
        return getOrientation(context) == Configuration.ORIENTATION_LANDSCAPE;
    }

    public static int getOrientation(@NonNull Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    @Nullable
    public static <T extends Activity, R> R doWithActivity(T activity, Function<T, R> function) {
        if (activity != null) {
            return function.apply(activity);
        }
        return null;
    }

    public static void popFragment(@Nullable FragmentActivity activity) {
        if (activity != null) {
            FragmentManager fragmentManager = activity.getSupportFragmentManager();
            if (fragmentManager != null) {
                fragmentManager.popBackStack();
            }
        }
    }

    private ContextUtils() {
        throw new IllegalAccessError("ContextUtils' constructor invocation.");
    }
}
