package com.startoonlabs.apps.pheezee.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaScannerConnection;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.popup.SessionSummaryPopupWindow;
import com.startoonlabs.apps.pheezee.repository.MqttSyncRepository;
import com.startoonlabs.apps.pheezee.services.PheezeeBleService;
import com.startoonlabs.apps.pheezee.utils.AngleOperations;
import com.startoonlabs.apps.pheezee.utils.BatteryOperation;
import com.startoonlabs.apps.pheezee.utils.ByteToArrayOperations;
import com.startoonlabs.apps.pheezee.utils.TakeScreenShot;
import com.startoonlabs.apps.pheezee.views.ArcViewInside;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.battery_percent;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.bluetooth_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.device_disconnected_firmware;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.device_state;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.session_data;
import static com.startoonlabs.apps.pheezee.services.PheezeeBleService.usb_state;

public class MonitorActivity extends AppCompatActivity implements MqttSyncRepository.GetSessionNumberResponse {

    //session inserted on server
    private boolean sessionCompleted = false, can_beeep_max = true,can_beep_min = true;
    MqttSyncRepository repository;
    private String str_body_orientation="",json_phizioemail = "", patientid = "", bodyorientation = "", patientname = "";
    TextView tv_max_angle, tv_min_angle, tv_max_emg, tv_snap, Repetitions, holdTime,
            tv_session_no, tv_body_part, tv_repsselected, EMG, time, patientId, patientName, tv_action_time;
    private int ui_rate = 0, gain_initial = 20, body_orientation = 0, angleCorrection = 0,
            currentAngle = 0, Seconds, Minutes, maxAngle, minAngle, maxEmgValue;
    int REQUEST_ENABLE_BT = 1;
    boolean angleCorrected = false, deviceState = true, usbState = false;
    String bodypart, orientation = "NO", timeText = "", holdTimeValue = "0:0", exerciseType;
    private String  str_exercise_name, str_muscle_name, str_max_emg_selected, str_min_angle_selected, str_max_angle_selected;
    private int exercise_position, bodypart_position, repsselected, muscle_position;
    SharedPreferences sharedPreferences;
    JSONObject json_phizio = new JSONObject();
    JSONArray emgJsonArray, romJsonArray;
    ImageView iv_back_monitor;
    File file_session_emgdata, file_dir_session_emgdata, file_session_romdata, file_session_sessiondetails;
    FileOutputStream outputStream_session_emgdata, outputStream_session_romdata, outputStream_session_sessiondetails;
    List<Entry> dataPoints;
    LineChart lineChart;
    LineDataSet lineDataSet;
    ArcViewInside arcViewInside;
    ImageView iv_angle_correction;
    LineData lineData, lineDataNew;
    Button timer, btn_emg_decrease_gain, btn_emg_increase_gain, stopBtn, cancelBtn;
    long MillisecondTime, StartTime, TimeBuff, UpdateTime = 0L;
    Handler handler;
    LinearLayout emgSignal;
    Date rawdata_timestamp;
    Long tsLong = 0L;
    AngleOperations angleOperations;
    private boolean mSessionStarted = false, isBound = false;
    PheezeeBleService mService;

    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    AlertDialog deviceDisconnectedDialog, usbPluggedInDialog, error_device_dialog;


