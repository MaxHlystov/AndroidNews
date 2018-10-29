package ru.fmtk.khlystov.newsgetter;

import android.support.annotation.Nullable;

import java.util.List;
import java.util.Objects;

public class NewsResponse {

    @Nullable
    private final String status;
    private final int totalResults;

    @Nullable
    private final List<Article> articles;

    public NewsResponse(@Nullable String status, int totalResults, @Nullable List<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    @Nullable
    public String getStatus() {
        return status;
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
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getArticles(), that.getArticles());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getTotalResults(), getArticles());
    }
}
