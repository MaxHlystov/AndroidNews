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
import ru.fmtk.khlystov.androidnews.BuildConfig;
import ru.fmtk.khlystov.androidnews.R;
import ru.fmtk.khlystov.utils.AssetsReader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.TimeUnit;

public class NewsGetter {

    @NonNull
    private static final String LOG_TAG = "NewsAppNewsGetter";

    private static final long IDLE_TIME_SECONDS = 4;

    @NonNull
    private static final String COUNTRY_CODE_BY_DEFAULT = "us";

    @NonNull
    private static final String FORMAT_NEWS_URL = "https://newsapi.org/v2/top-headlines?country=%s&apiKey=%s";
    private static final int BUFFER_SIZE = 8 * 1024;

    @Nullable
    private static Gson gson = null;

    @Nullable
    private static String countryCode;

    private static boolean online;

    private static int callNumber = 0;
    @Nullable
    private static Single<NewsResponse> newsObserver = null;

    private NewsGetter() {
        throw new IllegalAccessError("NewsGetter's constructor invocation.");
    }

    @Nullable
    public static Single<NewsResponse> getNewsObserver(@NonNull Context context,
                                                       @Nullable String countryCode,
                                                       boolean online) {
        Log.d(LOG_TAG, "On getNewsObserver " + Thread.currentThread() + "; news observer: " + newsObserver);
        if (newsObserver == null
                || NewsGetter.countryCode == null
                || !NewsGetter.countryCode.equals(countryCode)
                || NewsGetter.online != online) {
            setNewsObserver(context, countryCode, online);
        }
        else {
            Log.d(LOG_TAG, "Takes existing observer");
        }
        return newsObserver;
    }

    @Nullable
    private static void setNewsObserver(@NonNull Context context,
                                        @Nullable String countryCode,
                                        boolean online) {
        Log.d(LOG_TAG, "Init call number: " + Integer.toString(NewsGetter.callNumber));
        NewsGetter.callNumber++;
        NewsGetter.countryCode = countryCode == null ? COUNTRY_CODE_BY_DEFAULT : countryCode;
        NewsGetter.online = online;
        if (gson == null) gson = new Gson();
        newsObserver = Single.create((SingleEmitter<String> singleEmitter) -> {
            if (online) {
                createOnlineRequest(singleEmitter);
            } else {
                getOfflineNews(singleEmitter, context);
            }
        })
                .doOnSuccess(it -> {
                    Log.d(LOG_TAG, "Made call number: " + Integer.toString(NewsGetter.callNumber));
                })
                // In accordance to the item 5 step 5 hw 4
                .delay(IDLE_TIME_SECONDS, TimeUnit.SECONDS)
                .map((String it) -> gson.fromJson(it, NewsResponse.class))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static void createOnlineRequest(@NonNull SingleEmitter<String> emitter) throws IOException {
        URL url = new URL(String.format(FORMAT_NEWS_URL, countryCode, BuildConfig.APIkey));
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        try {
            urlConn.connect();
            if (urlConn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                emitter.onError(new RuntimeException(urlConn.getResponseMessage()));
            } else {
                InputStreamReader inputStreamReader = new InputStreamReader(urlConn.getInputStream(), "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader, BUFFER_SIZE);
                StringWriter buffer = new StringWriter();
                copyTo(bufferedReader, buffer);
                String s = buffer.toString();
                emitter.onSuccess(s);
            }
        } catch (IOException ex) {
            Log.d(LOG_TAG, "Error", ex);
            emitter.onError(ex);
        } finally {
            urlConn.disconnect();
        }
    }

    private static void getOfflineNews(@NonNull SingleEmitter<String> emitter, @NonNull Context context) {
        String offlineText = AssetsReader.readFromAssetFile(
                R.raw.offline_news_example,
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
        char[] buffer = new char[BUFFER_SIZE];
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
