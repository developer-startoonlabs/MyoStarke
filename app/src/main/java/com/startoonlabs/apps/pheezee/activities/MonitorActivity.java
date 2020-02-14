package com.startoonlabs.apps.pheezee.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.fragments.SmileyMonitoringFragment;
import com.startoonlabs.apps.pheezee.fragments.StandardGoldTeachFragment;
import com.startoonlabs.apps.pheezee.fragments.StarMonitorFragment;
import com.startoonlabs.apps.pheezee.services.PheezeeBleService;
import com.startoonlabs.apps.pheezee.utils.TakeScreenShot;

import org.json.JSONException;
import org.json.JSONObject;

import static com.startoonlabs.apps.pheezee.utils.PackageTypes.ACHEDAMIC_TEACH_PLUS;
import static com.startoonlabs.apps.pheezee.utils.PackageTypes.GOLD_PLUS_PACKAGE;
import static com.startoonlabs.apps.pheezee.utils.PackageTypes.STANDARD_PACKAGE;

public class MonitorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private int phizio_packagetype = 0;
    int selected_theme = 0;
    //session inserted on server
    ImageView iv_back_monitor, iv_theme_chooser;
    private String str_body_orientation="",json_phizioemail = "";
    TextView tv_snap;
    int REQUEST_ENABLE_BT = 1;
    SharedPreferences sharedPreferences;
    JSONObject json_phizio = new JSONObject();

    private boolean mSessionStarted = false, isBound = false;
    PheezeeBleService mService;
    String patientid, patientname;
    BluetoothAdapter bluetoothAdapter;
    BluetoothManager mBluetoothManager;
    Fragment fragment;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monitor_updated);
        fragmentManager = getSupportFragmentManager();
        iv_back_monitor = findViewById(R.id.iv_back_monitor);
        tv_snap = findViewById(R.id.snap_monitor);
        iv_theme_chooser = findViewById(R.id.theme_chooser);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(sharedPreferences.getString("phiziodetails", ""));
            json_phizioemail = json_phizio.getString("phizioemail");
            phizio_packagetype = json_phizio.getInt("packagetype");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        patientid = getIntent().getStringExtra("patientId");
        patientname = getIntent().getStringExtra("patientName");



        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        iv_back_monitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation aniFade = AnimationUtils.loadAnimation(MonitorActivity.this,R.anim.fade_in);
                iv_back_monitor.setAnimation(aniFade);
                finish();
            }
        });
        tv_snap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TakeScreenShot screenShot = new TakeScreenShot(MonitorActivity.this, patientname, patientid);
                screenShot.takeScreenshot(null);
                showToast("Took Screenshot");
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

        visibilityChangesBasedOnPackageType();
        registerForContextMenu(iv_theme_chooser);

        mBluetoothManager = (BluetoothManager)getSystemService(BLUETOOTH_SERVICE);
        bluetoothAdapter = mBluetoothManager.getAdapter();
        if (bluetoothAdapter!=null && !bluetoothAdapter.isEnabled()) {
            startBleRequest();
        }

        Intent mIntent = new Intent(this, PheezeeBleService.class);
        bindService(mIntent,mConnection, BIND_AUTO_CREATE);

        standardGoldTeachFragment();

    }

    private void visibilityChangesBasedOnPackageType() {
        if(phizio_packagetype!=STANDARD_PACKAGE){
            tv_snap.setVisibility(View.VISIBLE);
        }
        if(phizio_packagetype==GOLD_PLUS_PACKAGE || phizio_packagetype==ACHEDAMIC_TEACH_PLUS){
            iv_theme_chooser.setVisibility(View.VISIBLE);
        }
    }

    public void standardGoldTeachFragment(){
        if(selected_theme!=1) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new StandardGoldTeachFragment();
            fragmentTransaction.replace(R.id.fragment_monitor_container, fragment);
            fragmentTransaction.commit();
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            selected_theme = 1;
        }
    }
    public void smileyFragment(){
        if(selected_theme!=2) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new SmileyMonitoringFragment();
            fragmentTransaction.replace(R.id.fragment_monitor_container, fragment);
            fragmentTransaction.commit();
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            selected_theme = 2;
        }
    }

    public void starFragment(){
        if(selected_theme!=3) {
            fragmentTransaction = fragmentManager.beginTransaction();
            fragment = new StarMonitorFragment();
            fragmentTransaction.replace(R.id.fragment_monitor_container, fragment);
            fragmentTransaction.commit();
            FragmentManager fm = getSupportFragmentManager();
            for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
                fm.popBackStack();
            }
            selected_theme = 3;
        }
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


    public void startBleRequest(){
        showToast("Bluetooth Disabled");
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    public void increaseGain(){
        if(mService!=null){
            mService.increaseGain();
        }
    }

    public void decreaseGain(){
        if(mService!=null){
            mService.decreaseGain();
        }
    }

    public void disableNotificationOfSession(){
        if(mService!=null){
            mSessionStarted = false;
            mService.disableNotificationOfSession();
        }
    }

    public void sendBodypartDataToDevice(String bodypart, int body_orientation, String patientname, int exercise_position,
                                         int muscle_position, int bodypart_position, int orientation_position){
        if(mService!=null){
            mSessionStarted = true;
            mService.sendBodypartDataToDevice(bodypart, body_orientation, patientname, exercise_position,
                    muscle_position, bodypart_position, orientation_position);
        }
    }

    public Message getSessionData(){
        if(mService!=null) {
            Message msg = mService.getSessionData();
            return msg;
        }
        return null;
    }

    public void showPopup(View v){
        if(!mSessionStarted) {
            PopupMenu popup = new PopupMenu(this, v);
            MenuInflater inflater = popup.getMenuInflater();
            inflater.inflate(R.menu.theme_chooser, popup.getMenu());
            popup.setOnMenuItemClickListener(this);
            popup.show();
        }else {
            showToast("Stop the session to change theme!");
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    /**
     * show toask
     *
     * @param message
     */
    private void showToast(String message) {
        Toast.makeText(MonitorActivity.this, message, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_normal_theme:{
                Log.i("Here","1");
                standardGoldTeachFragment();
                return true;
            }

            case R.id.menu_smiley_theme:{
                smileyFragment();
                Log.i("Here","2");
                return true;
            }

            case R.id.menu_star_theme:{
                starFragment();
                Log.i("Here","3");
                return true;
            }

            default:{
                return false;
            }
        }
    }

    public void getBasicDeviceInfo() {
        if(mService!=null){
            mService.gerDeviceBasicInfo();
        }
    }
}
