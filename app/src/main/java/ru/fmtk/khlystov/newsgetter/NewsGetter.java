package ru.fmtk.khlystov.newsgetter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ru.fmtk.khlystov.NewsApplication;
import ru.fmtk.khlystov.androidnews.BuildConfig;
import ru.fmtk.khlystov.utils.AssetsReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class NewsGetter {

    private static final long idleTime = 2000; // 2 second

    @NonNull
    private static final String formatNewsURL = "https://newsapi.org/v2/top-headlines?country=%s&apiKey=%s";
    private static final int bufferSize = 8 * 1024;

    @Nullable
    private static String countryCode;

    private static boolean online;

    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @Nullable String countryCode,
                                                       boolean online) {
        if (newsObserver != null
                && NewsGetter.countryCode != null
                && NewsGetter.countryCode.equals(countryCode)
                && NewsGetter.online == online) {
            return newsObserver;
        }
        NewsGetter.countryCode = countryCode == null ? "us" : countryCode;
        NewsGetter.online = online;
        Gson gson = new Gson();
        newsObserver = Single.create((SingleEmitter<String> singleEmitter) -> {
            // In accordance to the item 5 step 5 hw 4
            Thread.sleep(idleTime);
            if (online) {
                createOnlineRequest(singleEmitter);
            } else {
                getOfflineNews(singleEmitter, context);
            }
        })
                .map((String it) -> gson.fromJson(it, NewsResponse.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        return newsObserver;
    }

    private static void createOnlineRequest(@NonNull SingleEmitter<String> emitter) throws IOException {
        URL url = new URL(String.format(formatNewsURL, countryCode, BuildConfig.APIkey));
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

    private static void getOfflineNews(@NonNull SingleEmitter<String> emitter, @NonNull Context context) {
        String offlineText = AssetsReader.ReadFromAssetFile(
                "offline_news_example.json",
                context);
        if (offlineText != null) {
            emitter.onSuccess(offlineText);
        } else {
            emitter.onError(new IOException("Error reading assets file."));
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
