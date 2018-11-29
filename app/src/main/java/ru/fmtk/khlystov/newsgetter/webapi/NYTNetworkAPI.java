package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.concurrent.TimeUnit;

import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.fmtk.khlystov.androidnews.BuildConfig;

public class NYTNetworkAPI {

    private static final int TIMEOUT_IN_SECONDS = 10;

    @NonNull
    private static final String FORMAT_NEWS_URL = "https://api.nytimes.com";

    @NonNull
    public static Single<DTONewsResponse> createOnlineRequest(String section) {
        OkHttpClient okHttpClient = getHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FORMAT_NEWS_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(NYTRetrofitEndpoint.class)
                .getSection(section);
    }

    @NonNull
    private static OkHttpClient getHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new APIKeyInterceptor(BuildConfig.NYT_APIkey));
        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
            logging.setLevel(HttpLoggingInterceptor.Level.BASIC);
            builder.addInterceptor(logging);
        }
        return builder
                .connectTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .readTimeout(TIMEOUT_IN_SECONDS, TimeUnit.SECONDS)
                .build();
    }
}
