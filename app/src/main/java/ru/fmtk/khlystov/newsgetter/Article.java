package ru.fmtk.khlystov.newsgetter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Objects;

public class Article implements Parcelable {

    public Article(@Nullable Source source,
                   @Nullable String author,
                   @Nullable String title,
                   @Nullable String description,
                   @Nullable String content,
                   @Nullable String url,
                   @Nullable String urlToImage,
                   @Nullable Date publishedAt)

    {
        this.source = source;
        this.author = author;
        this.title = title;
        this.description = description;
        this.content = content;
        this.url = url;
        this.urlToImage = urlToImage;
        this.publishedAt = publishedAt;
    }

    protected Article(@NonNull Parcel in) {
        source = (Source) in.readValue(Source.class.getClassLoader());
        author = in.readString();
        title = in.readString();
        description = in.readString();
        content = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        long tmpPublishedAt = in.readLong();
        publishedAt = tmpPublishedAt != -1 ? new Date(tmpPublishedAt) : null;
    }

    public @Nullable
    Source getSource() {
        return source;
    }

    public @Nullable
    String getAuthor() {
        return author;
    }

    public @Nullable
    String getTitle() {
        return title;
    }

    public @Nullable
    String getDescription() {
        return description;
    }

    public @Nullable
    String getContent() {
        return content;
    }

    public @Nullable
    String getUrl() {
        return url;
    }

    public @Nullable
    String getUrlToImage() {
        return urlToImage;
    }

    public @Nullable
    Date getPublishedAt() {
        return publishedAt;
    }

    public @Nullable
    String getSourceName() {
        if (source != null) {
            return source.getName();
        }
        return null;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Article article = (Article) o;
        return Objects.equals(getAuthor(), article.getAuthor()) &&
                Objects.equals(getTitle(), article.getTitle()) &&
                Objects.equals(getPublishedAt(), article.getPublishedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthor(), getTitle(), getPublishedAt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeValue(source);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(urlToImage);
        dest.writeLong(publishedAt != null ? publishedAt.getTime() : -1L);
    }

    @NonNull
    public static final Parcelable.Creator<Article> CREATOR = new Parcelable.Creator<Article>() {
        @Override
        public Article createFromParcel(@NonNull Parcel in) {
            return new Article(in);
        }

        @Override
        public Article[] newArray(int size) {
            return new Article[size];
        }
    };

    @Nullable
    private final Source source;

    @Nullable
    private final String author;

    @Nullable
    private final String title;

    @Nullable
    private final String description;

    @Nullable
    private final String content;

    @Nullable
    private final String url;

    @Nullable
    private final String urlToImage;

    @Nullable
    private final Date publishedAt;
}
