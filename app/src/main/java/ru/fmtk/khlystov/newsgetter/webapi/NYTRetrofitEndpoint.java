package ru.fmtk.khlystov.newsgetter.webapi;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface NYTRetrofitEndpoint {
    @GET("svc/topstories/v2/{section}.json")
    Single<DTONewsResponse> getSection(@Path("section") String section);
}
