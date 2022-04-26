package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;


public class PhizioSessionReportData {

    @SerializedName("session")
    @Expose
    private Integer session;
    @SerializedName("report")
    @Expose
    private Integer report;

    public Integer getSession() {
        return session;
    }

    public void setSession(Integer session) {
        this.session = session;
    }

    public Integer getReport() {
        return report;
    }

    public void setReport(Integer report) {
        this.report = report;
    }

}