package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.fmtk.khlystov.androidnews.BuildConfig;

public class OnlineNewsSupplier implements INewsSupplier {

    @NonNull
    private static final String LOG_TAG = "NewsAppOnlineSupplier";

    @NonNull
    private static final String FORMAT_NEWS_URL = "https://newsapi.org/v2/top-headlines?country=%s&apiKey=%s";
    private static final int BUFFER_SIZE = 8 * 1024;


    @NonNull
    private final String countryCode;

    public OnlineNewsSupplier(@NonNull String countryCode) {
        this.countryCode = countryCode;
    }

    @Nullable
    @Override
    public String get() throws IOException {
        URL url = new URL(String.format(FORMAT_NEWS_URL, countryCode, BuildConfig.APIkey));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        Log.d(LOG_TAG, "---------- Start to get online news ------------");
        try {
            urlConn.connect();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(urlConn.getResponseMessage());
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader, BUFFER_SIZE);
                StringWriter buffer = new StringWriter();
                copyTo(bufferedReader, buffer);
                return buffer.toString();
            }
        } finally {
            urlConn.disconnect();
        }
    }

    private static void copyTo(@NonNull BufferedReader bufferedReader,
                               @NonNull Writer out) throws IOException {
        char[] buffer = new char[BUFFER_SIZE];
        while (true) {
            int chars = bufferedReader.read(buffer);
            if (chars < 0) {
                break;
            }
            out.write(buffer, 0, chars);
        }
    }
}
