package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class APIKeyInterceptor implements Interceptor {

    @NonNull
    private static final String PARAM_API_KEY = "api-key";

    @NonNull
    private final String apiKey;

    public APIKeyInterceptor(@NonNull String apiKey) {
        this.apiKey = apiKey;
    }


    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl url = request.url()
                .newBuilder()
                .addQueryParameter(PARAM_API_KEY, apiKey)
                .build();
        return chain.proceed(request.newBuilder()
                .url(url)
                .build());
    }
}
