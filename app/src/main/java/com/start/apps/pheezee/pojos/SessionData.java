package com.start.apps.pheezee.pojos;

import org.json.JSONArray;

public class SessionData {

    String heldon,maxangle,minangle,anglecorrected,maxemg,holdtime,bodypart,sessiontime,numofreps,numofsessions,
            phizioemail,patientid, painscale, muscletone, exercisename, commentsession, symptoms, activetime,
            orientation,mmtgrade, bodyorientation, sessiontype ,repsselected, musclename, maxangleselected,
            minangleselected, maxemgselected, sessioncolor, id,holdangle;
    JSONArray emgdata, romdata,activity_list;


    public SessionData(String heldon, String maxangle, String minangle, String anglecorrected, String maxemg, String holdtime,String holdangle, JSONArray activity_list,
                       String bodypart, String sessiontime, String numofreps, String numofsessions, String phizioemail, String patientid,
                       String painscale, String muscletone, String exercisename, String commentsession, String symptoms, String activetime,
                       String orientation, String mmtgrade, String bodyorientation, String sessiontype, String repsselected, String musclename,
                       String maxangleselected, String minangleselected, String maxemgselected, String sessioncolor, JSONArray emgdata,
                       JSONArray romdata, String id) {
        this.heldon = heldon;
        this.maxangle = maxangle;
        this.minangle = minangle;
        this.anglecorrected = anglecorrected;
        this.maxemg = maxemg;
        this.holdtime = holdtime;
        this.holdangle = holdangle;
        this.bodypart = bodypart;
        this.sessiontime = sessiontime;
        this.numofreps = numofreps;
        this.numofsessions = numofsessions;
        this.phizioemail = phizioemail;
        this.patientid = patientid;
        this.painscale = painscale;
        this.muscletone = muscletone;
        this.exercisename = exercisename;
        this.commentsession = commentsession;
        this.symptoms = symptoms;
        this.activetime = activetime;
        this.orientation = orientation;
        this.mmtgrade = mmtgrade;
        this.bodyorientation = bodyorientation;
        this.sessiontype = sessiontype;
        this.repsselected = repsselected;
        this.musclename = musclename;
        this.maxangleselected = maxangleselected;
        this.minangleselected = minangleselected;
        this.maxemgselected = maxemgselected;
        this.sessioncolor = sessioncolor;
        this.emgdata = emgdata;
        this.romdata = romdata;
        this.id = id;
        this.activity_list = activity_list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHeldon() {
        return heldon;
    }

    public void setHeldon(String heldon) {
        this.heldon = heldon;
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

    public String getAnglecorrected() {
        return anglecorrected;
    }

    public void setAnglecorrected(String anglecorrected) {
        this.anglecorrected = anglecorrected;
    }

    public String getMaxemg() {
        return maxemg;
    }

    public void setMaxemg(String maxemg) {
        this.maxemg = maxemg;
    }

    public String getHoldtime() {
        return holdtime;
    }

    public void setHoldtime(String holdtime) {
        this.holdtime = holdtime;
    }

    public String getBodypart() {
        return bodypart;
    }

    public void setBodypart(String bodypart) {
        this.bodypart = bodypart;
    }

    public String getSessiontime() {
        return sessiontime;
    }

    public void setSessiontime(String sessiontime) {
        this.sessiontime = sessiontime;
    }

    public String getNumofreps() {
        return numofreps;
    }

    public void setNumofreps(String numofreps) {
        this.numofreps = numofreps;
    }

    public String getNumofsessions() {
        return numofsessions;
    }

    public void setNumofsessions(String numofsessions) {
        this.numofsessions = numofsessions;
    }

    public String getPhizioemail() {
        return phizioemail;
    }

    public void setPhizioemail(String phizioemail) {
        this.phizioemail = phizioemail;
    }

    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(String patientid) {
        this.patientid = patientid;
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

    public String getSymptoms() {
        return symptoms;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public String getActivetime() {
        return activetime;
    }

    public void setActivetime(String activetime) {
        this.activetime = activetime;
    }

    public String getOrientation() {
        return orientation;
    }

    public void setOrientation(String orientation) {
        this.orientation = orientation;
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

    public String getRepsselected() {
        return repsselected;
    }

    public void setRepsselected(String repsselected) {
        this.repsselected = repsselected;
    }

    public String getMusclename() {
        return musclename;
    }

    public void setMusclename(String musclename) {
        this.musclename = musclename;
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

    public String getSessioncolor() {
        return sessioncolor;
    }

    public void setSessioncolor(String sessioncolor) {
        this.sessioncolor = sessioncolor;
    }

    public JSONArray getEmgdata() {
        return emgdata;
    }

    public void setEmgdata(JSONArray emgdata) {
        this.emgdata = emgdata;
    }

    public JSONArray getRomdata() {
        return romdata;
    }

    public void setRomdata(JSONArray romdata) {
        this.romdata = romdata;
    }

    public void setActivityList(JSONArray activity_list) {
        this.activity_list = activity_list;
    }
}
