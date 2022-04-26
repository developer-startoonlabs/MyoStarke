package com.start.apps.pheezee.pojos;

public class DeletePhiziouserData {

    private String phizioemail;
    private String feedback;
    private String todelete;
    private String needdata;


    public DeletePhiziouserData(String phizioemail, String feedback, String todelete, String needdata) {
        this.phizioemail = phizioemail;
        this.feedback = feedback;
        this.todelete = todelete;
        this.needdata = needdata;


    }
    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }
    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String phizioemail) {
        this.feedback = feedback;
    }

    public String getTodelete() {
        return todelete;
    }

    public void setTodelete(String todelete) {
        this.todelete = todelete;
    }

    public String getNeeddata() {
        return needdata;
    }

    public void setNeeddata(String needdata) {
        this.needdata = needdata;
    }
}
