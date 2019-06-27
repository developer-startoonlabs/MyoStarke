package com.startoonlabs.apps.pheezee.classes;

public class BodyPartWithMmtSelectionModel {
    int iv_body_part;
    String exercise_name;

    public BodyPartWithMmtSelectionModel(int iv_body_part, String exercise_name) {
        this.iv_body_part = iv_body_part;
        this.exercise_name = exercise_name;
    }

    public int getIv_body_part() {
        return iv_body_part;
    }

    public void setIv_body_part(int iv_body_part) {
        this.iv_body_part = iv_body_part;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }
}
