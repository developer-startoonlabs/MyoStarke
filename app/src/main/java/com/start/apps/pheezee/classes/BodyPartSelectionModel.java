package com.start.apps.pheezee.classes;

public class BodyPartSelectionModel {
    int iv_body_part, iv_body_part_preview;

    String exercise_name;

    public BodyPartSelectionModel(int iv_body_part, int iv_body_part_preview, String exercise_name) {
        this.iv_body_part = iv_body_part;
        this.iv_body_part_preview = iv_body_part_preview;
        this.exercise_name = exercise_name;
    }

    public int getIv_body_part() {
        return iv_body_part;
    }

    public void setIv_body_part(int iv_body_part) {
        this.iv_body_part = iv_body_part;
    }

    public int getIv_body_part_preview() {
        return iv_body_part_preview;
    }

    public void setIv_body_part_preview(int iv_body_part_preview) {
        this.iv_body_part_preview = iv_body_part_preview;
    }

    public String getExercise_name() {
        return exercise_name;
    }

    public void setExercise_name(String exercise_name) {
        this.exercise_name = exercise_name;
    }
}
