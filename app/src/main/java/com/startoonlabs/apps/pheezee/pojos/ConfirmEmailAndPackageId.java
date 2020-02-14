package com.startoonlabs.apps.pheezee.pojos;

public class ConfirmEmailAndPackageId {
    String phizioemail, otp, packageid;

    public ConfirmEmailAndPackageId(String phizioemail, String otp, String packageid) {
        this.phizioemail = phizioemail;
        this.otp = otp;
        this.packageid = packageid;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getPackageid() {
        return packageid;
    }

    public void setPackageid(String packageid) {
        this.packageid = packageid;
    }
}
