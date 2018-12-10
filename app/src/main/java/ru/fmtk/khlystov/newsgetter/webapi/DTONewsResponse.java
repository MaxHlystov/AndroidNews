
package ru.fmtk.khlystov.newsgetter.webapi;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DTONewsResponse implements Serializable {

    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("section")
    @Expose
    private String section;
    @SerializedName("results")
    @Expose
    private List<DTOResult> results;
    private final static long serialVersionUID = -8645667809215850272L;

    public DTONewsResponse(String status, String section, List<DTOResult> results) {
        super();
        this.status = status;
        this.section = section;
        this.results = results;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSection() {
        return section;
    }

    public void setSection(String section) {
        this.section = section;
    }

    public List<DTOResult> getResults() {
        return results;
    }

    public void setResults(List<DTOResult> results) {
        this.results = results;
    }

}
