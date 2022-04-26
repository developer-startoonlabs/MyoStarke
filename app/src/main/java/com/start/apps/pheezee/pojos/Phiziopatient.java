
package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Phiziopatient {

    @SerializedName("patientid")
    @Expose
    private String patientid;
    @SerializedName("patientname")
    @Expose
    private String patientname;
    @SerializedName("numofsessions")
    @Expose
    private String numofsessions;
    @SerializedName("dateofjoin")
    @Expose
    private String dateofjoin;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("patientage")
    @Expose
    private String patientage;
    @SerializedName("patientcasedes")
    @Expose
    private String patientcasedes;
    @SerializedName("patientgender")
    @Expose
    private String patientgender;
    @SerializedName("patientphone")
    @Expose
    private String patientphone;
    @SerializedName("patientprofilepicurl")
    @Expose
    private String patientprofilepicurl;

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getPatientname() {
        return patientname;
    }

    public void setPatientname(String patientname) {
        this.patientname = patientname;
    }

    public String getNumofsessions() {
        return numofsessions;
    }

    public void setNumofsessions(String numofsessions) {
        this.numofsessions = numofsessions;
    }

    public String getDateofjoin() {
        return dateofjoin;
    }

    public void setDateofjoin(String dateofjoin) {
        this.dateofjoin = dateofjoin;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatientage() {
        return patientage;
    }

    public void setPatientage(String patientage) {
        this.patientage = patientage;
    }

    public String getPatientcasedes() {
        return patientcasedes;
    }

    public void setPatientcasedes(String patientcasedes) {
        this.patientcasedes = patientcasedes;
    }

    public String getPatientgender() {
        return patientgender;
    }

    public void setPatientgender(String patientgender) {
        this.patientgender = patientgender;
    }

    public String getPatientphone() {
        return patientphone;
    }

    public void setPatientphone(String patientphone) {
        this.patientphone = patientphone;
    }

    public String getPatientprofilepicurl() {
        return patientprofilepicurl;
    }

    public void setPatientprofilepicurl(String patientprofilepicurl) {
        this.patientprofilepicurl = patientprofilepicurl;
    }

}
