package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import ru.fmtk.khlystov.newsgetter.Article;
import ru.fmtk.khlystov.newsgetter.NewsResponse;
import ru.fmtk.khlystov.newsgetter.NewsSection;
import ru.fmtk.khlystov.utils.fashionutils.STDDateConverter;

public class DTONewsConverter {

    @NonNull
    private static final String NYT_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZ";

    @NonNull
    private static final String IMAGE_FORMAT = "superJumbo";

    private DTONewsConverter() {
        throw new IllegalAccessError("DTONewsConverter's constructor invocation.");
    }

    @NonNull
    public static NewsResponse convertToNewsResponse(@Nullable DTONewsResponse dtoNewsResponse) {
        if (dtoNewsResponse == null) {
            return new NewsResponse(0, null);
        }
        List<Article> articles = new ArrayList<>();
        List<DTOResult> results = dtoNewsResponse.getResults();
        if (results != null) {
            for (DTOResult result : results) {
                articles.add(convertToArticle(result));
            }
        }
        return new NewsResponse(articles.size(), articles);
    }

    @NonNull
    public static Article convertToArticle(@NonNull DTOResult dtoResult) {
        Date date = STDDateConverter.unconvert(dtoResult.getPublishedDate(), NYT_DATE_FORMAT);
        return new Article.Builder(
                NewsSection.getByID(dtoResult.getSection()),
                dtoResult.getTitle(),
                date)
                .setAuthor(dtoResult.getByline())
                .setSubsection(dtoResult.getSubsection())
                .setDescription(dtoResult.getAbstract())
                .setContent(dtoResult.getAbstract())
                .setUrl(dtoResult.getUrl())
                .setUrlToImage(getNormalImageUrl(dtoResult.getMultimedia()))
                .build();
    }

    @Nullable
    private static String getNormalImageUrl(@Nullable List<DTOMultimedium> multimedia) {
        if (multimedia != null) {
            for (DTOMultimedium dtoMultimedium : multimedia) {
                if (Objects.equals(IMAGE_FORMAT, dtoMultimedium.getFormat())) {
                    return dtoMultimedium.getUrl();
                }
            }
        }
        return null;
    }
}