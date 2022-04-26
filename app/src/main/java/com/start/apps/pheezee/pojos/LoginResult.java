
package com.start.apps.pheezee.pojos;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.start.apps.pheezee.room.Entity.PhizioPatients;

public class LoginResult {

    @SerializedName("isvalid")
    @Expose
    private Boolean isvalid;
    @SerializedName("phizioname")
    @Expose
    private String phizioname;
    @SerializedName("phizioemail")
    @Expose
    private String phizioemail;
    @SerializedName("phiziophone")
    @Expose
    private String phiziophone;
    @SerializedName("phizioprofilepicurl")
    @Expose
    private String phizioprofilepicurl;
    @SerializedName("phiziopatients")
    @Expose
    private List<PhizioPatients> phiziopatients = null;
    @SerializedName("__v")
    @Expose
    private Integer v;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("clinicname")
    @Expose
    private String clinicname;
    @SerializedName("cliniclogo")
    @Expose
    private String cliniclogo;
    @SerializedName("degree")
    @Expose
    private String degree;
    @SerializedName("experience")
    @Expose
    private String experience;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("phiziodob")
    @Expose
    private String phiziodob;
    @SerializedName("specialization")
    @Expose
    private String specialization;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("packagetype")
    @Expose
    private int packagetype;
    @SerializedName("patientlimit")
    @Expose
    private int patientlimit;



    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getPackagetype() {
        return packagetype;
    }

    public int getPatientlimit() { return patientlimit;    }

    public void setPackagetype(int packagetype) {
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

    public String getPhiziophone() {
        return phiziophone;
    }

    public void setPhiziophone(String phiziophone) {
        this.phiziophone = phiziophone;
    }

    public String getPhizioprofilepicurl() {
        return phizioprofilepicurl;
    }

    public void setPhizioprofilepicurl(String phizioprofilepicurl) {
        this.phizioprofilepicurl = phizioprofilepicurl;
    }

    public List<PhizioPatients> getPhiziopatients() {
        return phiziopatients;
    }

    public void setPhiziopatients(List<PhizioPatients> phiziopatients) {
        this.phiziopatients = phiziopatients;
    }

    public Integer getV() {
        return v;
    }

    public void setV(Integer v) {
        this.v = v;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getClinicname() {
        return clinicname;
    }

    public void setClinicname(String clinicname) {
        this.clinicname = clinicname;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getPhiziodob() {
        return phiziodob;
    }

    public void setPhiziodob(String phiziodob) {
        this.phiziodob = phiziodob;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }
    public Boolean getIsvalid() {
        return isvalid;
    }

    public void setIsvalid(Boolean isvalid) {
        this.isvalid = isvalid;
    }

    public String getCliniclogo() {
        return cliniclogo;
    }

    public void setCliniclogo(String cliniclogo) {
        this.cliniclogo = cliniclogo;
    }
}
