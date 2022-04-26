package com.start.apps.pheezee.classes;

public class DeviceListClass {
    String deviceName, deviceMacAddress, deviceBondState, deviceRssi;
    long timeStampNano;

    public long getTimeStampNano() {
        return timeStampNano;
    }

    public void setTimeStampNano(long timeStampNano) {
        this.timeStampNano = timeStampNano;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getDeviceMacAddress() {
        return deviceMacAddress;
    }

    public void setDeviceMacAddress(String deviceMacAddress) {
        this.deviceMacAddress = deviceMacAddress;
    }

    public String getDeviceBondState() {
        return deviceBondState;
    }

    public void setDeviceBondState(String deviceBondState) {
        this.deviceBondState = deviceBondState;
    }

    public String getDeviceRssi() {
        return deviceRssi;
    }

    public void setDeviceRssi(String deviceRssi) {
        this.deviceRssi = deviceRssi;
    }
}
