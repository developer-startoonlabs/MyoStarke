package com.start.apps.pheezee.pojos;

public class PatientDetailsData {
    String phizioemail;
    String patientid;
    String patientname;
    String numofsessions;
    String dateofjoin;
    String patientage;
    String patientgender;
    String patientcasedes;
    String status;
    String patientphone;
    String patientprofilepicurl;

    public PatientDetailsData(String phizioemail, String patientid, String patientname, String numofsessions,
                          String dateofjoin, String patientage, String patientgender, String patientcasedes,
                          String status, String patientphone, String patientprofilepicurl) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.patientname = patientname;
        this.numofsessions = numofsessions;
        this.dateofjoin = dateofjoin;
        this.patientage = patientage;
        this.patientgender = patientgender;
        this.patientcasedes = patientcasedes;
        this.status = status;
        this.patientphone = patientphone;
        this.patientprofilepicurl = patientprofilepicurl;
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

    public String getPatientage() {
        return patientage;
    }

    public void setPatientage(String patientage) {
        this.patientage = patientage;
    }

    public String getPatientgender() {
        return patientgender;
    }

    public void setPatientgender(String patientgender) {
        this.patientgender = patientgender;
    }

    public String getPatientcasedes() {
        return patientcasedes;
    }

    public void setPatientcasedes(String patientcasedes) {
        this.patientcasedes = patientcasedes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
