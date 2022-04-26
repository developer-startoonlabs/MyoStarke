package com.start.apps.pheezee.pojos;

public class MmtData {

    String phizioemail, patientid, heldon, mmtgrade, bodyorientation, sessiontype, id, commentsession;

    public MmtData(String phizioemail, String patientid, String heldon, String mmtgrade, String bodyorientation, String sessiontype, String id, String commentsession) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.heldon = heldon;
        this.mmtgrade = mmtgrade;
        this.bodyorientation = bodyorientation;
        this.sessiontype = sessiontype;
        this.commentsession = commentsession;
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

    public String getMmtgrade() {
        return mmtgrade;
    }

    public void setMmtgrade(String mmtgrade) {
        this.mmtgrade = mmtgrade;
    }

    public String getBodyorientation() {
        return bodyorientation;
    }

    public void setBodyorientation(String bodyorientation) {
        this.bodyorientation = bodyorientation;
    }

    public String getSessiontype() {
        return sessiontype;
    }

    public void setSessiontype(String sessiontype) {
        this.sessiontype = sessiontype;
    }
}
