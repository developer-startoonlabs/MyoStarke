package com.startoonlabs.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.SessionReportActivity;
import com.startoonlabs.apps.pheezee.pojos.DeleteSessionData;
import com.startoonlabs.apps.pheezee.pojos.MmtData;
import com.startoonlabs.apps.pheezee.pojos.SessionData;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;
import com.startoonlabs.apps.pheezee.utils.NetworkOperations;
import com.startoonlabs.apps.pheezee.utils.TakeScreenShot;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;
import com.startoonlabs.apps.pheezee.views.ArcViewInside;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SessionSummaryPopupWindow {
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    private boolean session_inserted_in_server = false;
    private String dateString;
    private Context context;
    private PopupWindow report;
    private int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected;
    private String sessionNo, mmt_selected = "", orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation="", session_type="", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected;
    private String bodyOrientation="";
    private MqttSyncRepository repository;
    private MqttSyncRepository.OnSessionDataResponse response_data;
    private Long tsLong;
    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps,  int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected){
        this.context = context;
        this.maxEmgValue = maxEmgValue;
        this.sessionNo = sessionNo;
        this.maxAngle = maxAngle;
        this.minAngle = minAngle;
        this.orientation = orientation;
        this.bodypart = bodypart;
        this.phizioemail = phizioemail;
        this.sessiontime = sessiontime;
        this.actiontime = actiontime;
        this.holdtime = holdtime;
        this.numofreps = numofreps;
        this.angleCorrection = angleCorrection;
        this.patientid = patientid;
        this.patientname = patientname;
        this.tsLong = tsLong;
        this.bodyOrientation = bodyOrientation;
        this.dateofjoin = dateOfJoin;
        this.exercise_selected_position = exercise_selected_position;
        this.body_part_selected_position = body_part_selected_position;
        this.exercise_name = exercise_name;
        this.muscle_name = muscle_name;
        this.min_angle_selected = min_angle_selected;
        this.max_angle_selected = max_angle_selected;
        this.max_emg_selected = max_emg_selected;
        this.repsselected = repsselected;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }

    public void showWindow(){
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary_large, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary, null);
        }


        int color = ValueBasedColorOperations.getCOlorBasedOnTheBodyPart(body_part_selected_position,
                exercise_selected_position,maxAngle,minAngle,context);

        int emg_color = ValueBasedColorOperations.getEmgColor(400,maxEmgValue,context);
        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT,true);
        report.setWindowLayoutMode(ConstraintLayout.LayoutParams.MATCH_PARENT,ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.showAtLocation(layout, Gravity.CENTER, 0, 0);

        LinearLayout ll_min_max_arc = layout.findViewById(R.id.ll_min_max_arc);
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
        TextView tv_delete_pateint_session = layout.findViewById(R.id.summary_tv_delete_session);
        final LinearLayout ll_mmt_confirm = layout.findViewById(R.id.bp_model_mmt_confirm);

        LinearLayout ll_mmt_container = layout.findViewById(R.id.ll_mmt_grading);

        final RadioGroup rg_session_type = layout.findViewById(R.id.rg_session_type);
        final LinearLayout ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);

        EditText et_remarks = layout.findViewById(R.id.et_remarks);
        TextView tv_confirm = layout.findViewById(R.id.tv_confirm_ll_overall_summary);


        //Share and cancel image view
        ImageView summary_go_back = layout.findViewById(R.id.summary_go_back);
        ImageView summary_share =  layout.findViewById(R.id.summary_share);

        //Emg Progress Bar
        ProgressBar pb_max_emg = layout.findViewById(R.id.progress_max_emg);




        rg_session_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                tv_confirm.setText("Confirm");
            }
        });


        for (int i=0;i<ll_mmt_container.getChildCount();i++){
            View view_nested = ll_mmt_container.getChildAt(i);
            view_nested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tv_confirm.setText("Confirm");
                    LinearLayout ll_container = ((LinearLayout)v);
                    LinearLayout parent = (LinearLayout) ll_container.getParent();
                    for (int i=0;i<parent.getChildCount();i++){
                        LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                        TextView tv_childs = (TextView) ll_child.getChildAt(0);
                        tv_childs.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
                        tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
                    }
                    TextView tv_selected = (TextView) ll_container.getChildAt(0);
                    tv_selected.setBackgroundColor(Color.YELLOW);
                    mmt_selected=tv_selected.getText().toString();
                    tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
                    tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
                }
            });
        }

        tv_session_num.setText(sessionNo);
        tv_orientation_and_bodypart.setText(orientation+"-"+bodypart+"-"+exercise_name);
        tv_musclename.setText(muscle_name);

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

        ll_click_to_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                ll_click_to_view_report.setAnimation(aniFade);
                if(NetworkOperations.isNetworkAvailable(context)){
                    Intent mmt_intent = new Intent(context, SessionReportActivity.class);
                    mmt_intent.putExtra("patientid", patientid);
                    mmt_intent.putExtra("patientname", patientname);
                    mmt_intent.putExtra("phizioemail", phizioemail);
                    mmt_intent.putExtra("dateofjoin",dateofjoin);
                    ((Activity)context).startActivity(mmt_intent);
                }
                else {
                    NetworkOperations.networkError(context);
                }
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        dateString = formatter.format(new Date(tsLong));
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
        ArcViewInside arcView =layout.findViewById(R.id.session_summary_arcview);
        arcView.setMaxAngle(maxAngle);
        arcView.setMinAngle(minAngle);
        arcView.setRangeColor(color);

        if(!min_angle_selected.equals("") && !max_angle_selected.equals("")){
            int reference_min_angle = Integer.parseInt(min_angle_selected);
            int reference_max_angle = Integer.parseInt(max_angle_selected);
            arcView.setEnableAndMinMax(reference_min_angle,reference_max_angle,true);
        }

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


