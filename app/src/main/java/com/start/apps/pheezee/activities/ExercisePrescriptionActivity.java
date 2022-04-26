package com.start.apps.pheezee.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import start.apps.pheezee.R;
import com.start.apps.pheezee.adapters.ExercisePrescriptionRecyclerViewAdapter;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.room.Entity.SceduledSession;
import com.start.apps.pheezee.services.PheezeeBleService;

import java.util.List;

public class ExercisePrescriptionActivity extends AppCompatActivity {
    LiveData<List<SceduledSession>> sessions;
    RecyclerView mRecyclerView;
    ExercisePrescriptionRecyclerViewAdapter mAdapter;
    ImageView iv_back;
    String str_patient_name, str_patient_id, str_patient_dateofjoin;
    MqttSyncRepository repository;
    Button btn_confirm;
    boolean isBound = false;
    PheezeeBleService mService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise_prescription);
        repository = new MqttSyncRepository(getApplication());
        iv_back = findViewById(R.id.iv_back_exercise_prescription);
        btn_confirm = findViewById(R.id.btn_confirm_exercise_prescription);
        str_patient_id = getIntent().getStringExtra("patientId");
        str_patient_name = getIntent().getStringExtra("patientName");
        str_patient_dateofjoin = getIntent().getStringExtra("dateofjoin");

        mRecyclerView = findViewById(R.id.exercise_prescription_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new ExercisePrescriptionRecyclerViewAdapter(this);
        mRecyclerView.setAdapter(mAdapter);

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(ExercisePrescriptionActivity.this,R.anim.fade_in);
                iv_back.setAnimation(aniFade);
                finish();
            }
        });

        sessions = repository.getAllSceduledSessions(str_patient_id);
        if(sessions.getValue()!=null && sessions.getValue().size()>0){
            Log.i("SIZE", String.valueOf(sessions.getValue().size()));
            mAdapter.setNotes(sessions.getValue());
        }
        sessions.observe(this,new Observer<List<SceduledSession>>() {
            @Override
            public void onChanged(List<SceduledSession> sessions) {
                Log.i("size", String.valueOf(sessions.size()));
                mAdapter.setNotes(sessions);
            }
        });

        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mService!=null){
                    boolean device_state = mService.getDeviceState();
                    boolean usb_state = mService.getUsbState();
                    int device_disconnected_status = mService.getDeviceDeactivationStatus();
                    if(device_state && !usb_state && device_disconnected_status==0) {
                        Intent intent = new Intent(ExercisePrescriptionActivity.this, MonitorActivity.class);
                        intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
                        intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
                        intent.putExtra("dateofjoin",str_patient_dateofjoin);
                        intent.putExtra("issceduled",true);
                        startActivity(intent);
                    }else {
                        if(usb_state){
                            showToast("Please remove USB from Pheezee to continue..");
                        }else if(device_disconnected_status==1){
                            showToast("Device has been deactivated");
                            Intent i = new Intent(ExercisePrescriptionActivity.this, PatientsView.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                        }
                        else {
                            showToast("Please connect Pheezee!");
                            Intent i = new Intent(ExercisePrescriptionActivity.this, PatientsView.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(i);
                        }
                    }
                    }else {
                        showToast("Please connect Pheezee!");
                        Intent i = new Intent(ExercisePrescriptionActivity.this, PatientsView.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }


            }
        });

        Intent intent = new Intent(this, PheezeeBleService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
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

    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
