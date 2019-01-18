package com.example.sai.pheezeeapp.Activities;

import android.Manifest;
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
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BluetoothSingelton;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class CalibrationSession extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_RELAX = 1;
    private static final int REQUEST_TIGHT = 2;

    //shared preference related declarations
    SharedPreferences mmt_sharedpreferences;
    JSONObject json_phizio;

    //MQtt related stuff
    MqttHelper mqttHelper;
    Long tsLong = 0L;
    //All the mqtt topics

    String mqtt_publish_calibrationSession  = "phizio/calibration/addpatientsession";
    String mqtt_publish_calibrationSession_response  = "phizio/calibration/addpatientsession/response";

    TextView tv_bodypart, tv_relax_or_tight,tv_about_to_start,tv_min_emg,tv_max_emg,tv_session_about_to_start,tv_successfully_recorded,tv_session_status;
    TextView tv_calibration_patientid, tv_calibration_patientname;

    Button btn_start_calibration_session;

    String bodyPart, patientId, patientName;

    //Arrays sizes
    public final int both_mpu_xyz_size = 12;
    public final int emg_data_size = 10;
    public final int emg_num_packets = 20;


    //Emg variables for average and min and max
    int minEmg=0,maxEmg=0,tempEmg,numOfEmgValues;


    //timer
    CountDownTimer countDownTimer;


    BluetoothGatt bluetoothGatt;
    int REQUEST_STORAGE = 1;
    BluetoothDevice remoteDevice;
    BluetoothAdapter bluetoothAdapter;
    //BluetoothManager mBluetoothManager;
    BluetoothGattDescriptor mBluetoothGattDescriptor;
    BluetoothGattCharacteristic mCharacteristic;



    //UUIDS
    public static final UUID service1_uuid = UUID.fromString("909a1400-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a1401-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calibration_session);

        //local data from shared preference
        mmt_sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(mmt_sharedpreferences.getString("phiziodetails", ""));
            Log.i("Patient View", json_phizio.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //MQtt Connection

        mqttHelper = new MqttHelper(this);

        //Defination of all the textviews

        tv_bodypart = (TextView)findViewById(R.id.tv_bodypart);
        tv_relax_or_tight = (TextView)findViewById(R.id.tv_relax_or_tight);
        tv_about_to_start = (TextView)findViewById(R.id.tv_about_to_start);
        tv_min_emg = (TextView)findViewById(R.id.tv_min_emg);
        tv_max_emg = (TextView)findViewById(R.id.tv_max_emg);
        tv_successfully_recorded = (TextView)findViewById(R.id.tv_sucessfully_recorded);
        tv_session_about_to_start = (TextView)findViewById(R.id.tv_session_about_to_start);
        tv_session_status = (TextView)findViewById(R.id.tv_session_status);
        tv_calibration_patientid =(TextView)findViewById(R.id.tv_calibration_patientid);
        tv_calibration_patientname=(TextView)findViewById(R.id.tv_calibration_patientname);

        //Defination of the start session button
        btn_start_calibration_session = (Button)findViewById(R.id.btn_start_calibration_session);

        //onclick of the calibration start session

        btn_start_calibration_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_start_calibration_session.setClickable(false);
                tsLong = System.currentTimeMillis();
                setRelaxOrTight(REQUEST_RELAX);
                tv_session_status.setText("Session is on going.....");
                tv_session_about_to_start.setVisibility(View.VISIBLE);
                tv_about_to_start.setVisibility(View.VISIBLE);
                countDownTimer = new CountDownTimer(4*1000,1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        tv_about_to_start.setText(""+millisUntilFinished/1000);
                    }

                    @Override
                    public void onFinish() {
                        startSession(REQUEST_RELAX);
                    }
                }.start();
            }
        });


        //Getting all the extras to the intent

        bodyPart = getIntent().getStringExtra("bodypart");
        patientId = getIntent().getStringExtra("patientid");
        patientName = getIntent().getStringExtra("patientname");

        tv_calibration_patientname.setText(patientName);
        tv_calibration_patientid.setText(patientId);


        setBodyPart();



        //All the bluetooth connection related work
        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(CalibrationSession.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //bluetoothGatt = BluetoothGattSingleton.getmInstance().getAdapter();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Toast.makeText(CalibrationSession.this, "GATT CLOSED", Toast.LENGTH_SHORT).show();
        }
        if (getIntent().hasExtra("deviceMacAddress"))
            remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                if (remoteDevice!=null)
                    bluetoothGatt = remoteDevice.connectGatt(CalibrationSession.this,true,callback);
            }
        });








    }

    private void startSession(final int requestCode) {
        tempEmg=0;numOfEmgValues=0;

        tv_successfully_recorded.setVisibility(View.VISIBLE);
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



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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
                    tv_successfully_recorded.setText("Successfully recorded relax values");

                    tv_session_about_to_start.setVisibility(View.INVISIBLE);
                    tv_about_to_start.setVisibility(View.INVISIBLE);
                    if(!(requestCode ==REQUEST_TIGHT)) {
                        tv_successfully_recorded.setText("Successfully recorded relax values");
                        startTimerForTight();
                        minEmg = tempEmg/numOfEmgValues;
                        Log.i("Temp Emg",tempEmg+" "+numOfEmgValues);
                    }
                    else {
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String dateString = formatter.format(new Date(tsLong));
                        tv_successfully_recorded.setText("Successfully recorded tight values");
                        tv_session_status.setText("Session Completed");
                        tv_relax_or_tight.setVisibility(View.INVISIBLE);
                        btn_start_calibration_session.setClickable(true);

                        maxEmg = tempEmg/numOfEmgValues;
                        Log.i("Temp Emg",tempEmg+" "+numOfEmgValues);
                        tv_min_emg.setText(minEmg+"");
                        tv_max_emg.setText(maxEmg+"");

                        JSONObject object = new JSONObject();
                        MqttMessage message = new MqttMessage();

                        try {
                            object.put("phizioemail",json_phizio.get("phizioemail"));
                            object.put("patientid",patientId);
                            object.put("heldon",dateString);
                            object.put("bodypart",bodyPart);
                            object.put("relaxminemg",minEmg+"");
                            object.put("tightmaxemg",maxEmg+"");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        message.setPayload(object.toString().getBytes());
                        mqttHelper.publishMqttTopic(mqtt_publish_calibrationSession,message);


                    }
                }
            },5000);

        }
    }

    private void startTimerForTight() {
        setRelaxOrTight(REQUEST_TIGHT);
        tv_session_about_to_start.setVisibility(View.VISIBLE);
        tv_about_to_start.setVisibility(View.VISIBLE);
        countDownTimer = new CountDownTimer(4*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_about_to_start.setText(""+millisUntilFinished/1000);
            }

            @Override
            public void onFinish() {
                tv_successfully_recorded.setVisibility(View.INVISIBLE);
                tv_successfully_recorded.setText("Recording values please wait");
                startSession(REQUEST_TIGHT);
            }
        }.start();

    }

    private void setRelaxOrTight(int requestCode) {
        if(requestCode==1)
            tv_relax_or_tight.setText("Please relax your "+bodyPart);
        else if(requestCode==2){
            tv_relax_or_tight.setText("please hold your "+bodyPart+" tight.");
        }
    }

    private void setBodyPart() {
        tv_bodypart.setText(bodyPart);
    }






    //Gatt callback

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


//
//                int ra[] = new int[both_mpu_xyz_size];
//                int angleDetected;
//                int temp = 20;

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
                        byte temp_array[] = new byte[emg_num_packets];
                        for (int i=0;i<emg_num_packets;i++){
                            temp_array[i] = sub_byte[i];
                        }
                        emg_data = constructEmgData(temp_array);

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
        }
    };


    public int[] constructEmgData(byte[] sub_byte){
        int k=0;
        int[] emg_data = new int[emg_data_size];
        for (int i=0;i<sub_byte.length;i++){
            int a = sub_byte[i]&0xFF;
            int b = sub_byte[i+1]&0xFF;

            emg_data[k] = b<<8 | a;

            tempEmg+=emg_data[k];
            numOfEmgValues++;
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
}
