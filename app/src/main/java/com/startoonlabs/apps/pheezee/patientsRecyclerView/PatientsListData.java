package com.startoonlabs.apps.pheezee.patientsRecyclerView;

public class PatientsListData {
    private String patientName;
    private String patientId;
    private String patientUrl;

    public String getPatientUrl() {
        return patientUrl;
    }

    public void setPatientUrl(String patientUrl) {
        this.patientUrl = patientUrl;
    }

    public PatientsListData(String patientName, String patientId, String patientUrl){
        this.patientName = patientName;
        this.patientId = patientId;
        this.patientUrl = patientUrl;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }
}
