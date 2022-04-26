package com.start.apps.pheezee.pojos;

public class HealthData {
    String time_stamp,uid;
    int u_lsm_ini,l_lsm_ini,gain_amplifier,atiny_init_status,adc_status,u_lsm_regi,l_lsm_regi,device_state,usb_state,gain_amplifier_write_status,
            ble_status,charger_staus,pow_btn_status,main_ldo_status,over_current_protection_status,u_lesm_read,l_lsm_read,atiny_read_status, u_mag_ini,
            l_mag_ini, u_mag_read, l_mag_read;

    public HealthData(String time_stamp, String uid, int u_lsm_ini, int l_lsm_ini, int gain_amplifier, int atiny_init_status, int adc_status,
                      int u_lsm_regi, int l_lsm_regi, int device_state, int usb_state, int gain_amplifier_write_status, int ble_status,
                      int charger_staus, int pow_btn_status, int main_ldo_status, int over_current_protection_status, int u_lesm_read,
                      int l_lsm_read, int atiny_read_status, int u_mag_ini, int l_mag_ini, int u_mag_read, int l_mag_read) {
        this.time_stamp = time_stamp;
        this.u_lsm_ini = u_lsm_ini;
        this.l_lsm_ini = l_lsm_ini;
        this.uid = uid;
        this.gain_amplifier = gain_amplifier;
        this.atiny_init_status = atiny_init_status;
        this.adc_status = adc_status;
        this.u_lsm_regi = u_lsm_regi;
        this.l_lsm_regi = l_lsm_regi;
        this.device_state = device_state;
        this.usb_state = usb_state;
        this.gain_amplifier_write_status = gain_amplifier_write_status;
        this.ble_status = ble_status;
        this.charger_staus = charger_staus;
        this.pow_btn_status = pow_btn_status;
        this.main_ldo_status = main_ldo_status;
        this.over_current_protection_status = over_current_protection_status;
        this.u_lesm_read = u_lesm_read;
        this.l_lsm_read = l_lsm_read;
        this.atiny_read_status = atiny_read_status;
        this.u_mag_ini = u_mag_ini;
        this.l_mag_ini = l_mag_ini;
        this.u_mag_read = u_mag_read;
        this.l_mag_read = l_mag_read;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public int getU_lsm_ini() {
        return u_lsm_ini;
    }

    public void setU_lsm_ini(int u_lsm_ini) {
        this.u_lsm_ini = u_lsm_ini;
    }

    public int getL_lsm_ini() {
        return l_lsm_ini;
    }

    public void setL_lsm_ini(int l_lsm_ini) {
        this.l_lsm_ini = l_lsm_ini;
    }

    public int getGain_amplifier() {
        return gain_amplifier;
    }

    public void setGain_amplifier(int gain_amplifier) {
        this.gain_amplifier = gain_amplifier;
    }

    public int getAtiny_init_status() {
        return atiny_init_status;
    }

    public void setAtiny_init_status(int atiny_init_status) {
        this.atiny_init_status = atiny_init_status;
    }

    public int getAdc_status() {
        return adc_status;
    }

    public void setAdc_status(int adc_status) {
        this.adc_status = adc_status;
    }

    public int getU_lsm_regi() {
        return u_lsm_regi;
    }

    public void setU_lsm_regi(int u_lsm_regi) {
        this.u_lsm_regi = u_lsm_regi;
    }

    public int getL_lsm_regi() {
        return l_lsm_regi;
    }

    public void setL_lsm_regi(int l_lsm_regi) {
        this.l_lsm_regi = l_lsm_regi;
    }

    public int getDevice_state() {
        return device_state;
    }

    public void setDevice_state(int device_state) {
        this.device_state = device_state;
    }

    public int getUsb_state() {
        return usb_state;
    }

    public void setUsb_state(int usb_state) {
        this.usb_state = usb_state;
    }

    public int getGain_amplifier_write_status() {
        return gain_amplifier_write_status;
    }

    public void setGain_amplifier_write_status(int gain_amplifier_write_status) {
        this.gain_amplifier_write_status = gain_amplifier_write_status;
    }

    public int getBle_status() {
        return ble_status;
    }

    public void setBle_status(int ble_status) {
        this.ble_status = ble_status;
    }

    public int getCharger_staus() {
        return charger_staus;
    }

    public void setCharger_staus(int charger_staus) {
        this.charger_staus = charger_staus;
    }

    public int getPow_btn_status() {
        return pow_btn_status;
    }

    public void setPow_btn_status(int pow_btn_status) {
        this.pow_btn_status = pow_btn_status;
    }

    public int getMain_ldo_status() {
        return main_ldo_status;
    }

    public void setMain_ldo_status(int main_ldo_status) {
        this.main_ldo_status = main_ldo_status;
    }

    public int getOver_current_protection_status() {
        return over_current_protection_status;
    }

    public void setOver_current_protection_status(int over_current_protection_status) {
        this.over_current_protection_status = over_current_protection_status;
    }

    public int getU_lesm_read() {
        return u_lesm_read;
    }

    public void setU_lesm_read(int u_lesm_read) {
        this.u_lesm_read = u_lesm_read;
    }

    public int getL_lsm_read() {
        return l_lsm_read;
    }

    public void setL_lsm_read(int l_lsm_read) {
        this.l_lsm_read = l_lsm_read;
    }

    public int getAtiny_read_status() {
        return atiny_read_status;
    }

    public void setAtiny_read_status(int atiny_read_status) {
        this.atiny_read_status = atiny_read_status;
    }
}
