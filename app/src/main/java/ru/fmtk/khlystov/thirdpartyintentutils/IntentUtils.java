package ru.fmtk.khlystov.thirdpartyintentutils;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

public class IntentUtils {

    private IntentUtils() {
        throw new IllegalAccessError("Attempt to instantiate utility class.");
    }

    public static void showIntent(@NonNull Activity activity,
                           @NonNull View parent,
                           @NonNull Intent intent,
                           @NonNull String errorMessage) {
        if (intent.resolveActivity(activity.getPackageManager()) != null) {
            activity.startActivity(intent);
        } else {
            Snackbar.make(parent, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }
}
