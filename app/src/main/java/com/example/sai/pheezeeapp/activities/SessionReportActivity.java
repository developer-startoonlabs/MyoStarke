package com.example.sai.pheezeeapp.activities;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sai.pheezeeapp.fragments.FragmentReportDay;
import com.example.sai.pheezeeapp.fragments.ReportMonth;
import com.example.sai.pheezeeapp.fragments.ReportWeek;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SessionReportActivity extends AppCompatActivity {

    JSONArray session_arry;
    boolean initia = true;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressDialog progress;

    TextView tv_day, tv_week, tv_month, tv_overall_summary;





    ImageView iv_go_back;


    public static String patientId , phizioemail;
    String mqtt_publish_getpatientReport_response = "patient/generate/report/response";
    String mqtt_publish_getpatientReport = "patient/generate/report";

    MqttHelper mqttHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_report);
        fragmentManager = getSupportFragmentManager();

        declareView();

        patientId = getIntent().getStringExtra("patientid");
        phizioemail = getIntent().getStringExtra("phizioemail");

        mqttHelper = new MqttHelper(this);
        progress = new ProgressDialog(this);
        progress.setMessage("Generating report");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setIndeterminate(true);
        progress.show();


        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {
                MqttMessage message = new MqttMessage();
                JSONObject object = new JSONObject();
                try {
                    object.put("patientid",patientId);
                    object.put("phizioemail",phizioemail);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                message.setPayload(object.toString().getBytes());
                if(initia==true)
                    mqttHelper.publishMqttTopic(mqtt_publish_getpatientReport,message);
            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                if(topic.equals(mqtt_publish_getpatientReport_response+phizioemail)) {
                    initia = false;
                    progress.dismiss();
                    openWeekFragment();
                    session_arry = new JSONArray(message.toString());
                    Log.i("array sessions",session_arry.toString());
                    Log.i("array sessions len",String.valueOf(session_arry.length()));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });



    }

    private void declareView() {

        tv_day = findViewById(R.id.tv_session_report_day);
        tv_month = findViewById(R.id.tv_session_report_month);
        tv_week = findViewById(R.id.tv_session_report_week);
        tv_overall_summary = findViewById(R.id.tv_session_report_overall_report);
        iv_go_back = findViewById(R.id.iv_back_session_report);



        iv_go_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


//        tv_day.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeViewOfDayMonthWeek();
//                tv_day.setTypeface(null, Typeface.BOLD);
//                tv_day.setAlpha(1);
//
//                openDayFragment();
//            }
//        });


//        tv_month.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                changeViewOfDayMonthWeek();
//                tv_month.setTypeface(null, Typeface.BOLD);
//                tv_month.setAlpha(1);
//
//                openMonthFragment();
//            }
//        });

        tv_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_week.setTypeface(null, Typeface.BOLD);
                tv_week.setAlpha(1);
                openWeekFragment();
            }
        });


    }



    public void changeViewOfDayMonthWeek(){
        tv_month.setTypeface(null, Typeface.NORMAL);
        tv_week.setTypeface(null, Typeface.NORMAL);
        tv_day.setTypeface(null, Typeface.NORMAL);
        tv_day.setAlpha(0.5f);
        tv_week.setAlpha(0.5f);
        tv_month.setAlpha(0.5f);
    }


    public void openDayFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new FragmentReportDay();
        fragmentTransaction.replace(R.id.fragment_report_container,fragment);
        fragmentTransaction.commit();
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void openWeekFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ReportWeek();
        fragmentTransaction.replace(R.id.fragment_report_container,fragment);
        fragmentTransaction.commit();
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }

    public void openMonthFragment(){
        fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new ReportMonth();
        fragmentTransaction.replace(R.id.fragment_report_container,fragment);
        fragmentTransaction.commit();
        FragmentManager fm = getSupportFragmentManager();
        for(int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
    }


    public JSONArray getSessions(){
        return session_arry;
    }
}
