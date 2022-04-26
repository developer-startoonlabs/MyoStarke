package com.start.apps.pheezee.pojos;

public class DeviceDeactivationStatusResponse {

    private String uid;
    protected boolean status;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
