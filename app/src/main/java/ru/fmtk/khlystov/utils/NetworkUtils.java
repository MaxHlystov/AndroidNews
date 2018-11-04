package ru.fmtk.khlystov.utils;

import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class NetworkUtils {

    public static void getImgToImageView(String imageUrl, ImageView imageView, int targetWidth, int targetHeight) {
        Picasso.get().load(imageUrl)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .onlyScaleDown()
                .into(imageView);
    }

    private NetworkUtils() {
        throw new IllegalAccessError("NetworkUtils's constructor invocation.");
    }
}
