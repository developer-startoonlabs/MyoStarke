package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SessionResult {

    @SerializedName("_id")
    @Expose
    private String id;
    @SerializedName("phizioemail")
    @Expose
    private String phizioemail;
    @SerializedName("patientid")
    @Expose
    private String patientid;
    @SerializedName("sessiondetails")
    @Expose
    private List<Sessiondetail> sessiondetails = null;
    @SerializedName("overalldetails")
    @Expose
    private List<Overalldetail> overalldetails = null;
    @SerializedName("__v")
    @Expose
    private Integer v;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public List<Sessiondetail> getSessiondetails() {
        return sessiondetails;
    }

    public void setSessiondetails(List<Sessiondetail> sessiondetails) {
        this.sessiondetails = sessiondetails;
    }

    public List<Overalldetail> getOveralldetails() {
        return overalldetails;
    }

    public void setOveralldetails(List<Overalldetail> overalldetails) {
        this.overalldetails = overalldetails;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

}
