package ru.fmtk.khlystov.thirdpartyintentutils;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;


public class BrowserIntent {

    private BrowserIntent() {
    }

    @NonNull
    public static Intent get(@NonNull String url) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
        intent.setData(Uri.parse(url));
        return intent;
    }
}