    public void deviceDisconnectedPopup(boolean operation) {
        String title = "Device Disconnected";
        String message;
        if(operation){
            message = "Please come in range to the device to continue the session";
        }else {
            message = "Please come in range to start session";
        }
        AlertDialog.Builder deviceDisconnected = new AlertDialog.Builder(MonitorActivity.this);
        deviceDisconnected.setTitle(title);
        deviceDisconnected.setMessage(message);
        deviceDisconnected.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        if(operation) {
            deviceDisconnected.setNegativeButton("End Session", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopBtn.performClick();
                }
            });
        }
        deviceDisconnectedDialog = deviceDisconnected.create();
        deviceDisconnectedDialog.show();
    }

    public void usbConnectedDialog(boolean operation) {
        String title = "Usb Connected";
        String message;
        if(operation){
            message = "Please disconnect usb to continue the session";
        }else {
            message = "Please disconnect usb to start session";
        }
        AlertDialog.Builder deviceDisconnected = new AlertDialog.Builder(MonitorActivity.this);
        deviceDisconnected.setTitle(title);
        deviceDisconnected.setMessage(message);
        deviceDisconnected.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        if(operation) {
            deviceDisconnected.setNegativeButton("End Session", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    stopBtn.performClick();
                }
            });
        }
        usbPluggedInDialog = deviceDisconnected.create();
        usbPluggedInDialog.show();
    }

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600) {
            setContentView(R.layout.activity_monitor);
        } else {
            setContentView(R.layout.fragment_monitor);
        }
        angleOperations = new AngleOperations();
        lineChart = findViewById(R.id.chart);
        EMG = findViewById(R.id.emgValue);
        arcViewInside = findViewById(R.id.arcViewInside);
        Repetitions = findViewById(R.id.Repetitions);
        holdTime = findViewById(R.id.holdtime);
        timer = findViewById(R.id.timer);
        stopBtn = findViewById(R.id.stopBtn);
        patientId = findViewById(R.id.patientId);
        patientName = findViewById(R.id.patientName);
        time = findViewById(R.id.displayTime);
        emgSignal = findViewById(R.id.emg);
        tv_session_no = findViewById(R.id.tv_session_no);
        tv_body_part = findViewById(R.id.bodyPart);
        cancelBtn = findViewById(R.id.cancel);
        iv_angle_correction = findViewById(R.id.tv_angleCorrection);
        tv_action_time = findViewById(R.id.tv_action_time);
        tv_max_angle = findViewById(R.id.tv_max_angle);
        tv_min_angle = findViewById(R.id.tv_min_angle);
        tv_max_emg = findViewById(R.id.tv_max_emg_show);
        tv_repsselected = findViewById(R.id.repsSelected);
        btn_emg_decrease_gain = findViewById(R.id.btn_emg_decrease_gain);
        btn_emg_increase_gain = findViewById(R.id.btn_emg_increase_gain);
        iv_back_monitor = findViewById(R.id.iv_back_monitor);
        tv_snap = findViewById(R.id.snap_monitor);

        handler = new Handler();
        emgJsonArray = new JSONArray();
        romJsonArray = new JSONArray();
        repository = new MqttSyncRepository(getApplication());
        repository.setOnSessionNumberResponse(this);

        tv_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeScreenShot screenShot = new TakeScreenShot(MonitorActivity.this, patientname, patientid);
                screenShot.takeScreenshot(null);
                showToast("Took Screenshot");
            }
        });

        btn_emg_increase_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSessionStarted) {
                    btn_emg_decrease_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
                    if (gain_initial < 120) {
                        gain_initial += 10;
                        lineChart.zoom(1.4f, 1.4f, ui_rate, ui_rate);
                        if (gain_initial == 120) {
                            btn_emg_increase_gain.setBackgroundColor(ContextCompat.getColor(MonitorActivity.this, R.color.home_semi_red));
                        }
                    }
                    mService.increaseGain();
                } else {
                    showToast("Please start the session!");
                }
            }
        });

        btn_emg_decrease_gain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSessionStarted) {
                    btn_emg_increase_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
                    if (gain_initial > 10) {
                        gain_initial -= 10;
                        if (gain_initial == 10) {
                            btn_emg_decrease_gain.setBackgroundColor(ContextCompat.getColor(MonitorActivity.this, R.color.home_semi_red));
                        }
                        lineChart.zoomOut();
                    }
                    mService.decreaseGain();
                } else {
                    showToast("Please start the session!");
                }
            }
        });

        tv_snap.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    tv_snap.setAlpha(0.4f);
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    tv_snap.setAlpha(1f);
                }
                return false;
            }
        });

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(sharedPreferences.getString("phiziodetails", ""));
            json_phizioemail = json_phizio.getString("phizioemail");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //get intent values
        patientid = getIntent().getStringExtra("patientId");
        patientname = getIntent().getStringExtra("patientName");
        bodyorientation = getIntent().getStringExtra("bodyorientation");
        body_orientation = getIntent().getIntExtra("body_orientation", 0);
        bodypart = getIntent().getStringExtra("exerciseType");
        orientation = getIntent().getStringExtra("orientation");
        str_body_orientation = getIntent().getStringExtra("bodyorientation");
        str_exercise_name = getIntent().getStringExtra("exercisename");

        str_muscle_name = getIntent().getStringExtra("musclename");
        str_max_emg_selected = getIntent().getStringExtra("maxemgselected");
        str_max_angle_selected = getIntent().getStringExtra("maxangleselected");
        str_min_angle_selected = getIntent().getStringExtra("minangleselected");
        exercise_position = getIntent().getIntExtra("exerciseposition",0);
        bodypart_position = getIntent().getIntExtra("bodypartposition",0);
        repsselected = getIntent().getIntExtra("repsselected",0);
        muscle_position = getIntent().getIntExtra("muscleposition",0);


        //setting patient id and name
        if(patientid.length()>3){
            String temp = patientid.substring(0,3)+"xxx";
            patientId.setText(temp);
        }else {
            patientId.setText(patientid);
        }

        patientName.setText(patientname);

        //setting session number
        repository.getPatientSessionNo(patientid);

        tv_body_part.setText(tv_body_part.getText().toString().concat(bodypart));
        tv_body_part.setText(orientation + "-" + bodypart + "-" + str_exercise_name);
        if (repsselected!= 0) {
            tv_repsselected.setText("/".concat(String.valueOf(repsselected)));
        } else {
            tv_repsselected.setVisibility(View.GONE);
        }
        arcViewInside.setMinAngle(0);
        arcViewInside.setMaxAngle(0);




        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        timer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = BatteryOperation.getDialogMessageForLowBattery(PatientsView.deviceBatteryPercent, MonitorActivity.this);
                if (!message.equalsIgnoreCase("c")) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
                    builder.setTitle("Battery Low");
                    builder.setMessage(message);
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(deviceState && !usbState)
                                startSession();
                            else {
                                if(!deviceState)
                                    deviceDisconnectedPopup(false);
                                else
                                    usbConnectedDialog(false);
                            }
                        }
                    });
                    builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                } else{
                    if(deviceState && !usbState)
                        startSession();
                    else {
                        if(!deviceState)
                            deviceDisconnectedPopup(false);
                        else
                            usbConnectedDialog(false);
                    }
                }

            }
        });

        /**
         * Cancel session
         */
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timer.getVisibility() == View.GONE) {
                    sessionCompleted = true;
                    mSessionStarted = false;
                    timer.setBackgroundResource(R.drawable.rounded_start_button);
                    stopBtn.setVisibility(View.GONE);
                    timer.setVisibility(View.VISIBLE);
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            mService.disableNotificationOfSession();
                        }
                    });
                    TimeBuff += MillisecondTime;
                    handler.removeCallbacks(runnable);
                    MillisecondTime = 0L;
                    StartTime = 0L;
                    TimeBuff = 0L;
                    UpdateTime = 0L;
                    Seconds = 0;
                    Minutes = 0;
                    timer.setText(R.string.timer_start);
                    tsLong = System.currentTimeMillis();
                    insertValuesAndNotifyMediaStore();
                }
            }
        });

        /**
         * Stop session
         */
        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sessionCompleted = true;
                mSessionStarted = false;
                cancelBtn.setVisibility(View.GONE);
                timer.setBackgroundResource(R.drawable.rounded_start_button);
                stopBtn.setVisibility(View.GONE);
                timer.setVisibility(View.VISIBLE);
                //Discable notifications
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mService.disableNotificationOfSession();
                    }
                });
                TimeBuff += MillisecondTime;
                handler.removeCallbacks(runnable);
                MillisecondTime = 0L;
                StartTime = 0L;
                TimeBuff = 0L;
                UpdateTime = 0L;
                Seconds = 0;
                Minutes = 0;
                timer.setText(R.string.timer_start);
                tsLong = System.currentTimeMillis();
                initiatePopupWindowModified();
                insertValuesAndNotifyMediaStore();
            }
        });

        /**
         * Angle correction popup
         */
        iv_angle_correction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
                builder.setTitle("Correct Angle");
                builder.setMessage("please enter the expected angle");
                final EditText editText = new EditText(MonitorActivity.this);
                editText.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                editText.setLayoutParams(lp);
                builder.setView(editText);
                builder.setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!editText.getText().toString().equals("")) {
                            try {
                                if (mSessionStarted) {
                                    angleCorrection = Integer.parseInt(editText.getText().toString());
                                    angleCorrected = true;
                                    maxAngle = angleCorrection;
                                    minAngle = angleCorrection;
                                    angleCorrection -= currentAngle;
                                    currentAngle += angleCorrection;


                                }
                            } catch (NumberFormatException e) {

                            }
                        }
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                if (mSessionStarted) {
                    builder.show();
                } else {
                    showToast("Please start session!");
                }
            }
        });
        MillisecondTime = 0L;
        StartTime = 0L;
        TimeBuff = 0L;
        UpdateTime = 0L;
        Seconds = 0;
        Minutes = 0;
        time.setText("Session time:   00 : 00");
        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter!=null && !bluetoothAdapter.isEnabled()) {
            startBleRequest();
        }

        iv_back_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(MonitorActivity.this,R.anim.fade_in);
                iv_back_monitor.setAnimation(aniFade);
                finish();
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(device_state);
        intentFilter.addAction(bluetooth_state);
        intentFilter.addAction(usb_state);
        intentFilter.addAction(battery_percent);
        intentFilter.addAction(session_data);
        intentFilter.addAction(device_disconnected_firmware);
        registerReceiver(session_data_receiver,intentFilter);

        Intent mIntent = new Intent(this, PheezeeBleService.class);
        bindService(mIntent,mConnection, BIND_AUTO_CREATE);

        creatGraphView();
    }


    public void startBleRequest(){
        showToast("Bluetooth Disabled");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    /**
     * Inserts the summary values in files and also tells the media to scan the files for visibility when connected to the laptop.
     */
    private void insertValuesAndNotifyMediaStore() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                try {


                    outputStream_session_sessiondetails.write("Angle-Corrected : ".concat(String.valueOf(angleCorrection)).getBytes());

                    outputStream_session_sessiondetails.write("\n\n\n".getBytes());
                    outputStream_session_sessiondetails.write("Session Details".getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Session Stop Pressed".getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Max Angle:".concat(String.valueOf(maxAngle)).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Min Angle:".concat(String.valueOf(minAngle)).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Max Emg:".concat(String.valueOf(maxEmgValue)).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Hold Time:".concat(holdTimeValue).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Num of Reps:".concat(Repetitions.getText().toString()).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Session Time:".concat(time.getText().toString()).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                    outputStream_session_sessiondetails.write("Active Time:".concat(tv_action_time.getText().toString()).getBytes());
                    outputStream_session_sessiondetails.write("\n".getBytes());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[]{file_session_emgdata.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[]{file_session_romdata.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
                MediaScannerConnection.scanFile(
                        getApplicationContext(),
                        new String[]{file_session_sessiondetails.getAbsolutePath()},
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {
//                        Log.v("grokkingandroid",
//                                "file " + path + " was scanned seccessfully: " + uri);
                            }
                        });
            }
        });

    }

    /**
     * Updates the view of gain to default
     */
    private void updateGainView() {
        btn_emg_decrease_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
        btn_emg_increase_gain.setBackgroundResource(R.drawable.monitor_gain_btn);
    }


    public void startSession() {
        updateGainView();
        error_device_dialog=null;
        mSessionStarted = true;
        sessionCompleted = false;
        ui_rate = 0;
        angleCorrected = false;
        angleCorrection = 0;
        emgJsonArray = new JSONArray();
        romJsonArray = new JSONArray();
        maxAngle = 0;minAngle = 360;maxEmgValue = 0;
        can_beeep_max=true;can_beep_min=true;
        creatGraphView();
        timer.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.VISIBLE);
        stopBtn.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    mService.sendBodypartDataToDevice(bodypart, body_orientation, patientname, exercise_position, muscle_position, bodypart_position);
                }
            }, 100);
            rawdata_timestamp = Calendar.getInstance().getTime();
            android.text.format.DateFormat df = new android.text.format.DateFormat();
