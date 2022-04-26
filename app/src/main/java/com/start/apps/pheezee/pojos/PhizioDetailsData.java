package com.start.apps.pheezee.pojos;

public class PhizioDetailsData {
    String phizioname, phiziophone, phizioemail, clinicname, phiziodob, experience, specialization, degree, gender, address;

    public PhizioDetailsData(String phizioname, String phiziophone, String phizioemail,
                             String clinicname, String phiziodob, String experience, String specialization, String degree,
                             String gender, String address) {
        this.phizioname = phizioname;
        this.phiziophone = phiziophone;
        this.phizioemail = phizioemail;
        this.clinicname = clinicname;
        this.phiziodob = phiziodob;
        this.experience = experience;
        this.specialization = specialization;
        this.degree = degree;
        this.gender = gender;
        this.address = address;
    }


    public String getPhizioname() {
        return phizioname;
    }

    public void setPhizioname(String phizioname) {
        this.phizioname = phizioname;
    }

    public String getPhiziophone() {
        return phiziophone;
    }

    public void setPhiziophone(String phiziophone) {
        this.phiziophone = phiziophone;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getClinicname() {
        return clinicname;
    }

    public void setClinicname(String clinicname) {
        this.clinicname = clinicname;
    }

    public String getPhiziodob() {
        return phiziodob;
    }

    public void setPhiziodob(String phiziodob) {
        this.phiziodob = phiziodob;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getDegree() {
        return degree;
    }

    public void setDegree(String degree) {
        this.degree = degree;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
