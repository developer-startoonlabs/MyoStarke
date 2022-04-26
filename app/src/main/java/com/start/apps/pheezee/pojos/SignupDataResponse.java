package com.start.apps.pheezee.pojos;

import com.start.apps.pheezee.room.Entity.PhizioPatients;

import java.util.List;

public class SignupDataResponse {
    String phizioname, phizioemail, phiziopassword, phiziophone, phizioprofilepicurl, packageid;
    List<PhizioPatients> patients;
    boolean inserted;
    int packagetype;

    public SignupDataResponse(String phizioname, String phizioemail, String phiziopassword, String phone,
                      String phizioprofilepicurl, List<PhizioPatients> patients, String packageid, boolean inserted, int packagetype) {
        this.phizioname = phizioname;
        this.phizioemail = phizioemail;
        this.phiziopassword = phiziopassword;
        this.phiziophone = phone;
        this.phizioprofilepicurl = phizioprofilepicurl;
        this.patients = patients;
        this.packageid = packageid;
        this.inserted = inserted;
        this.packagetype = packagetype;
    }

    public String getPhizioname() {
        return phizioname;
    }

    public void setPhizioname(String phizioname) {
        this.phizioname = phizioname;
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

    public String getPhone() {
        return phiziophone;
    }

    public void setPhone(String phone) {
        this.phiziophone = phone;
    }

    public String getPhizioprofilepicurl() {
        return phizioprofilepicurl;
    }

    public void setPhizioprofilepicurl(String phizioprofilepicurl) {
        this.phizioprofilepicurl = phizioprofilepicurl;
    }

    public List<PhizioPatients> getPatients() {
        return patients;
    }

    public void setPatients(List<PhizioPatients> patients) {
        this.patients = patients;
    }

    public String getPhiziophone() {
        return phiziophone;
    }

    public void setPhiziophone(String phiziophone) {
        this.phiziophone = phiziophone;
    }

    public String getPackageid() {
        return packageid;
    }

    public void setPackageid(String packageid) {
        this.packageid = packageid;
    }

    public boolean isInserted() {
        return inserted;
    }

    public void setInserted(boolean inserted) {
        this.inserted = inserted;
    }

    public int getPackagetype() {
        return packagetype;
    }

    public void setPackagetype(int packagetype) {
        this.packagetype = packagetype;
    }
}
