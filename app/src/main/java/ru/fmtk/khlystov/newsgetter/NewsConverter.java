package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.fmtk.khlystov.newsgetter.webapi.DTOMultimedium;
import ru.fmtk.khlystov.newsgetter.webapi.DTONewsResponse;
import ru.fmtk.khlystov.newsgetter.webapi.DTOResult;

import static android.text.TextUtils.isEmpty;

public class NewsConverter {

    @NonNull
    private static final String DateFormat = "EEE MMM d HH:mm:ss zz yyyy";

    @NonNull
    private static final String imageFormat = "superJumbo";

    @Nullable
    private static Source NYTSource;

    private NewsConverter() {
        throw new IllegalAccessError("NewsConverter's constructor invocation.");
    }

    @NonNull
    public static NewsResponse convertToNewsResponse(@Nullable DTONewsResponse dtoNewsResponse) {
        if (dtoNewsResponse != null) {
            List<Article> articles = new ArrayList<>();
            List<DTOResult> results = dtoNewsResponse.getResults();
            if (results != null) {
                for (DTOResult result : results) {
                    articles.add(convertToArticle(result));
                }
            }
            return new NewsResponse(dtoNewsResponse.getStatus(), articles.size(), articles);
        }
        return new NewsResponse("Error", 0, null);
    }

    @NonNull
    public static Article convertToArticle(@NonNull DTOResult dtoResult) {
        if (NYTSource == null) NYTSource = new Source("NYT", "NYT");
        return new Article(NYTSource,
                dtoResult.getSection(),
                null,
                dtoResult.getTitle(),
                dtoResult.getAbstract(),
                dtoResult.getAbstract(),
                dtoResult.getUrl(),
                getNormalImageUrl(dtoResult.getMultimedia()),
                stringToDate(dtoResult.getPublishedDate()));
    }

    @Nullable
    private static String getNormalImageUrl(@Nullable List<DTOMultimedium> multimedia) {
        if (multimedia != null) {
            for (DTOMultimedium dtoMultimedium : multimedia) {
                if (Objects.equals(imageFormat, dtoMultimedium.getFormat())) {
                    String url = dtoMultimedium.getUrl();
                    if (!isEmpty(url)) {
                        return url;
                    }
                }
            }
        }
        return null;
    }

    @Nullable
    private static Date stringToDate(@Nullable String date) {
        if (date == null) return null;
        ParsePosition pos = new ParsePosition(0);
        SimpleDateFormat simpledateformat = new SimpleDateFormat(DateFormat);
        return simpledateformat.parse(date, pos);

    }
}
