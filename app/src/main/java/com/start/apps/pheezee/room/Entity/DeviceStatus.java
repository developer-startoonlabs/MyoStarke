package com.start.apps.pheezee.room.Entity;


import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "DeviceStatus")
public class DeviceStatus {
    @NonNull
    @PrimaryKey
    private String uid;
    @NonNull
    private int status;

    public DeviceStatus( String uid, int status) {
        this.uid = uid;
        this.status = status;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
