package com.start.apps.pheezee.pojos;

public class LoginData {
    private String phizioemail;
    private String phiziopassword;

    public LoginData(String phizioemail, String phiziopassword) {
        this.phizioemail = phizioemail;
        this.phiziopassword = phiziopassword;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getPhiziopassword() {
        return phiziopassword;
    }

    public void setPhiziopassword(String phiziopassword) {
        this.phiziopassword = phiziopassword;
    }
}
