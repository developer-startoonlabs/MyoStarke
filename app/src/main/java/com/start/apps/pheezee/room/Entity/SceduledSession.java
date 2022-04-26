package com.start.apps.pheezee.room.Entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "sceduled_session")
public class SceduledSession {
    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String patientid;
    @NonNull
    private int sessionno;
    @NonNull
    private String bodypart;
    @NonNull
    private String side;
    @NonNull
    private String position;
    @NonNull
    private String exercise;
    @NonNull
    private String muscle;
    @NonNull
    private String reps;
    @NonNull
    private String emg;
    @NonNull
    private String angleMin;
    @NonNull
    private String angleMax;

    public SceduledSession(@NonNull String patientid, int sessionno, @NonNull String bodypart, @NonNull String side, @NonNull String position, @NonNull String exercise, @NonNull String muscle, @NonNull String reps, @NonNull String emg, @NonNull String angleMin, @NonNull String angleMax) {
        this.patientid = patientid;
        this.sessionno = sessionno;
        this.bodypart = bodypart;
        this.side = side;
        this.position = position;
        this.exercise = exercise;
        this.muscle = muscle;
        this.reps = reps;
        this.emg = emg;
        this.angleMin = angleMin;
        this.angleMax = angleMax;
    }

    @NonNull
    public String getPatientid() {
        return patientid;
    }

    public void setPatientid(@NonNull String patientid) {
        this.patientid = patientid;
    }

    @NonNull
    public int getSessionno() {
        return sessionno;
    }

    public void setSessionno(@NonNull int sessionno) {
        this.sessionno = sessionno;
    }

    @NonNull
    public String getBodypart() {
        return bodypart;
    }

    public void setBodypart(@NonNull String bodypart) {
        this.bodypart = bodypart;
    }

    @NonNull
    public String getSide() {
        return side;
    }

    public void setSide(@NonNull String side) {
        this.side = side;
    }

    @NonNull
    public String getPosition() {
        return position;
    }

    public void setPosition(@NonNull String position) {
        this.position = position;
    }

    @NonNull
    public String getExercise() {
        return exercise;
    }

    public void setExercise(@NonNull String exercise) {
        this.exercise = exercise;
    }

    @NonNull
    public String getMuscle() {
        return muscle;
    }

    public void setMuscle(@NonNull String muscle) {
        this.muscle = muscle;
    }

    @NonNull
    public String getReps() {
        return reps;
    }

    public void setReps(@NonNull String reps) {
        this.reps = reps;
    }

    @NonNull
    public String getEmg() {
        return emg;
    }

    public void setEmg(@NonNull String emg) {
        this.emg = emg;
    }

    @NonNull
    public String getAngleMin() {
        return angleMin;
    }

    public void setAngleMin(@NonNull String angleMin) {
        this.angleMin = angleMin;
    }

    @NonNull
    public String getAngleMax() {
        return angleMax;
    }

    public void setAngleMax(@NonNull String angleMax) {
        this.angleMax = angleMax;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
