package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class NewsResponse {

    private final int totalResults;

    @Nullable
    private final List<Article> articles;

    public NewsResponse(int totalResults, @Nullable List<Article> articles) {
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public int getTotalResults() {
        return totalResults;
    }

    @Nullable
    public List<Article> getArticles() {
        return articles;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NewsResponse that = (NewsResponse) o;
        return getTotalResults() == that.getTotalResults() &&
                Objects.equals(getArticles(), that.getArticles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTotalResults(), getArticles());
    }
}
