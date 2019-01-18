package com.example.sai.pheezeeapp.Activities;

import android.Manifest;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.Classes.MyGradeDialog;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MmtSession extends AppCompatActivity {


    SharedPreferences mmt_sharedpreferences;
    private static final int REQUEST_ENABLE_BT = 1;
    Dialog dialog;
    Drawable drawable_white,drawable_yellow;
    TextView tv_grade_given,tv_grade_option_1,tv_grade_option_2,tv_grade_option_2p,tv_grade_option_3,tv_grade_option_3p,tv_grade_option_4,tv_grade_option_5;
    MyGradeDialog myGradeDialog;
    public final int both_mpu_xyz_size = 12;
    public final int mpu_packet_sub_size = 24;
    public final int emg_data_size = 10;
    public final int emg_num_packets = 20;
    BluetoothGatt bluetoothGatt;
    int REQUEST_STORAGE = 1;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    //BluetoothManager mBluetoothManager;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    BluetoothGattCharacteristic mCharacteristic;
    File file_mpu, file_emgdata,file_dir_mpu,file_dir_emgdata;
    FileOutputStream outputStream_mpu, outputStream_emgdata;
    Date date;


    int i=0;

    String heldon;
    Long tsLong = 0L;

    Date rawdata_timestamp;


    CountDownTimer mCountdownTimer;

    public int maxAngle=0,minAngle=360,maxEmg=0;


    MqttHelper mqttHelper;

    String bodyPart="",grade="",patientId;

    JSONObject json_phizio;



    //All the mqtt topics

    String mqtt_publish_mqttSession  = "phizio/mmt/addpatientsession";
    String mqtt_publish_mqttSession_response  = "phizio/mmt/addpatientsession/response";





    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");



    Button btn_start_mmt_sesion;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mmt_session);

        mqttHelper = new MqttHelper(this);
        mmt_sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);

        try {
            json_phizio = new JSONObject(mmt_sharedpreferences.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }



        btn_start_mmt_sesion = (Button)findViewById(R.id.btn_start_mmt_session);


        btn_start_mmt_sesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_start_mmt_sesion.getText().equals("START")) {
                    btn_start_mmt_sesion.setText("STOP");
                    tsLong = System.currentTimeMillis();
                    rawdata_timestamp = Calendar.getInstance().getTime();
                    heldon = rawdata_timestamp.toString().substring(0, 19);
                    if (hasStoragePermissionGranted()) {
                        file_dir_mpu = new File(Environment.getExternalStorageDirectory() + "/Pheeze/files/MmtSession", "MpuData");
                        file_dir_emgdata = new File(Environment.getExternalStorageDirectory() + "/Pheeze/files/MmtSession", "EmgData");
                        if (!file_dir_emgdata.exists() && !file_dir_mpu.exists()) {
                            file_dir_emgdata.mkdirs();
                            file_dir_mpu.mkdirs();
                        }

                        file_mpu = new File(file_dir_mpu, "" + heldon + ".txt");
                        file_emgdata = new File(file_dir_emgdata, "" + heldon + ".txt");
                        try {
                            file_mpu.createNewFile();
                            file_emgdata.createNewFile();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        try {
                            outputStream_mpu = new FileOutputStream(file_mpu, true);
                            outputStream_mpu.write("MMT SESSION".getBytes());
                            outputStream_mpu.write("\n".getBytes());
                            outputStream_emgdata = new FileOutputStream(file_emgdata, true);
                            outputStream_emgdata.write("MMT SESSION".getBytes());
                            outputStream_emgdata.write("\n".getBytes());
                            outputStream_emgdata.write("EMG".getBytes());
                            outputStream_emgdata.write("\n".getBytes());
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                        if (characteristic1_service1_uuid.equals(mCharacteristic.getUuid()))
                            bluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                        if (descriptor_characteristic1_service1_uuid.equals(mBluetoothGattDescriptor.getUuid())) {


                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                    bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                                }
                            });

                        }

                    }
                } else {
                    btn_start_mmt_sesion.setText("START");
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                bluetoothGatt.setCharacteristicNotification(mCharacteristic, false);
                            }
                        });
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {

                                mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE);
                                bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                            }
                        });


                    MediaScannerConnection.scanFile(
                            getApplicationContext(),
                            new String[]{file_emgdata.getAbsolutePath()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v(heldon,
                                            "file " + path + " was scanned seccessfully: " + uri);
                                }
                            });
                    MediaScannerConnection.scanFile(
                            getApplicationContext(),
                            new String[]{file_mpu.getAbsolutePath()},
                            null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.v(heldon,
                                            "file " + path + " was scanned seccessfully: " + uri);
                                }
                            });

                    popUpRaitingWindow();
                }
            }


        });

        //body part initializing
        bodyPart = getIntent().getStringExtra("bodypart");
        patientId = getIntent().getStringExtra("patientid");

        Log.i("intent",bodyPart+patientId);

        //bluetooth related definations
        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(MmtSession.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //bluetoothGatt = BluetoothGattSingleton.getmInstance().getAdapter();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Toast.makeText(MmtSession.this, "GATT CLOSED", Toast.LENGTH_SHORT).show();
        }
        if (getIntent().hasExtra("deviceMacAddress"))
            remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (remoteDevice!=null)
                    bluetoothGatt = remoteDevice.connectGatt(MmtSession.this,true,callback);
            }
        });

    }

    private void popUpRaitingWindow() {

        //Patients current stare rating that can be choosen by the phiziotherapist.
        final View layout_grade_dialog = getLayoutInflater().inflate(R.layout.patient_grade_dialog_layout,null);
        TextView tv_cancel_dialog = layout_grade_dialog.findViewById(R.id.tv_cancel_grade_dialog);
        TextView tv_submit_dialog = layout_grade_dialog.findViewById(R.id.tv_submit_grade_dilog);
        TextView tv_minangle_mmt = layout_grade_dialog.findViewById(R.id.tv_minangle_mmt);
        TextView tv_maxangle_mmt = layout_grade_dialog.findViewById(R.id.tv_maxangle_mmt);
        tv_grade_given = layout_grade_dialog.findViewById(R.id.tv_grade_given);
        tv_grade_option_1 = layout_grade_dialog.findViewById(R.id.tv_grade_option_1);
        tv_grade_option_2 = layout_grade_dialog.findViewById(R.id.tv_grade_option_2);
        tv_grade_option_2p = layout_grade_dialog.findViewById(R.id.tv_grade_option_2p);
        tv_grade_option_3 = layout_grade_dialog.findViewById(R.id.tv_grade_option_3);
        tv_grade_option_3p = layout_grade_dialog.findViewById(R.id.tv_grade_option_3p);
        tv_grade_option_4 = layout_grade_dialog.findViewById(R.id.tv_grade_option_4);
        tv_grade_option_5 = layout_grade_dialog.findViewById(R.id.tv_grade_option_5);



        tv_minangle_mmt.setText(minAngle+"");
        tv_maxangle_mmt.setText(maxAngle+"");

         dialog = new Dialog(this);

        dialog.setContentView(layout_grade_dialog);
        dialog.setCancelable(false);
        dialog.show();

        tv_cancel_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                layout_grade_dialog.invalidate();
            }
        });

        tv_submit_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the required feilds and send it to the server need to do that later on

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String dateString = formatter.format(new Date(tsLong));

                if(!grade.equals("")) {
                    JSONObject object = new JSONObject();
                    try {
                        object.put("phizioemail",json_phizio.get("phizioemail"));
                        object.put("patientid",patientId);
                        object.put("bodypart",bodyPart);
                        object.put("maxangle",maxAngle);
                        object.put("minangle",minAngle);
                        object.put("heldon",dateString);
                        object.put("maxemg",maxEmg);
                        object.put("grade",grade);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    MqttMessage message = new MqttMessage();
                    message.setPayload(object.toString().getBytes());
                    mqttHelper.publishMqttTopic(mqtt_publish_mqttSession,message);
                    Log.i("JSON TO SEND",object.toString());
                    dialog.dismiss();
                }
                else {
                    Toast.makeText(MmtSession.this, "Please Select the grade..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void selectedGradeOfPatiet(View view){

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
           drawable_yellow = getDrawable(R.drawable.circular_textview_yellow);
        }
        switch (view.getId()){
            case R.id.tv_grade_option_1:{
                clearBackgroundTintOfAll();
                tv_grade_option_1.setBackground(drawable_yellow);
                tv_grade_given.setText("Trace");
                grade =1+"";
                break;
            }
            case R.id.tv_grade_option_2:{
                clearBackgroundTintOfAll();
                tv_grade_option_2.setBackground(drawable_yellow);
                tv_grade_given.setText("Poor");
                grade =2+"";
                break;
            }

            case R.id.tv_grade_option_2p:{
                clearBackgroundTintOfAll();
                tv_grade_option_2p.setBackground(drawable_yellow);
                tv_grade_given.setText("Poor +");
                grade =2+"+";
                break;
            }

            case R.id.tv_grade_option_3:{
                clearBackgroundTintOfAll();
                tv_grade_option_3.setBackground(drawable_yellow);
                tv_grade_given.setText("Fair");
                grade =3+"";
                break;
            }

            case R.id.tv_grade_option_3p:{
                clearBackgroundTintOfAll();
                tv_grade_option_3p.setBackground(drawable_yellow);
                tv_grade_given.setText("Fair +");
                grade =3+"+";
                break;
            }

            case R.id.tv_grade_option_4:{
                clearBackgroundTintOfAll();
                tv_grade_option_4.setBackground(drawable_yellow);
                tv_grade_given.setText("Good");
                grade =4+"";
                break;
            }

            case R.id.tv_grade_option_5:{
                clearBackgroundTintOfAll();
                tv_grade_option_5.setBackground(drawable_yellow);
                tv_grade_given.setText("Normal");
                grade =5+"";
                break;
            }
        }
    }

    private void clearBackgroundTintOfAll() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            drawable_white = getDrawable(R.drawable.circular_textview);
        }
        tv_grade_option_1.setBackground(drawable_white);
        tv_grade_option_2.setBackground(drawable_white);
        tv_grade_option_2p.setBackground(drawable_white);
        tv_grade_option_3.setBackground(drawable_white);
        tv_grade_option_3p.setBackground(drawable_white);
        tv_grade_option_4.setBackground(drawable_white);
        tv_grade_option_5.setBackground(drawable_white);
    }


    public BluetoothGattCallback callback = new BluetoothGattCallback() {
        @Override
        public void onPhyUpdate(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyUpdate(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onPhyRead(BluetoothGatt gatt, int txPhy, int rxPhy, int status) {
            super.onPhyRead(gatt, txPhy, rxPhy, status);
        }

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {

            if(newState == BluetoothProfile.STATE_CONNECTED){
                Log.i("GATT CONNECTED", "Attempting to start the service discovery");

                gatt.discoverServices();
            }
            else if (newState == BluetoothProfile.STATE_CONNECTING){
                Log.i("GATT DISCONNECTED", "GATT server is being connected");
            }

            else if(newState == BluetoothProfile.STATE_DISCONNECTING){
                Log.i("GATT DISCONNECTING","Gatt server disconnecting");
            }
            else if (newState == BluetoothProfile.STATE_DISCONNECTED){
                Log.i("GATT DISCONNECTED", "Gatt server disconnected");
            }

            super.onConnectionStateChange(gatt, status, newState);
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            if(status==BluetoothGatt.GATT_SUCCESS){
                mCharacteristic = gatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);
                Log.i("CHARACTERiSTIC", ""+mCharacteristic.getUuid());

                BluetoothGattDescriptor descriptor = mCharacteristic.getDescriptor(descriptor_characteristic1_service1_uuid);

                mBluetoothGattDescriptor = descriptor;
                bluetoothGatt = gatt;

            }
            //BluetoothGattCharacteristic characteristic = gatt.getService(service1_uuid).getCharacteristic(characteristic1_service1_uuid);
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())){
                Log.i("CHARACTERISTIC CHANGED","CHARACTERISTIC "+characteristic.getUuid()+" VALUE "+characteristic.getValue());
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())) {
                try {
                    outputStream_mpu = new FileOutputStream(file_mpu, true);
                    outputStream_emgdata = new FileOutputStream(file_emgdata, true);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }


                int ra[] = new int[both_mpu_xyz_size];
                int angleDetected;
                int temp = 20;

                int[] emg_data = new int[emg_data_size];
                byte[] b = characteristic.getValue();
                int x  = getIntValue(b[22],b[23]);
                byte sub_byte[] = new byte[b.length-2];
                int j = 2;
                for (int i = 0; i < sub_byte.length; i++, j++) {
                    sub_byte[i] = b[j];
                }
                byte header_main = b[0];
                byte header_sub = b[1];



                if (byteToStringHexadecimal(header_main).equals("AA")) {
                    if (byteToStringHexadecimal(header_sub).equals("01")) {
                        i++;
                        byte temp_array[] = new byte[emg_num_packets];
                        for (int i=0;i<emg_num_packets;i++){
                            temp_array[i] = sub_byte[i];
                        }
                        emg_data = constructEmgData(temp_array);
                        angleDetected = getAngleFromData(sub_byte[20],sub_byte[21]);
                        Log.i("angle",angleDetected+"");
                        maxAngle = maxAngle<angleDetected?angleDetected:maxAngle;
                        minAngle = minAngle>angleDetected?angleDetected:minAngle;

                    }
                    String str[] = new String[emg_data_size];
                    for (int i=0;i<emg_data.length;i++){
                        str[i] = ""+emg_data[i];
                    }

                    try {
                        for (int i=0;i<str.length;i++){
                            outputStream_emgdata.write(str[i].getBytes());
                            outputStream_emgdata.write("\n".getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        outputStream_mpu.flush();
                        outputStream_mpu.close();
                        outputStream_emgdata.flush();
                        outputStream_emgdata.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }
            }
        }


        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            if(descriptor_characteristic1_service1_uuid.equals(descriptor.getUuid())){
                Log.i("READ DESCRIPTOR", ""+status);
                Log.i("DESCRIPTOR VALUE", ""+descriptor.getValue());
            }
        }



        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            date = new Date();
            long l = date.getTime();
            Log.i("DES Time stamp", ""+l);
            Log.i("DES", "ENTERED DESCRIPTOR");
        }
    };


    public int[] constructEmgData(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[emg_data_size];
        for (int i=0;i<sub_byte.length;i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;
            maxEmg = maxEmg<emg_data[k]?emg_data[k]:maxEmg;
            i++;
            k++;
        }
        return emg_data;
    }

    public  int getIntValue(byte a, byte b){
        int a32 = a;
        int a33 = b;

        int xyz = a33<<8 | a32&0xFF;
        return xyz;
    }

    public String byteToStringHexadecimal(byte b){
        String st = String.format("%02X",b);
        return st;
    }

    public boolean hasStoragePermissionGranted(){
        return  ContextCompat.checkSelfPermission(MmtSession.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public int getAngleFromData(byte a, byte b){

        int a32 = a;
        int a33 = b;

        int angle = a33<<8 | a32&0xFF;
        return angle;
    }
}

