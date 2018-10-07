package ru.fmtk.khlystov.newsgetter;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Objects;

public class Source implements Parcelable {

    public Source(@Nullable String id, @Nullable String name) {
        this.id = id;
        this.name = name;
    }

    protected Source(@NonNull Parcel in) {
        this(in.readString(), in.readString());
    }

    public static final Creator<Source> CREATOR = new Creator<Source>() {
        @Override
        public Source createFromParcel(Parcel in) {
            return new Source(in);
        }

        @Override
        public Source[] newArray(int size) {
            return new Source[size];
        }
    };

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Source source = (Source) o;
        return Objects.equals(getId(), source.getId()) &&
                Objects.equals(getName(), source.getName());
    }

    @Override
    public int hashCode() {

        return Objects.hash(getId(), getName());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
    }

    private final String id;
    private final String name;

}
