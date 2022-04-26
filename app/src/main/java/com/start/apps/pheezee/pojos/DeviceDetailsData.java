package com.start.apps.pheezee.pojos;

public class DeviceDetailsData {
    String uid, mac, firmware_version, hardware_version, serial_version, atiny_version;

    public DeviceDetailsData(String uid, String mac, String firmware_version, String hardware_version, String serial_version, String atiny_version) {
        this.uid = uid;
        this.mac = mac;
        this.firmware_version = firmware_version;
        this.hardware_version = hardware_version;
        this.serial_version = serial_version;
        this.atiny_version = atiny_version;
    }
}
