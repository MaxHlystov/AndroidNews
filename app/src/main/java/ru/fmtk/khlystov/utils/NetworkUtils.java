package ru.fmtk.khlystov.utils;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import ru.fmtk.khlystov.NewsApplication;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class NetworkUtils {

    @NonNull
    private static final String LOG_TAG = "NewsAppNetworkUtils";

    public static void getImgToImageView(String imageUrl, ImageView imageView, int targetWidth, int targetHeight) {
        Picasso.get().load(imageUrl)
                .resize(targetWidth, targetHeight)
                .centerInside()
                .onlyScaleDown()
                .into(imageView);
    }

    public static boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager)
                NewsApplication.getContext().getSystemService(CONNECTIVITY_SERVICE);
        if (cm == null) {
            return false;
        }
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        // if no network is available networkInfo will be null
        // otherwise check if we are connected
        return networkInfo != null && networkInfo.isConnected();
    }

    private NetworkUtils() {
        throw new IllegalAccessError("NetworkUtils's constructor invocation.");
    }
}
