package com.start.apps.pheezee.pojos;



import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Overallresponse {

    @SerializedName("elbow")
    @Expose
    private Integer elbow;
    @SerializedName("knee")
    @Expose
    private Integer knee;
    @SerializedName("ankle")
    @Expose
    private Integer ankle;
    @SerializedName("hip")
    @Expose
    private Integer hip;
    @SerializedName("wrist")
    @Expose
    private Integer wrist;
    @SerializedName("shoulder")
    @Expose
    private Integer shoulder;
    @SerializedName("forearm")
    @Expose
    private Integer forearm;
    @SerializedName("spine")
    @Expose
    private Integer spine;

    @SerializedName("cervical")
    @Expose
    private Integer cervical;

    @SerializedName("thoracic")
    @Expose
    private Integer thoracic;

    @SerializedName("lumbar")
    @Expose
    private Integer lumbar;

    @SerializedName("abdomen")
    @Expose
    private Integer abdomen;

    @SerializedName("others")
    @Expose
    private Integer others;

    public Integer getElbow() {
        return elbow;
    }

    public void setElbow(Integer elbow) {
        this.elbow = elbow;
    }

    public Integer getKnee() {
        return knee;
    }

    public void setKnee(Integer knee) {
        this.knee = knee;
    }

    public Integer getAnkle() {
        return ankle;
    }

    public void setAnkle(Integer ankle) {
        this.ankle = ankle;
    }

    public Integer getHip() {
        return hip;
    }

    public void setHip(Integer hip) {
        this.hip = hip;
    }

    public Integer getWrist() {
        return wrist;
    }

    public void setWrist(Integer wrist) {
        this.wrist = wrist;
    }

    public Integer getShoulder() {
        return shoulder;
    }

    public void setShoulder(Integer shoulder) {
        this.shoulder = shoulder;
    }

    public Integer getForearm() {
        return forearm;
    }

    public void setForearm(Integer forearm) {
        this.forearm = forearm;
    }

    public Integer getSpine() {
        return spine;
    }

    public void setSpine(Integer spine) {
        this.spine = spine;
    }


    public Integer getCervical() {
        return cervical;
    }

    public void setCervical(Integer cervical) {
        this.cervical = cervical;
    }

    public Integer getThoracic() {
        return thoracic;
    }

    public void setThoracic(Integer thoracic) {
        this.thoracic = thoracic;
    }

    public Integer getLumbar() {
        return lumbar;
    }

    public void setLumbar(Integer lumbar) {
        this.lumbar = lumbar;
    }


    public Integer getAbdomen() {
        return abdomen;
    }

    public void setAbdomen(Integer abdomen) {
        this.abdomen = abdomen;
    }

    public Integer getOthers() {
        return others;
    }

    public void setOthers(Integer others) {
        this.others = others;
    }

}