package com.start.apps.pheezee.pojos;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SessionList {

    @SerializedName("heldon")
    @Expose
    private String heldon;
    @SerializedName("maxemg")
    @Expose
    private String maxemg;
    @SerializedName("maxangle")
    @Expose
    private String maxangle;
    @SerializedName("minangle")
    @Expose
    private String minangle;
    @SerializedName("bodypart")
    @Expose
    private String bodypart;
    @SerializedName("anglecorrected")
    @Expose
    private String anglecorrected;
    @SerializedName("holdtime")
    @Expose
    private String holdtime;
    @SerializedName("activetime")
    @Expose
    private String activetime;
    @SerializedName("mmtgrade")
    @Expose
    private String mmtgrade;
    @SerializedName("bodyorientation")
    @Expose
    private String bodyorientation;
    @SerializedName("sessiontype")
    @Expose
    private String sessiontype;
    @SerializedName("maxangleselected")
    @Expose
    private String maxangleselected;
    @SerializedName("minangleselected")
    @Expose
    private String minangleselected;
    @SerializedName("maxemgselected")
    @Expose
    private String maxemgselected;
    @SerializedName("sessioncolor")
    @Expose
    private Integer sessioncolor;
    @SerializedName("numofreps")
    @Expose
    private String numofreps;
    @SerializedName("sessiontime")
    @Expose
    private String sessiontime;
    @SerializedName("painscale")
    @Expose
    private String painscale;
    @SerializedName("muscletone")
    @Expose
    private String muscletone;
    @SerializedName("symptoms")
    @Expose
    private String symptoms;
    @SerializedName("exercisename")
    @Expose
    private String exercisename;
    @SerializedName("commentsession")
    @Expose
    private String commentsession;
    @SerializedName("musclename")
    @Expose
    private String musclename;
    @SerializedName("repsselected")
    @Expose
    private Integer repsselected;
    @SerializedName("orientation")
    @Expose
    private String orientation;

    public String getHeldon() {
        return heldon;
    }

    public void setHeldon(String heldon) {
        this.heldon = heldon;
    }

    public String getMaxemg() {
        return maxemg;
    }

    public void setMaxemg(String maxemg) {
        this.maxemg = maxemg;
    }

    public String getMaxangle() {
        return maxangle;
    }

    public void setMaxangle(String maxangle) {
        this.maxangle = maxangle;
    }

    public String getMinangle() {
        return minangle;
    }

    public void setMinangle(String minangle) {
        this.minangle = minangle;
    }

    public String getBodypart() {
        return bodypart;
    }

    public void setBodypart(String bodypart) {
        this.bodypart = bodypart;
    }

    public String getAnglecorrected() {
        return anglecorrected;
    }

    public void setAnglecorrected(String anglecorrected) {
        this.anglecorrected = anglecorrected;
    }

    public String getHoldtime() {
        return holdtime;
    }

    public void setHoldtime(String holdtime) {
        this.holdtime = holdtime;
    }

    public String getActivetime() {
        return activetime;
    }

    public void setActivetime(String activetime) {
        this.activetime = activetime;
    }

    public String getMmtgrade() {
        return mmtgrade;
    }

    public void setMmtgrade(String mmtgrade) {
        this.mmtgrade = mmtgrade;
    }

    public String getBodyorientation() {
        return bodyorientation;
    }

    public void setBodyorientation(String bodyorientation) {
        this.bodyorientation = bodyorientation;
    }

    public String getSessiontype() {
        return sessiontype;
    }

    public void setSessiontype(String sessiontype) {
        this.sessiontype = sessiontype;
    }

    public String getMaxangleselected() {
        return maxangleselected;
    }

    public void setMaxangleselected(String maxangleselected) {
        this.maxangleselected = maxangleselected;
    }

    public String getMinangleselected() {
        return minangleselected;
    }

    public void setMinangleselected(String minangleselected) {
        this.minangleselected = minangleselected;
    }

    public String getMaxemgselected() {
        return maxemgselected;
    }

    public void setMaxemgselected(String maxemgselected) {
        this.maxemgselected = maxemgselected;
    }

    public Integer getSessioncolor() {
        return sessioncolor;
    }

    public void setSessioncolor(Integer sessioncolor) {
        this.sessioncolor = sessioncolor;
    }

    public String getNumofreps() {
        return numofreps;
    }

    public void setNumofreps(String numofreps) {
        this.numofreps = numofreps;
    }

    public String getSessiontime() {
        return sessiontime;
    }

    public void setSessiontime(String sessiontime) {
        this.sessiontime = sessiontime;
    }

    public String getPainscale() {
        return painscale;
    }

    public void setPainscale(String painscale) {
        this.painscale = painscale;
    }

    public String getMuscletone() {
        return muscletone;
    }

    public void setMuscletone(String muscletone) {
        this.muscletone = muscletone;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getExercisename() {
        return exercisename;
    }

    public void setExercisename(String exercisename) {
        this.exercisename = exercisename;
    }

    public String getCommentsession() {
        return commentsession;
    }

    public void setCommentsession(String commentsession) {
        this.commentsession = commentsession;
    }

    public String getMusclename() {
        return musclename;
    }

    public void setMusclename(String musclename) {
        this.musclename = musclename;
    }

    public Integer getRepsselected() {
        return repsselected;
    }

    public void setRepsselected(Integer repsselected) {
        this.repsselected = repsselected;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
    }

}
