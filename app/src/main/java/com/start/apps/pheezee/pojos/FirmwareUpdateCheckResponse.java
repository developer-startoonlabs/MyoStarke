package com.start.apps.pheezee.pojos;

public class FirmwareUpdateCheckResponse {
    boolean firmware_available;
    String latest_firmware_link;
    String firmware_version;

    public String getFirmware_version() {
        return firmware_version;
    }

    public void setFirmware_version(String firmware_version) {
        this.firmware_version = firmware_version;
    }

    public boolean isFirmware_available() {
        return firmware_available;
    }

    public void setFirmware_available(boolean firmware_available) {
        this.firmware_available = firmware_available;
    }

    public String getLatest_firmware_link() {
        return latest_firmware_link;
    }

    public void setLatest_firmware_link(String latest_firmware_link) {
        this.latest_firmware_link = latest_firmware_link;
    }
}
