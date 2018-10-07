package ru.fmtk.khlystov.newsgetter;

import java.util.ArrayList;
import java.util.Objects;

public class NewsResponse {

    public NewsResponse(String status, int totalResults, ArrayList<Article> articles) {
        this.status = status;
        this.totalResults = totalResults;
        this.articles = articles;
    }

    public String getStatus() {
        return status;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public ArrayList<Article> getArticles() {
        return articles;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NewsResponse that = (NewsResponse) o;
        return getTotalResults() == that.getTotalResults() &&
                Objects.equals(getStatus(), that.getStatus()) &&
                Objects.equals(getArticles(), that.getArticles());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getStatus(), getTotalResults(), getArticles());
    }

    private final String status;
    private final int totalResults;
    private final ArrayList<Article> articles;
}
