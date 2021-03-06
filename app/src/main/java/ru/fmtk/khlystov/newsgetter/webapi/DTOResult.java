
package ru.fmtk.khlystov.newsgetter.webapi;

import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class DTOResult implements Serializable {

    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("subsection")
    @Expose
    private String subsection;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("byline")
    @Expose
    private String byline;
    @SerializedName("abstract")
    @Expose
    private String _abstract;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("published_date")
    @Expose
    private String publishedDate;
    @SerializedName("multimedia")
    @Expose
    private List<DTOMultimedium> multimedia;
    private final static long serialVersionUID = -8046644522326767844L;

    public DTOResult(String section, String subsection, String title, String byline,
                     String _abstract, String url, String publishedDate,
                     List<DTOMultimedium> multimedia) {
        super();
        this.section = section;
        this.subsection = subsection;
        this.title = title;
        this.byline = byline;
        this._abstract = _abstract;
        this.url = url;
        this.publishedDate = publishedDate;
        this.multimedia = multimedia;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public String getSubsection() {
        return subsection;
    }

    public void setSubsection(String subsection) {
        this.subsection = subsection;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getByline() {
        return byline;
    }

    public void setByline(String byline) {
        this.byline = byline;
    }

    public String getAbstract() {
        return _abstract;
    }

    public void setAbstract(String _abstract) {
        this._abstract = _abstract;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public List<DTOMultimedium> getMultimedia() {
        return multimedia;
    }

    public void setMultimedia(List<DTOMultimedium> multimedia) {
        this.multimedia = multimedia;
    }

    @Override
    @NonNull
    public String toString() {
        String t = getTitle();
        if (t == null) {
            return "empty DTO Result";
        }
        return t;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getTitle());
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof DTOResult)) {
            return false;
        }
        DTOResult rhs = ((DTOResult) other);
        return Objects.equals(getTitle(), rhs.getTitle());
    }

}
