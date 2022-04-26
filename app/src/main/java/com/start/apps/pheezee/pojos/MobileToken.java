package com.start.apps.pheezee.pojos;

public class MobileToken {
    String phizioemail, token;

    public MobileToken(String phizioemail, String token) {
        this.phizioemail = phizioemail;
        this.token = token;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
