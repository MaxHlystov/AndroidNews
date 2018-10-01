package ru.fmtk.khlystov.thirdpartyintentutils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;


public class MailIntent {

    private MailIntent() {
    }

    @NonNull
    public static Intent get(@NonNull String to,
                             @NonNull String subject,
                             @NonNull String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        String mailto = "mailto:" + to + "?subject=" + subject + "&body=" + msg;
        intent.setData(Uri.parse(mailto));
        return intent;
    }
}