//        AsyncTask.execute(new Runnable() {
//            @Override
//            public void run() {
//                storeLocalSessionDetails(dateString,sessiontime);
//            }
//        });



        ll_mmt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                ll_mmt_confirm.setAnimation(aniFade);
                String type = tv_confirm.getText().toString();
                if(type.equalsIgnoreCase("Confirm")) {

                    RadioButton rb_session_type = layout.findViewById(rg_session_type.getCheckedRadioButtonId());
                    if (rb_session_type != null) {
                        session_type = rb_session_type.getText().toString();
                    }
                    String check = mmt_selected.concat(session_type);
                    String comment_session = et_remarks.getText().toString();
                    if (!check.equalsIgnoreCase("")) {
                        tv_confirm.setText("New Session");
                        JSONObject object = new JSONObject();
                        try {
                            object.put("phizioemail", phizioemail);
                            object.put("patientid", patientid);
                            object.put("heldon", dateString);
                            object.put("mmtgrade", mmt_selected);
                            object.put("bodyorientation", bodyOrientation);
                            object.put("sessiontype", session_type);
                            object.put("commentsession", comment_session);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MqttSync mqttSync = new MqttSync(mqtt_publish_update_patient_mmt_grade, object.toString());
                        new StoreLocalDataAsync(mqttSync).execute();
                    } else {
                        showToast("Nothing Selected");
                    }
                }else {
                    tv_confirm.setText("Confirm");
                    report.dismiss();
                    ((Activity)context).finish();
                }
            }
        });

        tv_delete_pateint_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                tv_delete_pateint_session.setAnimation(aniFade);
                JSONObject object = new JSONObject();
                try {
                    object.put("phizioemail", phizioemail);
                    object.put("patientid", patientid);
                    object.put("heldon", dateString);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttSync mqttSync = new MqttSync(mqtt_delete_pateint_session, object.toString());
                new StoreLocalDataAsync(mqttSync).execute();
            }
        });
    }

    private void showToast(String nothing_selected) {
        Toast.makeText(context, nothing_selected, Toast.LENGTH_SHORT).show();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LinearLayout ll_container = ((LinearLayout)v);
            LinearLayout parent = (LinearLayout) ll_container.getParent();
            for (int i=0;i<parent.getChildCount();i++){
                LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                TextView tv_childs = (TextView) ll_child.getChildAt(0);
                tv_childs.setBackgroundResource(R.drawable.drawable_mmt_circular_tv);
                tv_childs.setTextColor(ContextCompat.getColor(context,R.color.pitch_black));
            }
            TextView tv_selected = (TextView) ll_container.getChildAt(0);
            tv_selected.setBackgroundColor(Color.YELLOW);
            mmt_selected=tv_selected.getText().toString();
            tv_selected.setBackgroundResource(R.drawable.drawable_mmt_grade_selected);
            tv_selected.setTextColor(ContextCompat.getColor(context,R.color.white));
        }
    };

    /**
     * Sending data to the server and storing locally
     */
    public class StoreLocalDataAsync extends AsyncTask<Void,Void,Long> {
        private MqttSync mqttSync;
        public StoreLocalDataAsync(MqttSync mqttSync){
            this.mqttSync = mqttSync;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return PheezeeDatabase.getInstance(context).mqttSyncDao().insert(mqttSync);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
            new SendDataToServerAsync(mqttSync,id).execute();
        }
    }

    /**
     * Sending data to the server and storing locally
     */
    public class SendDataToServerAsync extends AsyncTask<Void, Void, Void> {
        private MqttSync mqttSync;
        private Long id;
        public SendDataToServerAsync(MqttSync mqttSync, Long id){
            this.mqttSync = mqttSync;
            this.id = id;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            try {
                JSONObject object = new JSONObject(mqttSync.getMessage());
                object.put("id",id);
                if(NetworkOperations.isNetworkAvailable(context)){
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    if(mqttSync.getTopic()==mqtt_publish_update_patient_mmt_grade){
                        if(session_inserted_in_server){
                            MmtData data = gson.fromJson(object.toString(),MmtData.class);
                            repository.updateMmtData(data);
                        }
                        else {

                        }
                    } else  if(mqttSync.getTopic()==mqtt_delete_pateint_session){
                        if(session_inserted_in_server){
                            DeleteSessionData data = gson.fromJson(object.toString(),DeleteSessionData.class);
                            repository.deleteSessionData(data);
                        }
                        else {

                        }
                    }
                    else {
                        SessionData data = gson.fromJson(object.toString(),SessionData.class);
                        repository.insertSessionData(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }
    }


    /**
     * collects all the data of the session and sends to async task to send the data to the server and also to store locally.
     * @param emgJsonArray
     * @param romJsonArray
     */
    public void storeLocalSessionDetails( JSONArray emgJsonArray, JSONArray romJsonArray) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    JSONObject object = new JSONObject();
                    object.put("heldon",dateString);
                    object.put("maxangle",maxAngle);
                    object.put("minangle",minAngle);
                    object.put("anglecorrected",angleCorrection);
                    object.put("maxemg",maxEmgValue);
                    object.put("holdtime",holdtime);
                    object.put("bodypart",bodypart);
                    object.put("sessiontime",sessiontime);
                    object.put("numofreps",numofreps);
                    object.put("numofsessions",sessionNo);
                    object.put("phizioemail",phizioemail);
                    object.put("patientid",patientid);
                    object.put("painscale","");
                    object.put("muscletone","");
                    object.put("exercisename",exercise_name);
                    object.put("commentsession","");
                    object.put("symptoms","");
                    object.put("activetime",actiontime);
                    object.put("orientation", orientation);
                    object.put("mmtgrade",mmt_selected);
                    object.put("bodyorientation",bodyOrientation);
                    object.put("sessiontype",session_type);
                    object.put("repsselected",repsselected);
                    object.put("musclename", muscle_name);
                    object.put("maxangleselected",max_angle_selected);
                    object.put("minangleselected",min_angle_selected);
                    object.put("maxemgselected",max_emg_selected);
                    object.put("sessioncolor",ValueBasedColorOperations.getCOlorBasedOnTheBodyPartExercise(body_part_selected_position,exercise_selected_position,maxAngle,minAngle,context));
                    Gson gson = new GsonBuilder().create();
                    Lock lock = new ReentrantLock();
                    lock.lock();
                    SessionData data = gson.fromJson(object.toString(),SessionData.class);
                    data.setEmgdata(emgJsonArray);
                    data.setRomdata(romJsonArray);
                    object = new JSONObject(gson.toJson(data));
                    MqttSync sync = new MqttSync(mqtt_publish_add_patient_session_emg_data,object.toString());
                    lock.unlock();
                    new StoreLocalDataAsync(sync).execute();
                    int numofsessions = Integer.parseInt(sessionNo);
                    repository.setPatientSessionNumber(String.valueOf(numofsessions),patientid);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    MqttSyncRepository.OnSessionDataResponse onSessionDataResponse = new MqttSyncRepository.OnSessionDataResponse() {
        @Override
        public void onInsertSessionData(Boolean response, String message) {
            if(response_data!=null){
                if(response){
                    session_inserted_in_server = true;
                }
                response_data.onInsertSessionData(response,message);
            }
        }

        @Override
        public void onSessionDeleted(Boolean response, String message) {
            if(response_data!=null){
                response_data.onSessionDeleted(response,message);
            }
        }

        @Override
        public void onMmtValuesUpdated(Boolean response, String message) {
            if(response_data!=null){
                response_data.onMmtValuesUpdated(response,message);
            }
        }

        @Override
        public void onCommentSessionUpdated(Boolean response) {
            if(response_data!=null){
                response_data.onCommentSessionUpdated(response);
            }
        }
    };



    public void setOnSessionDataResponse(MqttSyncRepository.OnSessionDataResponse response){
        this.response_data = response;
    }
}


