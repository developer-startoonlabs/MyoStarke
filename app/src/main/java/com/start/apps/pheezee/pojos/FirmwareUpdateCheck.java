package com.start.apps.pheezee.pojos;

public class FirmwareUpdateCheck {
    String firmware_version;

    public FirmwareUpdateCheck(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }
}
