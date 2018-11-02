package ru.fmtk.khlystov.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;

public class IntentUtils {

    public static void showIntent(@NonNull Context context,
                                  @NonNull View parent,
                                  @NonNull Intent intent,
                                  @NonNull String errorMessage) {
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Snackbar.make(parent, errorMessage, Snackbar.LENGTH_LONG).show();
        }
    }

    @NonNull
    public static Intent getBrowserIntent(@NonNull String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        return intent;
    }

    @NonNull
    public static Intent getMailIntent(@NonNull String to,
                                       @NonNull String subject,
                                       @NonNull String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String mailto = "mailto:" + to + "?subject=" + subject + "&body=" + msg;
        intent.setData(Uri.parse(mailto));
        return intent;
    }

    private IntentUtils() {
        throw new IllegalAccessError("Attempt to instantiate utility class.");
    }
}
