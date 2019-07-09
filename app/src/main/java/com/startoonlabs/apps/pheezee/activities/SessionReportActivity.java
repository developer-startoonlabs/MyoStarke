package com.startoonlabs.apps.pheezee.activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.startoonlabs.apps.pheezee.fragments.FragmentReportDay;
import com.startoonlabs.apps.pheezee.fragments.ReportMonth;
import com.startoonlabs.apps.pheezee.fragments.ReportWeek;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SessionReportActivity extends AppCompatActivity {

    JSONArray session_arry;
    boolean initia = true;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ProgressDialog progress;
    private static final String TAG = "Report File";String dateSelected = null;
    TextView tv_day, tv_week, tv_month, tv_overall_summary;



    final Calendar myCalendar = Calendar.getInstance();

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
                    tv_day.performClick();
//                    openWeekFragment();
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


        tv_day.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeViewOfDayMonthWeek();
                tv_day.setTypeface(null, Typeface.BOLD);
                tv_day.setAlpha(1);
                new DatePickerDialog(SessionReportActivity.this, dateChangedListener, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


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

    DatePickerDialog.OnDateSetListener dateChangedListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, month);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel();
            if(dateSelected!=null) {
                GetDataService getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
                Call<ResponseBody> fileCall = getDataService.getReport("/getreport/"+patientId+"/"+phizioemail+"/" + dateSelected);
                sendToast("Generating report please wait....");
                fileCall.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        Log.i("Response", response.body().toString());
                        File file = writeResponseBodyToDisk(response.body(), phizioemail+"-"+patientId);
                        if (file != null) {
                            Intent target = new Intent(Intent.ACTION_VIEW);
                            target.setDataAndType(FileProvider.getUriForFile(SessionReportActivity.this, getApplicationContext().getPackageName() + ".my.package.name.provider", file), "application/pdf");
                            target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

                            Intent intent = Intent.createChooser(target, "Open File");
                            try {
                                startActivity(target);
                            } catch (ActivityNotFoundException e) {
                                // Instruct the user to install a PDF reader here, or something
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {

                    }
                });
            }

        }
    };

    private void updateLabel() {
        String myFormat = "yyyy-MM-dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
        dateSelected = sdf.format(myCalendar.getTime());
        Log.i("date selected",dateSelected);
    }


    private File writeResponseBodyToDisk(ResponseBody body, String name) {
        File reportPdf=null, file=null;
        try {
            // todo change the file location/name according to your needs
            reportPdf = new File(Environment.getExternalStorageDirectory()+"/Pheezee/files","reports");
            if(!reportPdf.exists())
                reportPdf.mkdirs();

            file = new File(reportPdf,name+".pdf");
            if(!file.exists())
                file.createNewFile();

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return file;
            }finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return file;
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

    public void sendToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }


    public JSONArray getSessions(){
        return session_arry;
    }
}
