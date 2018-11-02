package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import io.reactivex.SingleEmitter;
import ru.fmtk.khlystov.NewsApplication;
import ru.fmtk.khlystov.androidnews.BuildConfig;

public class NetworkAPI {

    @NonNull
    private static final String formatNewsURL = "https://api.nytimes.com/svc/topstories/v2/%s.json?api-key=%s";
    private static final int bufferSize = 8 * 1024;


    public static void createOnlineRequest(@NonNull SingleEmitter<String> emitter, String section) throws IOException {
        URL url = new URL(String.format(formatNewsURL, section, BuildConfig.NYT_APIkey));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        try {
            urlConn.connect();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                emitter.onError(new RuntimeException(urlConn.getResponseMessage()));
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader, bufferSize);
                StringWriter buffer = new StringWriter();
                copyTo(bufferedReader, buffer);
                String s = buffer.toString();
                emitter.onSuccess(s);
            }
        } catch (IOException ex) {
            Log.d(NewsApplication.LOG_TAG, "Error", ex);
            emitter.onError(ex);
        } finally {
            urlConn.disconnect();
        }
    }

    private static long copyTo(@NonNull BufferedReader bufferedReader,
                               @NonNull Writer out) throws IOException {
        long charsCopied = 0;
        char[] buffer = new char[bufferSize];
        while (true) {
            int chars = bufferedReader.read(buffer);
            if (chars < 0) {
                break;
            }
            out.write(buffer, 0, chars);
            charsCopied += chars;
        }
        return charsCopied;
    }
}
