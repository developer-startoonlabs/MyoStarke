package com.start.apps.pheezee.pojos;

public class GetReportData {
    String phizioemail, patientid;

    public GetReportData(String phizioemail, String patientid) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
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
}
