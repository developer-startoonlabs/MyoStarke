package com.start.apps.pheezee.pojos;

public class DeleteSessionData {
    String phizioemail, patientid, heldon, id;

    public DeleteSessionData(String phizioemail, String patientid, String heldon, String id) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.heldon = heldon;
        this.id = id;
    }

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

    public String getHeldon() {
        return heldon;
    }

    public void setHeldon(String heldon) {
        this.heldon = heldon;
    }
}
