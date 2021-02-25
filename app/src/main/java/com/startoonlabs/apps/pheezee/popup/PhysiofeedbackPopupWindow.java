package com.startoonlabs.apps.pheezee.popup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import com.startoonlabs.apps.pheezee.activities.MonitorActivity;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.activities.SessionReportActivity;
import com.startoonlabs.apps.pheezee.classes.PatientActivitySingleton;
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

import static com.startoonlabs.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSION;
import static com.startoonlabs.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSIONS_COMPLETED;
import static com.startoonlabs.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;

public class PhysiofeedbackPopupWindow {
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    private boolean session_inserted_in_server = false;
    private String dateString;
    private Context context;
    private PopupWindow report;
    private int maxEmgValue, maxAngle, minAngle, angleCorrection, exercise_selected_position, body_part_selected_position, repsselected,hold_angle_session;
    private String sessionNo, mmt_selected = "", orientation, bodypart, phizioemail, patientname, patientid, sessiontime, actiontime,
            holdtime, numofreps, body_orientation="", session_type="", dateofjoin, exercise_name, muscle_name, min_angle_selected,
            max_angle_selected, max_emg_selected;
    private String bodyOrientation="";
    private MqttSyncRepository repository;
    private MqttSyncRepository.OnSessionDataResponse response_data;
    private Long tsLong;
    private boolean mmt_selected_flag = false;
    private View layout_d;
    JSONArray emgJsonArray, romJsonArray;
    int phizio_packagetype;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public PhysiofeedbackPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps, int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected,View layout_d,JSONArray emgJsonArray, JSONArray romJsonArray,int phizio_packagetype,int hold_angle_session){
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
        this.layout_d = layout_d;
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
        this.phizio_packagetype=phizio_packagetype;
        this.hold_angle_session=hold_angle_session;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }

    public void showWindow(){
        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.physiofeedback_popup, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.physiofeedback_popup, null);
        }

        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;

        report = new PopupWindow(layout, width-100, ConstraintLayout.LayoutParams.WRAP_CONTENT,true);
        report.setWindowLayoutMode(width-100,ConstraintLayout.LayoutParams.WRAP_CONTENT);
        report.setOutsideTouchable(true);


        report.showAtLocation(layout, Gravity.CENTER, 0, 0);


        final LinearLayout ll_mmt_confirm = layout.findViewById(R.id.bp_model_mmt_confirm);

        LinearLayout ll_mmt_container = layout.findViewById(R.id.ll_mmt_grading);

        final RadioGroup rg_session_type = layout.findViewById(R.id.rg_session_type);
        final TextView ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);

        EditText et_remarks = layout.findViewById(R.id.et_remarks);
        TextView tv_confirm = layout.findViewById(R.id.tv_confirm_ll_overall_summary);
        ImageView image_exercise = layout.findViewById(R.id.image_exercise);

        // Setting the proper image


        String feedback_image = orientation+"_"+bodypart+"_"+exercise_name;
        feedback_image = "ic_fb_"+feedback_image;
        feedback_image = feedback_image.replace(" - ","_");
        feedback_image = feedback_image.replace(" ","_");
        feedback_image = feedback_image.replace(")","");
        feedback_image = feedback_image.replace("(","");
        feedback_image = feedback_image.toLowerCase();

        int res = context.getResources().getIdentifier(feedback_image, "drawable",context.getPackageName());

        if(res !=0) {
            image_exercise.setImageResource(res);
        }



        rg_session_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                tv_confirm.setText("Confirm Session");
            }
        });


        for (int i=0;i<ll_mmt_container.getChildCount();i++){
            View view_nested = ll_mmt_container.getChildAt(i);
            view_nested.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mmt_selected_flag = true;
//                    tv_confirm.setText("Confirm Session");
                    LinearLayout ll_container = ((LinearLayout)v);
                    LinearLayout parent = (LinearLayout) ll_container.getParent();
                    for (int i=0;i<parent.getChildCount();i++){
                        LinearLayout ll_child = (LinearLayout) parent.getChildAt(i);
                        TextView tv_childs = (TextView) ll_child.getChildAt(0);
                        tv_childs.setBackgroundResource(R.drawable.grey_circle_feedback);
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


        //for held on date
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateString = formatter.format(new Date(tsLong));

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

        ll_click_to_view_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report.dismiss();
//                if(IS_SCEDULED_SESSION){
//                    if(IS_SCEDULED_SESSIONS_COMPLETED){
//                        Intent i = new Intent(context, PatientsView.class);
//                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        context.startActivity(i);
//                    }
//                }
//                ((Activity)context).finish();
//                if(NetworkOperations.isNetworkAvailable(context)){
//                    Intent mmt_intent = new Intent(context, SessionReportActivity.class);
//                    mmt_intent.putExtra("patientid", patientid);
//                    mmt_intent.putExtra("patientname", patientname);
//                    mmt_intent.putExtra("phizioemail", phizioemail);
//                    mmt_intent.putExtra("dateofjoin",dateofjoin);
//                    ((Activity)context).startActivity(mmt_intent);
//                    report.dismiss();
//                }
//                else {
//                    NetworkOperations.networkError(context);
//                }
            }
        });

        ll_mmt_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
                ll_mmt_confirm.setAnimation(aniFade);
                String type = tv_confirm.getText().toString();
                if(type.equalsIgnoreCase("Next")) {

                    RadioButton rb_session_type = layout.findViewById(rg_session_type.getCheckedRadioButtonId());
                    if (rb_session_type != null && mmt_selected_flag == true) {
                        session_type = rb_session_type.getText().toString();
                    }
                    String check = mmt_selected.concat(session_type);
                    String comment_session = et_remarks.getText().toString();
                    if (rb_session_type != null && mmt_selected_flag == true) {
                        if (!check.equalsIgnoreCase("Next")) {
//                        tv_confirm.setText("Next Session");
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

                            //Else
                            ll_click_to_view_report.setAnimation(aniFade);
                            ViewExercisePopupWindow feedback = new ViewExercisePopupWindow(context,maxEmgValue, sessionNo, maxAngle, minAngle, orientation, bodypart,
                                    phizioemail, sessiontime, actiontime, holdtime, numofreps,
                                    angleCorrection, patientid, patientname, tsLong, bodyOrientation, dateofjoin, exercise_selected_position,body_part_selected_position,
                                    muscle_name,exercise_name,min_angle_selected,max_angle_selected,max_emg_selected,repsselected,hold_angle_session,mmt_selected,session_type,comment_session);
                            feedback.showWindow();
                            feedback.storeLocalSessionDetails(emgJsonArray,romJsonArray);

                            // Setting shared preference for deciding to download the report or not
                            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                            editor = sharedPreferences.edit();

                            //Setting date in yyyy/mm/dd format
                            SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd");
                            String sharedpref_date = date_formatter.format(new Date(tsLong));
                            editor.putBoolean(patientid+sharedpref_date, true);
                            editor.apply();
                            Log.d("sharedpreff",patientid+sharedpref_date);

                            if(phizio_packagetype!=STANDARD_PACKAGE)
                                repository.getPatientSessionNo(patientid);
                            feedback.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
                                @Override
                                public void onInsertSessionData(Boolean response, String message) {
                                    if (response)
                                        showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());

                                }

                                @Override
                                public void onSessionDeleted(Boolean response, String message) {
                                    showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());
                                }

                                @Override
                                public void onMmtValuesUpdated(Boolean response, String message) {
                                    showToast(message.substring(0,1).toUpperCase()+ message.substring(1).toLowerCase());
                                }

                                @Override
                                public void onCommentSessionUpdated(Boolean response) {
                                }
                            });
