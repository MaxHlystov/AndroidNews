
package ru.fmtk.khlystov.newsgetter.webapi;

import java.io.Serializable;
import java.util.Objects;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTOMultimedium implements Serializable {

    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("format")
    @Expose
    private String format;
    @SerializedName("caption")
    @Expose
    private String caption;
    private final static long serialVersionUID = -7429356025502088525L;

    public DTOMultimedium(String url, String format, String caption) {
        super();
        this.url = url;
        this.format = format;
        this.caption = caption;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public String toString() {
        return "DTO-DTOMultimedium: " + url;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUrl());
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        DTOMultimedium multimedium = (DTOMultimedium) other;
        return Objects.equals(getUrl(), multimedium.getUrl());
    }

}
