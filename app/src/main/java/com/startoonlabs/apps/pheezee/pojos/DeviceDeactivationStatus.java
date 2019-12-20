package com.startoonlabs.apps.pheezee.pojos;

import com.startoonlabs.apps.pheezee.services.DeviceDetailsService;

public class DeviceDeactivationStatus {
    private String uid;

    public DeviceDeactivationStatus(String uid) {
        this.uid = uid;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }
}
