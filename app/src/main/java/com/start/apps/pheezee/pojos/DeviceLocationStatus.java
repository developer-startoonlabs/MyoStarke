package com.start.apps.pheezee.pojos;

public class DeviceLocationStatus {
    String uid, time_stamp;
    Double latitude, longitude;

    public DeviceLocationStatus(String uid, String time_stamp, Double latitude, Double longitude) {
        this.uid = uid;
        this.time_stamp = time_stamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
