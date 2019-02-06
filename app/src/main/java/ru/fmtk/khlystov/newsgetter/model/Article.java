package ru.fmtk.khlystov.newsgetter.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;
import java.util.Objects;

public class Article implements Parcelable {

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

    @NonNull
    private final NewsSection section;

    @Nullable
    private final String title;

    @Nullable
    private final Date publishedAt;

    @Nullable
    private final String subsection;

    @Nullable
    private final String author;

    @Nullable
    private final String description;

    @Nullable
    private final String content;

    @Nullable
    private final String url;

    @Nullable
    private final String urlToImage;

    protected Article(@NonNull Builder builder) {
        this.section = builder.section;
        this.title = builder.title;
        this.publishedAt = builder.publishedAt;
        this.subsection = builder.subsection;
        this.author = builder.author;
        this.description = builder.description;
        this.content = builder.content;
        this.url = builder.url;
        this.urlToImage = builder.urlToImage;
    }

    protected Article(@NonNull Parcel in) {
        section = NewsSection.getByID(in.readString());
        subsection = in.readString();
        author = in.readString();
        title = in.readString();
        description = in.readString();
        content = in.readString();
        url = in.readString();
        urlToImage = in.readString();
        long tmpPublishedAt = in.readLong();
        publishedAt = tmpPublishedAt != -1 ? new Date(tmpPublishedAt) : null;
    }

    @NonNull
    public NewsSection getSection() {
        return section;
    }

    @Nullable
    public String getSubsection() {
        return subsection;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getDescription() {
        return description;
    }

    @Nullable
    public String getContent() {
        return content;
    }

    @Nullable
    public String getUrl() {
        return url;
    }

    @Nullable
    public String getUrlToImage() {
        return urlToImage;
    }

    @Nullable
    public Date getPublishedAt() {
        return publishedAt;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Article article = (Article) o;
        return Objects.equals(getTitle(), article.getTitle()) &&
                Objects.equals(getSection(), article.getSection()) &&
                Objects.equals(getPublishedAt(), article.getPublishedAt());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getSection(), getPublishedAt());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(section.getID());
        dest.writeString(subsection);
        dest.writeString(author);
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(content);
        dest.writeString(url);
        dest.writeString(urlToImage);
        dest.writeLong(publishedAt != null ? publishedAt.getTime() : -1L);
    }

    public static class Builder {
        @NonNull
        private NewsSection section;

        @Nullable
        private String title;

        @Nullable
        private Date publishedAt;

        @Nullable
        private String subsection;

        @Nullable
        private String author;

        @Nullable
        private String description;

        @Nullable
        private String content;

        @Nullable
        private String url;

        @Nullable
        private String urlToImage;


        public Builder(@NonNull Article article) {
            this.section = article.getSection();
            this.title = article.getTitle();
            this.publishedAt = article.getPublishedAt();
            setSubsection(article.getSubsection());
            setAuthor(article.getAuthor());
            setDescription(article.getDescription());
            setContent(article.getContent());
            setUrl(article.getUrl());
            setUrlToImage(article.getUrlToImage());
        }

        public Builder(@NonNull NewsSection section,
                       @Nullable String title,
                       @Nullable Date publishedAt)

        {
            this.section = section;
            this.title = title;
            this.publishedAt = publishedAt;
        }

        public void setSection(@NonNull NewsSection section) {
            this.section = section;
        }

        public void setTitle(@Nullable String title) {
            this.title = title;
        }

        public void setPublishedAt(@Nullable Date publishedAt) {
            this.publishedAt = publishedAt;
        }

        @NonNull
        public Builder setSubsection(@Nullable String subsection) {
            this.subsection = subsection;
            return this;
        }

        @NonNull
        public Builder setAuthor(@Nullable String author) {
            this.author = author;
            return this;
        }

        @NonNull
        public Builder setDescription(@Nullable String description) {
            this.description = description;
            return this;
        }

        @NonNull
        public Builder setContent(@Nullable String content) {
            this.content = content;
            return this;
        }

        @NonNull
        public Builder setUrl(@Nullable String url) {
            this.url = url;
            return this;
        }

        @NonNull
        public Builder setUrlToImage(@Nullable String urlToImage) {
            this.urlToImage = urlToImage;
            return this;
        }

        @NonNull
        public Article build() {
            return new Article(this);
        }
    }
}