//            String s = rawdata_timestamp.toString().substring(0, 19);
            String s = String.valueOf(DateFormat.format("yyyy-MM-dd hh-mm-ssa", rawdata_timestamp));
            String child = patientname + patientid;
            file_dir_session_emgdata = new File(Environment.getExternalStorageDirectory() + "/Pheezee/files/EmgData/" + child + "/sessiondata/", s);
            if (!file_dir_session_emgdata.exists()) {
                file_dir_session_emgdata.mkdirs();
            }
            file_session_emgdata = new File(file_dir_session_emgdata, "emg.txt");
            file_session_romdata = new File(file_dir_session_emgdata, "rom.txt");
            file_session_sessiondetails = new File(file_dir_session_emgdata, "sessiondetails.txt");
            try {
                file_session_emgdata.createNewFile();
                file_session_romdata.createNewFile();
                file_session_sessiondetails.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                outputStream_session_emgdata = new FileOutputStream(file_session_emgdata, true);
                outputStream_session_romdata = new FileOutputStream(file_session_romdata, true);
                outputStream_session_sessiondetails = new FileOutputStream(file_session_sessiondetails, true);

                //emg file output stream
                outputStream_session_emgdata.write("EMG".getBytes());
                outputStream_session_emgdata.write("\n".getBytes());

                //rom file output stream
                outputStream_session_romdata.write("EMG".getBytes());
                outputStream_session_romdata.write("\n".getBytes());


                //sessiondetails file output stream
                outputStream_session_sessiondetails.write("Patient Name : ".getBytes());
                outputStream_session_sessiondetails.write(patientname.getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
                outputStream_session_sessiondetails.write("Patient Id: ".concat(patientid).getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
                outputStream_session_sessiondetails.write("Orientation-Bodypart-ExerciseName-MuscleName : ".concat(orientation + "-" + bodypart + "-" + str_exercise_name+"-"+str_muscle_name).getBytes());
                outputStream_session_sessiondetails.write("\n".getBytes());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            StartTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * handler for session time incrimental
     */
    public Runnable runnable = new Runnable() {
        public void run() {
            MillisecondTime = SystemClock.uptimeMillis() - StartTime;
            UpdateTime = TimeBuff + MillisecondTime;
            Seconds = (int) (UpdateTime / 1000);
            Minutes = Seconds / 60;
            Seconds = Seconds % 60;
            timeText = "Session time:   " + String.format("%02d", Minutes) + " : " + String.format("%02d", Seconds);
            time.setText(timeText);
            handler.postDelayed(this, 0);
        }
    };

    /**
     * Handler to post the values received from device in the view
     */
    @SuppressLint("HandlerLeak")
    public final Handler myHandler = new Handler() {
        public void handleMessage(Message message) {
            try {
                if (mSessionStarted) {
                    int angleDetected = 0, num_of_reps = 0, hold_time_minutes, hold_time_seconds, active_time_minutes, active_time_seconds;
                    int emg_data, error_device = 0;
                    byte[] sub_byte;
                    sub_byte = (byte[]) message.obj;
                    if (sub_byte != null) {
                        error_device = sub_byte[10] & 0xFF;
                        if (error_device == 0) {
                            emg_data = ByteToArrayOperations.getAngleFromData(sub_byte[0], sub_byte[1]);
                            angleDetected = ByteToArrayOperations.getAngleFromData(sub_byte[2], sub_byte[3]);
                            if (ui_rate == 0) {
                                minAngle = angleDetected;
                                maxAngle = angleDetected;
                            }
                            num_of_reps = ByteToArrayOperations.getNumberOfReps(sub_byte[4], sub_byte[5]);
                            hold_time_minutes = sub_byte[6];
                            hold_time_seconds = sub_byte[7];
                            active_time_minutes = sub_byte[8];
                            active_time_seconds = sub_byte[9];
                            currentAngle = angleDetected;
                            String repetitionValue = "" + num_of_reps;
                            Repetitions.setText(repetitionValue);
                            String minutesValue = "" + hold_time_minutes, secondsValue = "" + hold_time_seconds;
                            if (hold_time_minutes < 10)
                                minutesValue = "0" + hold_time_minutes;
                            if (hold_time_seconds < 10)
                                secondsValue = "0" + hold_time_seconds;
                            holdTimeValue = minutesValue + "m: " + secondsValue + "s";
                            if (angleCorrected) {
                                angleDetected += angleCorrection;
                                arcViewInside.setMaxAngle(angleDetected);
                            } else {
                                arcViewInside.setMaxAngle(angleDetected);
                            }
                            romJsonArray.put(angleDetected);

//            //Beep
                            if (!str_max_angle_selected.equals("")) {
                                int x = Integer.parseInt(str_max_angle_selected);
                                if (angleDetected < x && !can_beeep_max) {
                                    can_beeep_max = true;
                                }
                            }

                            if (!str_min_angle_selected.equals("")) {
                                int x = Integer.parseInt(str_min_angle_selected);
                                if (angleDetected > x && !can_beep_min) {
                                    can_beep_min = true;
                                }
                            }
                            try {
                                outputStream_session_romdata = new FileOutputStream(file_session_romdata, true);
                                outputStream_session_romdata.write(String.valueOf(angleDetected).getBytes());
                                outputStream_session_romdata.write(",".getBytes());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            try {
                                outputStream_session_romdata.flush();
                                outputStream_session_romdata.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            LinearLayout.LayoutParams params;
                            params = (LinearLayout.LayoutParams) emgSignal.getLayoutParams();
                            ++ui_rate;
                            lineData.addEntry(new Entry((float) ui_rate / 50, emg_data), 0);
                            try {
                                outputStream_session_emgdata = new FileOutputStream(file_session_emgdata, true);
                                outputStream_session_emgdata.write(String.valueOf(emg_data).getBytes());
                                outputStream_session_emgdata.write(",".getBytes());
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            emgJsonArray.put(emg_data);
                            try {
                                outputStream_session_emgdata.flush();
                                outputStream_session_emgdata.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                            maxEmgValue = maxEmgValue < emg_data ? emg_data : maxEmgValue;
                            if (maxEmgValue == 0)
                                maxEmgValue = 1;
                            tv_max_emg.setText(String.valueOf(maxEmgValue));
                            params.height = (int) (((View) emgSignal.getParent()).getMeasuredHeight() * emg_data / maxEmgValue);
                            EMG.setText(Integer.toString(emg_data).concat(getResources().getString(R.string.emg_unit)));
                            lineChart.notifyDataSetChanged();
                            lineChart.invalidate();
                            lineChart.getXAxis();
                            lineChart.getAxisLeft();
                            lineChart.getAxisLeft().setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return (int) value + getResources().getString(R.string.emg_unit);
                                }
                            });
                            if (UpdateTime / 1000 > 3)
                                lineChart.setVisibleXRangeMaximum(5f);
                            lineChart.moveViewToX((float) ui_rate / 50);


                            //Beep
                            if (!str_max_angle_selected.equals("")) {
                                int x = Integer.parseInt(str_max_angle_selected);
                                if (angleDetected > x && can_beeep_max) {
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                            //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,150);
                                            toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 150);
                                        }
                                    });
                                    can_beeep_max = false;
                                }
                            }

                            if (!str_min_angle_selected.equals("")) {
                                int x = Integer.parseInt(str_min_angle_selected);
                                if (angleDetected <= x && can_beep_min) {
                                    new Handler().post(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToneGenerator toneGen1 = new ToneGenerator(AudioManager.STREAM_MUSIC, 100);
                                            //toneGen1.startTone(ToneGenerator.TONE_PROP_BEEP,150);
                                            toneGen1.startTone(ToneGenerator.TONE_PROP_ACK, 150);
                                        }
                                    });
                                    can_beep_min = false;
                                }
                            }
                            maxAngle = maxAngle < angleDetected ? angleDetected : maxAngle;
                            tv_max_angle.setText(String.valueOf(maxAngle));
                            minAngle = minAngle > angleDetected ? angleDetected : minAngle;
                            tv_min_angle.setText(String.valueOf(minAngle));
//            }
                            emgSignal.setLayoutParams(params);
                            holdTime.setText(holdTimeValue);
                            minutesValue = "" + active_time_minutes;
                            secondsValue = "" + active_time_seconds;
                            if (active_time_minutes < 10)
                                minutesValue = "0" + active_time_minutes;
                            if (active_time_seconds < 10)
                                secondsValue = "0" + active_time_seconds;
                            tv_action_time.setText(minutesValue + "m: " + secondsValue + "s");

                            if (num_of_reps >= repsselected && repsselected != 0 && !sessionCompleted) {
                                sessionCompleted = true;
                                openSuccessfullDialogAndCloseSession();
                            }
                        } else {
                            if(error_device_dialog==null)
                                errorInDeviceDialog();
                        }
                    }
                }
            } catch (IndexOutOfBoundsException e){
                e.printStackTrace();
            }
        }
    };


    /**
     * Close session in 2000ms once the session goal is reached
     */
    private void openSuccessfullDialogAndCloseSession() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MonitorActivity.this);
        builder.setTitle("Session Completed");
        builder.setMessage("You have reached the goal.");
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        stopBtn.performClick();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.cancel();
            }
        }, 2000);
    }

    private void errorInDeviceDialog() {
        String title = "Error";
        String message = "Please make sure the device is placed properly as per the device placement pictures and make sure the wire connecting the two modules is not stretched. Reset the device to start again.";
        AlertDialog.Builder error_device = new AlertDialog.Builder(MonitorActivity.this);
        error_device.setTitle(title);
        error_device.setCancelable(false);
        error_device.setMessage(message);
        error_device.setPositiveButton("ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                
            }
        });
        error_device_dialog = error_device.create();
        error_device_dialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                mService.disableNotificationOfSession();
            }
        });
        mSessionStarted=false;
        if(isBound){
            unbindService(mConnection);
        }
        unregisterReceiver(session_data_receiver);
    }

    /**
     * Refreshes the line graph
     */
    private void creatGraphView() {
        lineChart.setHardwareAccelerationEnabled(true);
        dataPoints = new ArrayList<>();
        dataPoints.add(new Entry(0, 0));
        lineDataSet = new LineDataSet(dataPoints, "Emg Graph");
        lineDataSet.setDrawCircles(false);
        lineDataSet.setValueTextSize(0);
        lineDataSet.setDrawValues(false);
        lineDataSet.setColor(getResources().getColor(R.color.pitch_black));
        lineData = new LineData(lineDataSet);
        lineDataNew = new LineData(lineDataSet);    //for 30000
        lineChart.getXAxis();
        lineChart.getXAxis().setAxisMinimum(0f);
        lineChart.getAxisLeft().setSpaceTop(60f);
        lineChart.getAxisRight().setSpaceTop(60f);
        lineChart.getAxisRight().setDrawLabels(false);
        lineChart.getAxisLeft().setStartAtZero(false);
        lineChart.getAxisLeft().setDrawGridLines(false);
        lineChart.getXAxis().setDrawGridLines(false);
        lineChart.getXAxis().setDrawAxisLine(false);
        lineChart.setHorizontalScrollBarEnabled(true);
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.setScaleXEnabled(true);
        lineChart.fitScreen();
        lineChart.setData(lineData);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initiatePopupWindowModified() {
        String sessionNo = tv_session_no.getText().toString();
        String sessiontime = time.getText().toString().substring(16);
        String actiontime = tv_action_time.getText().toString();

        //testing with empty emg and rom array
//        emgJsonArray = new JSONArray();
//        romJsonArray = new JSONArray();

        SessionSummaryPopupWindow window = new SessionSummaryPopupWindow(this, maxEmgValue, sessionNo, maxAngle, minAngle, orientation, bodypart,
                json_phizioemail, sessiontime, actiontime, holdTime.getText().toString(), Repetitions.getText().toString(),
                angleCorrection, patientid, patientname, tsLong, bodyorientation, getIntent().getStringExtra("dateofjoin"), exercise_position,bodypart_position,
                str_muscle_name,str_exercise_name,str_min_angle_selected,str_max_angle_selected,str_max_emg_selected,repsselected);
        window.showWindow();
        window.storeLocalSessionDetails(emgJsonArray,romJsonArray);
        repository.getPatientSessionNo(patientid);
        window.setOnSessionDataResponse(new MqttSyncRepository.OnSessionDataResponse() {
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

    ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            isBound = true;
            PheezeeBleService.LocalBinder mLocalBinder = (PheezeeBleService.LocalBinder)service;
            mService = mLocalBinder.getServiceInstance();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            mService = null;
        }
    };


    /**
     * show toask
     *
     * @param message
     */
    private void showToast(String message) {
        Toast.makeText(MonitorActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSessionNumberResponse(String sessionnumber) {
        tv_session_no.setText(sessionnumber);
    }


    BroadcastReceiver session_data_receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equalsIgnoreCase(device_state)){

                boolean device_status = intent.getBooleanExtra(device_state,false);
                if(device_status){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deviceState = true;
                            if(mSessionStarted) {
                                mService.sendBodypartDataToDevice(bodypart, body_orientation, patientname, exercise_position, muscle_position, bodypart_position);
                            }
                            if(deviceDisconnectedDialog!=null) {
                                deviceDisconnectedDialog.dismiss();
                            }
                        }
                    },2000);
                }else {
                    deviceState = false;
                    if(deviceDisconnectedDialog!=null) {
                        if (!deviceDisconnectedDialog.isShowing())
                            deviceDisconnectedPopup(mSessionStarted);
                    }else {
                        deviceDisconnectedPopup(mSessionStarted);
                    }
                }
            }else if(action.equalsIgnoreCase(bluetooth_state)){
                boolean ble_state = intent.getBooleanExtra(bluetooth_state,false);
                if(ble_state){
                    showToast("Bluetooth Enabled");
                }else {
                    startBleRequest();
                }
            }else if(action.equalsIgnoreCase(usb_state)){
                boolean usb_status = intent.getBooleanExtra(usb_state,false);
                Message msg = new Message();
                if(usb_status){
                    usbState = true;
                    if(usbPluggedInDialog!=null) {
                        if(!usbPluggedInDialog.isShowing())
                            usbConnectedDialog(mSessionStarted);
                    }else {
                        usbConnectedDialog(mSessionStarted);
                    }
                }else {
                    usbState = false;
                    if(usbPluggedInDialog!=null) {
                        usbPluggedInDialog.dismiss();
                    }
                    if(mSessionStarted){
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mService.sendBodypartDataToDevice(bodypart, body_orientation, patientname, exercise_position, muscle_position, bodypart_position);
                            }
                        },500);
                    }
                }
            }else if(action.equalsIgnoreCase(battery_percent)){
                String percent = intent.getStringExtra(battery_percent);
            }else if(action.equalsIgnoreCase(session_data)){
                if(mSessionStarted) {
                    Message message = new Message();
                    message.obj = mService.getSessionData().obj;
                    myHandler.sendMessage(message);
                }
            }else if(action.equalsIgnoreCase(device_disconnected_firmware)){
                boolean device_disconnected_status = intent.getBooleanExtra(device_disconnected_firmware,false);
                if(device_disconnected_status){
                    showToast("The device has been deactivated");
                    Intent i = new Intent(MonitorActivity.this, PatientsView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
            }
        }
    };
}
