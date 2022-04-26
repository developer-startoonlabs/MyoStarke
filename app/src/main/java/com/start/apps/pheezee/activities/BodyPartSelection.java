package com.start.apps.pheezee.activities;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import start.apps.pheezee.R;
import com.start.apps.pheezee.adapters.BodyPartSelectionRecyclerViewAdapter;
import com.start.apps.pheezee.classes.BodyPartSelectionModel;
import com.start.apps.pheezee.classes.BodyPartWithMmtSelectionModel;
import com.start.apps.pheezee.services.PheezeeBleService;

import java.util.ArrayList;

import static com.start.apps.pheezee.activities.PatientsView.phizio_packagetype;
import static com.start.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;

public class BodyPartSelection extends AppCompatActivity {
    //Drawable arry for the body part selection


    RecyclerView bodyPartRecyclerView;

    //Adapter for body part recycler view
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;

    ArrayList<BodyPartWithMmtSelectionModel> bodyPartWithMmtSelectionModels;

    //Floating action button for done
    ImageView iv_back_body_part_selection;
    String[] string;
    private String str_orientation, str_exercise_name, str_muscle_name, str_body_orientation, str_body_part, str_max_emg_selected="", min_angle_selected="", max_angle_selected="";
    private int int_repsselected = 0, exercise_selected_postion=-1, body_part_selected_position=-1, muscle_selected_position=-1;

//    GridLayoutManager manager;
    RecyclerView.LayoutManager manager;
    BodyPartSelectionRecyclerViewAdapter adapter;
    boolean isBound = false;
    PheezeeBleService mService;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);
        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);

        bodyPartRecyclerView = findViewById(R.id.bodyPartRecyclerView);
        bodyPartRecyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();
        bodyPartWithMmtSelectionModels = new ArrayList<>();
        string = getResources().getStringArray(R.array.bodyPartName);
        for (int i=0;i<string.length;i++){
            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(string[i]);
            bodyPartWithMmtSelectionModels.add(bp);
        }
        adapter = new BodyPartSelectionRecyclerViewAdapter(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bodyPartRecyclerView.setAdapter(adapter);
            }
        },50);


        //Going back to the previous activity
        iv_back_body_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(BodyPartSelection.this,R.anim.fade_in);
                iv_back_body_part_selection.setAnimation(aniFade);
                finish();
            }
        });


        adapter.onSetOptionsSelectedListner(new BodyPartSelectionRecyclerViewAdapter.onBodyPartOptionsSelectedListner() {
            @Override
            public void onBodyPartSelected(String bodypart) {
                str_body_part = bodypart;
            }

            @Override
            public void onOrientationSelected(String orientation) {
                str_orientation = orientation;
            }

            @Override
            public void onBodyOrientationSelected(String body_orientation) {
                str_body_orientation = body_orientation;
            }

            @Override
            public void onExerciseNameSelected(String exercise_name) {
                str_exercise_name = exercise_name;
            }

            @Override
            public void onMuscleNameSelected(String muscle_name, int position) {
                str_muscle_name = muscle_name;
                muscle_selected_position = position;
            }

            @Override
            public void onGoalSelected(int reps) {
                if(phizio_packagetype!=STANDARD_PACKAGE)
                    int_repsselected = reps;
            }

            @Override
            public void onMaxEmgUpdated(String max_emg_updated) {
                if(phizio_packagetype!=STANDARD_PACKAGE)
                    str_max_emg_selected = max_emg_updated;
            }

            @Override
            public void onMaxAngleUpdated(String max_angle_updated) {
                if(phizio_packagetype!=STANDARD_PACKAGE)
                    max_angle_selected = max_angle_updated;
            }

            @Override
            public void onMinAngleUpdated(String min_angle_updated) {
                if(phizio_packagetype!=STANDARD_PACKAGE)
                    min_angle_selected = min_angle_updated;
            }

            @Override
            public void onBodyPartSelectedPostion(int position) {
                body_part_selected_position = position;
            }

            @Override
            public void onExerciseSelectedPostion(int position) {
                exercise_selected_postion = position;
            }
        });

        Intent intent = new Intent(this, PheezeeBleService.class);
        bindService(intent,mConnection,BIND_AUTO_CREATE);
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

    @Override
    protected void onResume() {
        if(adapter!=null){
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bodyPartRecyclerView.setAdapter(adapter);
                }
            },50);
            adapter.setPositionToInitial();
        }

        reinitializeStatics();
        super.onResume();
    }

    /**
     * This method is called when pressed done. Check weather any feild is not selected
     * @param view
     */
    public void startMonitorSession(View view) {
        if(mService!=null){
            boolean device_state = mService.getDeviceState();
            boolean usb_state = mService.getUsbState();
            int device_disconnected_status = mService.getDeviceDeactivationStatus();
            if(device_state && !usb_state && device_disconnected_status==0) {
                if (isValid()) {
                    int body_orientation = 0;
                    if (str_body_orientation.equalsIgnoreCase("sit")) body_orientation = 2;
                    else if (str_body_orientation.equalsIgnoreCase("stand")) body_orientation = 1;
                    else body_orientation = 3;
                    Intent intent = new Intent(BodyPartSelection.this, MonitorActivity.class);
                    //To be started here i need to putextras in the intents and send them to the moitor activity
                    intent.putExtra("deviceMacAddress", getIntent().getStringExtra("deviceMacAddress"));
                    intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
                    intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
                    intent.putExtra("exerciseType", str_body_part);
                    intent.putExtra("orientation", str_orientation);
                    intent.putExtra("bodyorientation", str_body_orientation);
                    intent.putExtra("body_orientation", body_orientation);
                    intent.putExtra("dateofjoin", getIntent().getStringExtra("dateofjoin"));
                    intent.putExtra("repsselected", int_repsselected);
                    intent.putExtra("exercisename", str_exercise_name);
                    intent.putExtra("musclename", str_muscle_name);
                    intent.putExtra("maxemgselected", str_max_emg_selected);
                    intent.putExtra("maxangleselected", max_angle_selected);
                    intent.putExtra("minangleselected", min_angle_selected);
                    intent.putExtra("exerciseposition", exercise_selected_postion);
                    intent.putExtra("bodypartposition", body_part_selected_position);
                    intent.putExtra("muscleposition", muscle_selected_position);
                    intent.putExtra("issceduled",false);
                    startActivity(intent);

                } else {
                    showToast(getInvalidMessage());
                }
            }else {
                if(usb_state){
                    showToast("Please remove USB from Pheezee to continue..");
                }else if(device_disconnected_status==1){
                    showToast("Device has been deactivated");
                    Intent i = new Intent(BodyPartSelection.this, PatientsView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
                else {
                    showToast("Please connect Pheezee!");
                    Intent i = new Intent(BodyPartSelection.this, PatientsView.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(i);
                }
            }
        }else {
            showToast("Please connect Pheezee!");
            Intent i = new Intent(BodyPartSelection.this, PatientsView.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
        }

    }

    public boolean isValid(){
        if(str_body_part!=null && str_orientation!=null && str_body_orientation!=null && str_exercise_name!=null && str_muscle_name!=null){
            return true;
        }
        else {
            return false;
        }
    }

    public String getInvalidMessage(){
        if(str_body_part==null){
            return "Please select body part";
        }
        else if (str_orientation==null){
            return "Please select body part side";
        }
        else if (str_body_orientation==null){
            return "Please select body position";
        }else if (str_exercise_name==null){
            return "Please select Movement name";
        }
        else if (str_muscle_name==null){
            return "Please select muscle name";
        }else {
            return "Please select the exercise";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mConnection);
    }

    /**
     * Reinitialize static variables
     */
    public void reinitializeStatics() {
        str_orientation=null; str_exercise_name=null; str_muscle_name=null; str_body_orientation=null; str_body_part=null;
        str_max_emg_selected="";min_angle_selected=""; max_angle_selected="";
        int_repsselected = 0; exercise_selected_postion=-1; body_part_selected_position=-1;
    }

    /**
     * Shows toast
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
