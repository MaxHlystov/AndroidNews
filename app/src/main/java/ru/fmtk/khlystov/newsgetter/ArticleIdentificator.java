package ru.fmtk.khlystov.newsgetter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class ArticleIdentificator implements Parcelable {

    @NonNull
    public static final Creator<ArticleIdentificator> CREATOR = new Creator<ArticleIdentificator>() {
        @Override
        public ArticleIdentificator createFromParcel(@NonNull Parcel in) {
            return new ArticleIdentificator(in);
        }

        @Override
        public ArticleIdentificator[] newArray(int size) {
            return new ArticleIdentificator[size];
        }
    };

    @Nullable
    private final String title;

    @Nullable
    private final String author;

    public ArticleIdentificator(@NonNull Article article) {
        title = article.getTitle();
        if (article.getAuthor() == null) {
            author = "";
        } else {
            author = article.getAuthor();
        }
    }

    protected ArticleIdentificator(@NonNull Parcel in) {
        author = in.readString();
        title = in.readString();
    }


    @Nullable
    public String getTitle() {
        return title;
    }

    @Nullable
    public String getAuthor() {
        return author;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ArticleIdentificator articleId = (ArticleIdentificator) o;
        return Objects.equals(getTitle(), articleId.getTitle()) &&
                Objects.equals(getAuthor(), articleId.getAuthor());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle(), getAuthor());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(author);
        dest.writeString(title);
    }
}
