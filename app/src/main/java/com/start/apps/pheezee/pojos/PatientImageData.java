package com.start.apps.pheezee.pojos;

public class PatientImageData {
    String patientid;
    String phizioemail;
    String image;

    public PatientImageData(String patientid, String phizioemail, String image) {
        this.patientid = patientid;
        this.phizioemail = phizioemail;
        this.image = image;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
