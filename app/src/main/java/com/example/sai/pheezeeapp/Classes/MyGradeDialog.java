package com.example.sai.pheezeeapp.Classes;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.example.sai.pheezeeapp.R;

public class MyGradeDialog {

    View v = null;

    public Dialog show(Context context) {
        Dialog d = new Dialog(context);
        v = LayoutInflater.from(context).inflate(R.layout.patient_grade_dialog_layout, null);
        d.setContentView(v);
        return d;
    }

    public void update() {
        v.invalidate();
    }

    public void show() {
    }
}
