package com.start.apps.pheezee.classes;

public class EmgPeak {
    private int initValue,max_emg_value, final_value;
    private boolean peak_done;

    public EmgPeak(int initValue, int max_emg_value, int final_value, boolean peak_done) {
        this.initValue = initValue;
        this.max_emg_value = max_emg_value;
        this.final_value = final_value;
        this.peak_done = peak_done;
    }

    public int getInitValue() {
        return initValue;
    }

    public void setInitValue(int initValue) {
        this.initValue = initValue;
    }

    public int getMax_emg_value() {
        return max_emg_value;
    }

    public void setMax_emg_value(int max_emg_value) {
        this.max_emg_value = max_emg_value;
    }

    public int getFinal_value() {
        return final_value;
    }

    public void setFinal_value(int final_value) {
        this.final_value = final_value;
    }

    public boolean isPeak_done() {
        return peak_done;
    }

    public void setPeak_done(boolean peak_done) {
        this.peak_done = peak_done;
    }
}