//                            report.dismiss();
//                            if (IS_SCEDULED_SESSION) {
//                                if (IS_SCEDULED_SESSIONS_COMPLETED) {
//                                    Intent i = new Intent(context, PatientsView.class);
//                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                                    context.startActivity(i);
//                                }
//                            }
//                            ((Activity) context).finish();

                        }
                    }else {
                        showToast("Please select MMT & Session type");
                    }
                }


            }
        });










        report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
//                View layout_dismiss = ((Activity) context).getLayoutInflater().inflate(R.layout.session_summary, null);
                FrameLayout layout_MainMenu = (FrameLayout) layout_d.findViewById(R.id.session_summary_frame);

                layout_MainMenu.getForeground().setAlpha(0);

                Log.d("test","closed");

                if(IS_SCEDULED_SESSIONS_COMPLETED) {
                    if(context!=null)
                        ((MonitorActivity) context).sceduledSessionsHasBeenCompletedDialog();
                }
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
                    object.put("holdangle",hold_angle_session);
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
                    object.put("sessioncolor",ValueBasedColorOperations.getCOlorBasedOnTheBodyPartExercise(bodypart,exercise_selected_position,maxAngle,minAngle,context));
                    Gson gson = new GsonBuilder().create();
                    Lock lock = new ReentrantLock();
                    lock.lock();
                    SessionData data = gson.fromJson(object.toString(),SessionData.class);
                    data.setEmgdata(emgJsonArray);
                    data.setRomdata(romJsonArray);
                    data.setActivityList(PatientActivitySingleton.getInstance().getactivitylist());
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


