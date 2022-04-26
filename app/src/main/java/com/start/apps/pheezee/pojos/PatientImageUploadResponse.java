package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PatientImageUploadResponse {
    @SerializedName("patientid")
    @Expose
    private String patientid;
    @SerializedName("url")
    @Expose
    private String url;
    @SerializedName("isvalid")
    @Expose
    private Boolean isvalid;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Boolean getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(Boolean isvalid) {
        this.isvalid = isvalid;
    }
}
