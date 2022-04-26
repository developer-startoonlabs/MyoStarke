package com.start.apps.pheezee.pojos;

public class PatientStatusData {
    private String phizioemail;
    private String patientid;
    private String status;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private String date;

    public PatientStatusData(String phizioemail, String patientid, String status) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.status = status;
    }

    public PatientStatusData(String phizioemail, String patientid, String status, String date) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.status = status;
        this.date = date;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
