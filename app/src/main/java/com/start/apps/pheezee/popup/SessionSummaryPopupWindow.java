package com.start.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
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
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.MonitorActivity;
import com.start.apps.pheezee.classes.PatientActivitySingleton;
import com.start.apps.pheezee.pojos.DeleteSessionData;
import com.start.apps.pheezee.pojos.MmtData;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PhizioSessionReportData;
import com.start.apps.pheezee.pojos.SessionData;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.MqttSync;
import com.start.apps.pheezee.room.PheezeeDatabase;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.TakeScreenShot;
import com.start.apps.pheezee.utils.ValueBasedColorOperations;
import com.start.apps.pheezee.views.ArcViewInside;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.start.apps.pheezee.activities.MonitorActivity.IS_SCEDULED_SESSIONS_COMPLETED;
import static com.start.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;

public class SessionSummaryPopupWindow {
    private String mqtt_delete_pateint_session = "phizio/patient/deletepatient/sesssion";
    private String mqtt_publish_update_patient_mmt_grade = "phizio/patient/updateMmtGrade";
    private String mqtt_publish_add_patient_session_emg_data = "patient/entireEmgData";

    GetDataService getDataService;

    private boolean session_inserted_in_server = false;
    JSONArray emgJsonArray, romJsonArray;
    int phizio_packagetype;
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
    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps,  int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected,JSONArray emgJsonArray, JSONArray romJsonArray,int phizio_packagetype,int hold_angle_session){
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
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
        this.phizio_packagetype=phizio_packagetype;
        this.hold_angle_session=hold_angle_session;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }

    public SessionSummaryPopupWindow(Context context, int maxEmgValue, String sessionNo, int maxAngle, int minAngle,
                                     String orientation, String bodypart, String phizioemail, String sessiontime, String actiontime,
                                     String holdtime, String numofreps,  int angleCorrection,
                                     String patientid, String patientname, Long tsLong, String bodyOrientation, String dateOfJoin,
                                     int exercise_selected_position, int body_part_selected_position, String muscle_name, String exercise_name,
                                     String min_angle_selected, String max_angle_selected, String max_emg_selected, int repsselected,JSONArray emgJsonArray, JSONArray romJsonArray,int phizio_packagetype){
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
        this.emgJsonArray = emgJsonArray;
        this.romJsonArray = romJsonArray;
        this.phizio_packagetype=phizio_packagetype;
        repository = new MqttSyncRepository(((Activity)context).getApplication());
        repository.setOnSessionDataResponse(onSessionDataResponse);
    }


    public void showWindow(){

        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);


        Configuration config = ((Activity)context).getResources().getConfiguration();
        final View layout;
        if (config.smallestScreenWidthDp >= 600)
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary, null);
        }
        else
        {
            layout = ((Activity)context).getLayoutInflater().inflate(R.layout.session_summary, null);
        }

        FrameLayout layout_MainMenu = (FrameLayout) layout.findViewById(R.id.session_summary_frame);
        layout_MainMenu.getForeground().setAlpha(0);

        int color = ValueBasedColorOperations.getCOlorBasedOnTheBodyPart(bodypart,
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

//        final LinearLayout ll_click_to_view_report = layout.findViewById(R.id.ll_click_to_view_report);
        final LinearLayout ll_click_to_next = layout.findViewById(R.id.ll_click_to_next);


        //Share and cancel image view
        ImageView summary_go_back = layout.findViewById(R.id.summary_go_back);
        ImageView summary_share =  layout.findViewById(R.id.summary_share);

        //Emg Progress Bar
        ProgressBar pb_max_emg = layout.findViewById(R.id.progress_max_emg);

//        if(IS_SCEDULED_SESSION && !IS_SCEDULED_SESSIONS_COMPLETED){
//            ll_click_to_view_report.setVisibility(View.GONE);
//        }else {
//            ll_click_to_view_report.setVisibility(View.VISIBLE);
//        }

        try
        {
            //for held on date
            SimpleDateFormat formatter_date = new SimpleDateFormat("yyyy-MM-dd");
            String dateString_sessionnumber = formatter_date.format(new Date(tsLong));
            // Haaris
            PatientStatusData data = new PatientStatusData(phizioemail, patientid,dateString_sessionnumber,dateString_sessionnumber);
            Call<PhizioSessionReportData> getsession_report_count_response = getDataService.getsession_number_count(data);
            getsession_report_count_response.enqueue(new Callback<PhizioSessionReportData>() {
                @Override
                public void onResponse(@NonNull Call<PhizioSessionReportData> call, @NonNull Response<PhizioSessionReportData> response) {
                    if (response.code() == 200) {
                        PhizioSessionReportData res = response.body();
                        sessionNo = res.getSession().toString();
                        tv_session_num.setText(sessionNo);


                    }
                }

                @Override
                public void onFailure(@NonNull Call<PhizioSessionReportData> call, @NonNull Throwable t) {

                }
            });
        }catch (Exception e)
        {
            e.printStackTrace();
        }


        tv_orientation_and_bodypart.setText(orientation+"-"+bodypart+"-"+exercise_name);
        tv_musclename.setText(muscle_name);

        if(exercise_name.equalsIgnoreCase("Isometric")){
            maxAngle = 0;
            minAngle = 0;
        }

//        ll_click_to_view_report.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Animation aniFade = AnimationUtils.loadAnimation(context,R.anim.fade_in);
//                ll_click_to_view_report.setAnimation(aniFade);
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
//            }
//        });

        ll_click_to_next.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("Range")
            @Override
            public void onClick(View v) {
                PhysiofeedbackPopupWindow feedback = new PhysiofeedbackPopupWindow(context,maxEmgValue, sessionNo, maxAngle, minAngle, orientation, bodypart,
                        phizioemail, sessiontime, actiontime, holdtime, numofreps,
                        angleCorrection, patientid, patientname, tsLong, bodyOrientation, dateofjoin, exercise_selected_position,body_part_selected_position,
                        muscle_name,exercise_name,min_angle_selected,max_angle_selected,max_emg_selected,repsselected,layout,emgJsonArray,romJsonArray,phizio_packagetype,hold_angle_session);
                feedback.showWindow();
                layout_MainMenu.getForeground().setAlpha(160);

                //feedback.storeLocalSessionDetails(emgJsonArray,romJsonArray);
                if(phizio_packagetype!=STANDARD_PACKAGE)
                    repository.getPatientSessionNo(patientid);
                feedback.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
                    @Override
                    public void onInsertSessionData(Boolean response, String message) {
                        if (response)
                            showToast(message);
                    }

                    @Override
                    public void onSessionDeleted(Boolean response, String message) {
                        showToast(message);
                    }

                    @Override
                    public void onMmtValuesUpdated(Boolean response, String message) {
                        showToast(message);
                    }

                    @Override
                    public void onCommentSessionUpdated(Boolean response) {
                    }
                });


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
        tv_max_angle.setText(String.valueOf(maxAngle).concat("°"));

        //total session time
        sessiontime = sessiontime.substring(0,2)+"m"+sessiontime.substring(3,7)+"s";
        tv_total_time.setText(sessiontime);

        tv_action_time_summary.setText(actiontime);
        tv_hold_time.setText(holdtime);
        tv_num_of_reps.setText(numofreps);
        tv_max_emg.setText(String.valueOf(maxEmgValue).concat(((Activity)context).getResources().getString(R.string.emg_unit)));

        tv_range.setText(String.valueOf(maxAngle-minAngle).concat("°"));

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





        tv_delete_pateint_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = tv_delete_pateint_session.getText().toString();
                if(type.toLowerCase().contains("delete")) {

                    // Custom notification added by Haaris
                    // custom dialog
                    final Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.notification_dialog_box);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(dialog.getWindow().getAttributes());
                    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                    TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                    TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                    Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                    Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                    Notification_Button_ok.setText("Confirm");
                    Notification_Button_cancel.setText("Cancel");

                    // Setting up the notification dialog
                    notification_title.setText("Deleting a session");
                    notification_message.setText("Are you sure you want to delete the \n session from the list. Please Confirm");

                    // On click on Continue
                    Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            tv_delete_pateint_session.setText("New Session");
                            ll_click_to_next.setVisibility(View.GONE);
                            Animation aniFade = AnimationUtils.loadAnimation(context, R.anim.fade_in);
                            tv_delete_pateint_session.setAnimation(aniFade);
                            JSONObject object = new JSONObject();
                            try {
                                object.put("phizioemail", phizioemail);
                                object.put("patientid", patientid);
                                object.put("heldon", dateString);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            MqttSync mqttSync = new MqttSync(mqtt_delete_pateint_session, object.toString());
                            new StoreLocalDataAsync(mqttSync).execute();
                            dialog.dismiss();

                        }
                    });
                    // On click Cancel
                    Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();

                        }
                    });

                    dialog.show();
                    dialog.getWindow().setAttributes(lp);

                    // End



                }else {
//                    report.dismiss();
                    ((Activity)context).finish();
                }
            }
        });

        report.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
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


