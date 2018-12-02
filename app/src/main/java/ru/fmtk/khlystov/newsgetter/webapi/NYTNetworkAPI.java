package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Nullable
    private static NYTRetrofitEndpoint retrofitEndpoint;

    @NonNull
    public static Single<DTONewsResponse> createOnlineRequest(String section) {
        if (retrofitEndpoint == null) {
            retrofitEndpoint = createRetrofitEndpoint();
        }
        return retrofitEndpoint.getSection(section);
    }

    @NonNull
    private static NYTRetrofitEndpoint createRetrofitEndpoint() {
        OkHttpClient okHttpClient = createOkHttpClient();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FORMAT_NEWS_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        return retrofit.create(NYTRetrofitEndpoint.class);
    }

    @NonNull
    private static OkHttpClient createOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                .addInterceptor(new APIKeyInterceptor(BuildConfig.NYT_API_KEY));
        if (BuildConfig.DEBUG) {
            final HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
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
