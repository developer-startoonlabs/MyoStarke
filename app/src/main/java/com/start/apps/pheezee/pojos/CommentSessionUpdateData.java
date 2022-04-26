package com.start.apps.pheezee.pojos;

public class CommentSessionUpdateData {
    String phizioemail, patientid, heldon, painscale, muscletone, exercisename, commentsession, symptoms;

    public CommentSessionUpdateData(String phizioemail, String patientid, String heldon,
                                    String painscale, String muscletone, String exercisename, String commentsession, String symptoms) {
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.heldon = heldon;
        this.painscale = painscale;
        this.muscletone = muscletone;
        this.exercisename = exercisename;
        this.commentsession = commentsession;
        this.symptoms = symptoms;
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

    public String getPainscale() {
        return painscale;
    }

    public void setPainscale(String painscale) {
        this.painscale = painscale;
    }

    public String getMuscletone() {
        return muscletone;
    }

    public void setMuscletone(String muscletone) {
        this.muscletone = muscletone;
    }

    public String getExercisename() {
        return exercisename;
    }

    public void setExercisename(String exercisename) {
        this.exercisename = exercisename;
    }

    public String getCommentsession() {
        return commentsession;
    }

    public void setCommentsession(String commentsession) {
        this.commentsession = commentsession;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
}
