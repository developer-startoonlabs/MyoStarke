package com.start.apps.pheezee.pojos;

public class FirmwareData {
    String data;

    public FirmwareData(String log) {
        this.data = log;
    }

    public String getLog() {
        return data;
    }

    public void setLog(String log) {
        this.data = log;
    }
}
