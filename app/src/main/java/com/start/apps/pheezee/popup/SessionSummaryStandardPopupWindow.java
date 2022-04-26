package com.start.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.FileProvider;

import start.apps.pheezee.R;

import com.anychart.core.gantt.elements.BaselinesElement;
import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.utils.TakeScreenShot;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;
import com.start.apps.pheezee.views.ArcViewInside;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SessionSummaryStandardPopupWindow {
    private Context context;
    private PopupWindow report;
    private int maxEmgValue, maxAngle, minAngle, exercise_selected_position, body_part_selected_position, repsselected;
    private String sessionNo, orientation, bodypart, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected;
    private Long tsLong;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    public SessionSummaryStandardPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String sessiontime, String actiontime,
                                     String holdtime, String numofreps,
                                     String patientid, String patientname, Long tsLong,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, int repsselected){
        this.context = context;
        this.maxEmgValue = maxEmgValue;
        this.sessionNo = sessionNo;
        this.maxAngle = maxAngle;
        this.minAngle = minAngle;
        this.orientation = orientation;
        this.bodypart = bodypart;
        this.sessiontime = sessiontime;
        this.actiontime = actiontime;
        this.holdtime = holdtime;
        this.numofreps = numofreps;
        this.patientid = patientid;
        this.patientname = patientname;
        this.tsLong = tsLong;
        this.exercise_selected_position = exercise_selected_position;
        this.body_part_selected_position = body_part_selected_position;
        this.exercise_name = exercise_name;
        this.muscle_name = muscle_name;
        this.min_angle_selected = min_angle_selected;
        this.max_angle_selected = max_angle_selected;
        this.repsselected = repsselected;
    }

    public void showWindow(){
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary_large_standard, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary_standard, null);
        }


        int color = ValueBasedColorOperations.getCOlorBasedOnTheBodyPart(bodypart,
                exercise_selected_position,maxAngle,minAngle,context);

        int emg_color = ValueBasedColorOperations.getEmgColor(400,maxEmgValue,context);
        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        report.showAtLocation(layout, Gravity.CENTER, 0, 0);

        final TextView tv_patient_name =layout.findViewById(R.id.tv_summary_patient_name);
        final TextView tv_patient_id = layout.findViewById(R.id.tv_summary_patient_id);
        TextView tv_held_on = layout.findViewById(R.id.session_held_on);
        TextView tv_min_angle = layout.findViewById(R.id.tv_min_angle);
        TextView tv_max_angle = layout.findViewById(R.id.tv_max_angle);
        TextView tv_total_time = layout.findViewById(R.id.tv_total_time);
        TextView tv_action_time_summary = layout.findViewById(R.id.tv_action_time);
        TextView tv_hold_time = layout.findViewById(R.id.tv_hold_time);
        TextView tv_num_of_reps = layout.findViewById(R.id.tv_num_of_reps);
        TextView tv_max_emg = layout.findViewById(R.id.tv_max_emg);
        TextView tv_session_num = layout.findViewById(R.id.tv_session_no);
        TextView tv_orientation_and_bodypart = layout.findViewById(R.id.tv_orientation_and_bodypart);
        TextView tv_musclename = layout.findViewById(R.id.tv_muscle_name);
        TextView tv_range = layout.findViewById(R.id.tv_range_min_max);
        TextView tv_back_to_home = layout.findViewById(R.id.tv_back_to_home);

        //Share and cancel image view
        ImageView summary_go_back = layout.findViewById(R.id.summary_go_back);
        ImageView summary_share =  layout.findViewById(R.id.summary_share);

        //Emg Progress Bar
        ProgressBar pb_max_emg = layout.findViewById(R.id.progress_max_emg);




        tv_session_num.setText(sessionNo);
        tv_orientation_and_bodypart.setText(orientation+"-"+bodypart+"-"+exercise_name);
        tv_musclename.setText(muscle_name);

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

        tv_back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PatientsView.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                context.startActivity(i);
            }
        });

        summary_share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeScreenShot screenShot = new TakeScreenShot(context,patientname,patientid);
                File file = screenShot.takeScreenshot(report);
                Uri pdfURI = FileProvider.getUriForFile(context, ((Activity)context).getApplicationContext().getPackageName() + ".my.package.name.provider", file);

                Intent i = new Intent();
                i.setAction(Intent.ACTION_SEND);
                i.putExtra(Intent.EXTRA_STREAM,pdfURI);
                i.setType("application/jpg");
                ((Activity)context).startActivity(Intent.createChooser(i, "share pdf"));
            }
        });

        summary_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
            }
        });

        if(patientid.length()>3){
            String temp = patientid.substring(0,3)+"xxx";
            tv_patient_id.setText(temp);
        }else {
            tv_patient_id.setText(patientid);
        }
        tv_patient_name.setText(patientname);

        //for held on date
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        String dateString_date = formatter_date.format(new Date(tsLong));
        tv_held_on.setText(dateString_date);
        tv_min_angle.setText(String.valueOf(minAngle).concat("°"));
        tv_min_angle.setTextColor(color);
        tv_max_angle.setText(String.valueOf(maxAngle).concat("°"));
        tv_max_angle.setTextColor(color);

        //total session time
        sessiontime = sessiontime.substring(0,2)+"m"+sessiontime.substring(3,7)+"s";
        tv_total_time.setText(sessiontime);

        tv_action_time_summary.setText(actiontime);
        tv_hold_time.setText(holdtime);
        tv_num_of_reps.setText(numofreps);
        tv_max_emg.setText(String.valueOf(maxEmgValue).concat(((Activity)context).getResources().getString(R.string.emg_unit)));
        tv_max_emg.setTextColor(emg_color);

        tv_range.setText(String.valueOf(maxAngle-minAngle).concat("°"));
        tv_range.setTextColor(color);

        //Creating the arc
        ArcViewInside arcView = layout.findViewById(R.id.session_summary_arcview);
        arcView.setMaxAngle(maxAngle);
        arcView.setMinAngle(minAngle);
        arcView.setRangeColor(color);

        TextView tv_180 = layout.findViewById(R.id.tv_180);
        if(((Activity)context).getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            tv_180.setPadding(5,1,170,1);
        }

        pb_max_emg.setMax(3000);
        pb_max_emg.setProgress(maxEmgValue);
        pb_max_emg.setEnabled(false);
        LayerDrawable bgShape = (LayerDrawable) pb_max_emg.getProgressDrawable();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bgShape.findDrawableByLayerId(bgShape.getId(1)).setTint(emg_color);
        }
    }
}


