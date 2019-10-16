package com.startoonlabs.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.BodyPartSelection;
import com.startoonlabs.apps.pheezee.activities.SessionReportActivity;
import com.startoonlabs.apps.pheezee.pojos.CommentSessionUpdateData;
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

public class SessionSummaryPopupWindow {
    String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    boolean session_inserted_in_server = false;
    Context context;
    PopupWindow report;
    int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected;
    private String sessionNo, mmt_selected = "", orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation="", session_type="", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected;
    String bodyOrientation="";

    JSONArray emgJsonArray,romJsonArray;
    MqttSyncRepository repository;
    MqttSyncRepository.OnSessionDataResponse response_data;
    Long tsLong;
    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps, JSONArray emgJsonArray, JSONArray romJsonArray, int angleCorrection,
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
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
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
        TextView tv_exercise_name = layout.findViewById(R.id.tv_exercise_name);
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


//        et_remarks.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tv_confirm.setText("Confirm");
//            }
//        });


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
        tv_exercise_name.setText(exercise_name);
        tv_orientation_and_bodypart.setText(orientation+"-"+bodypart);
        tv_musclename.setText(muscle_name);

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

        ll_click_to_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(NetworkOperations.isNetworkAvailable(context)){
                    Intent mmt_intent = new Intent(context, SessionReportActivity.class);
                    mmt_intent.putExtra("patientid", tv_patient_id.getText().toString());
                    mmt_intent.putExtra("patientname", tv_patient_name.getText().toString());
                    mmt_intent.putExtra("phizioemail", phizioemail);
                    mmt_intent.putExtra("dateofjoin",dateofjoin);
                    ((Activity)context).startActivity(mmt_intent);
                }
                else {
                    NetworkOperations.networkError(context);
                }
            }
        });

        ll_click_to_view_report.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    ll_click_to_view_report.setAlpha(0.4f);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    ll_click_to_view_report.setAlpha(1f);
                }
                return false;
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

        tv_patient_id.setText(patientid);
        tv_patient_name.setText(patientname);

        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
        final String dateString = formatter.format(new Date(tsLong));
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

        storeLocalSessionDetails(dateString,sessiontime);

        ll_mmt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        new SendDataAsyncTask(mqttSync).execute();
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

        ll_mmt_confirm.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    ll_mmt_confirm.setAlpha(0.4f);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    ll_mmt_confirm.setAlpha(1f);
                }
                return false;
            }
        });

        tv_delete_pateint_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject object = new JSONObject();
                try {
                    object.put("phizioemail", phizioemail);
                    object.put("patientid", patientid);
                    object.put("heldon", dateString);
                }catch (JSONException e) {
                    e.printStackTrace();
                }
                MqttSync mqttSync = new MqttSync(mqtt_delete_pateint_session, object.toString());
                new SendDataAsyncTask(mqttSync).execute();
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
    public class SendDataAsyncTask extends AsyncTask<Void,Void,Long> {
        private MqttSync mqttSync;
        public SendDataAsyncTask(MqttSync mqttSync){
            this.mqttSync = mqttSync;
        }

        @Override
        protected Long doInBackground(Void... voids) {
            return PheezeeDatabase.getInstance(context).mqttSyncDao().insert(mqttSync);
        }

        @Override
        protected void onPostExecute(Long id) {
            super.onPostExecute(id);
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
        }
    }

    /**
     * collects all the data of the session and sends to async task to send the data to the server and also to store locally.
     * @param dateString
     * @param tempsession
     */
    private void storeLocalSessionDetails(String dateString,String tempsession) {
            try {
                JSONObject object = new JSONObject();
                //Log.i("datestring","2019-04-22 13:08:34");
                object.put("heldon",dateString);
                object.put("maxangle",maxAngle);
                object.put("minangle",minAngle);
                object.put("anglecorrected",angleCorrection);
                object.put("maxemg",maxEmgValue);
                object.put("holdtime",holdtime);
                object.put("bodypart",bodypart);
                object.put("sessiontime",tempsession);
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
                SessionData data = gson.fromJson(object.toString(),SessionData.class);
                data.setEmgdata(emgJsonArray);
                data.setRomdata(romJsonArray);
                object = new JSONObject(gson.toJson(data));
                MqttSync sync = new MqttSync(mqtt_publish_add_patient_session_emg_data,object.toString());
                new SendDataAsyncTask(sync).execute();
                int numofsessions = Integer.parseInt(sessionNo);
//                numofsessions+=1;
                repository.setPatientSessionNumber(String.valueOf(numofsessions),patientid);
            }catch (JSONException e) {
                e.printStackTrace();
            }
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


