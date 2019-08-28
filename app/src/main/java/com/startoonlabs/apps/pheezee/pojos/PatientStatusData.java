package com.startoonlabs.apps.pheezee.pojos;

public class PatientStatusData {
    private String phizioemail;
    private String patientid;
    private String status;

    public PatientStatusData(String phizioemail, String patientid, String status) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.status = status;
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
