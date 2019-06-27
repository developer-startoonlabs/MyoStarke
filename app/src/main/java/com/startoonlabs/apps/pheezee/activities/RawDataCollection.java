package com.startoonlabs.apps.pheezee.activities;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.startoonlabs.apps.pheezee.classes.BluetoothSingelton;
import com.startoonlabs.apps.pheezee.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class RawDataCollection extends AppCompatActivity {

    private static final int REQUEST_ENABLE_BT = 1;
    private static final String TAG = "Characteristing send";

    public final int both_mpu_xyz_size = 12;
    public final int mpu_packet_sub_size = 24;
    public final int emg_data_size = 20;
    public final int emg_num_packets = 40;
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

    Date rawdata_timestamp;


    CountDownTimer mCountdownTimer;

    Button btn_startrawdatacollection;
    EditText et_timer_raw,et_type_of_packet;
    TextView tv_rawdata_timer;

    public static final UUID service1_uuid = UUID.fromString("909a0309-9693-4920-96e6-893c0157fedd");
    public static final UUID characteristic1_service1_uuid = UUID.fromString("909a7777-9693-4920-96e6-893c0157fedd");
    public static final UUID descriptor_characteristic1_service1_uuid = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_raw_data_collection);





        btn_startrawdatacollection = (Button)findViewById(R.id.btn_startrawdatacollection);
        et_timer_raw = (EditText)findViewById(R.id.et_timer_raw);
        tv_rawdata_timer = (TextView)findViewById(R.id.tv_rawdata_timer);
        et_type_of_packet = findViewById(R.id.et_type_of_packet);
        checkExternalStoragePermissions();



        btn_startrawdatacollection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (!et_timer_raw.getText().toString().equals("") && !et_type_of_packet.getText().toString().equals("")) {
                    if (btn_startrawdatacollection.getText().equals("START")) {
                        int x = Integer.parseInt(et_timer_raw.getText().toString());
                        tv_rawdata_timer.setText("" + x);

                        mCountdownTimer = new CountDownTimer((x + 1) * 1000, 1000) {

                            @Override
                            public void onTick(long millisUntilFinished) {
                                tv_rawdata_timer.setText("" + millisUntilFinished / 1000);
                            }

                            @Override
                            public void onFinish() {
                                tv_rawdata_timer.setText("0");
                            }
                        }.start();



                        btn_startrawdatacollection.setText("STOP");

                        rawdata_timestamp = Calendar.getInstance().getTime();
                        String s = rawdata_timestamp.toString().substring(0, 19);
                        if(hasStoragePermissionGranted()) {
                            if(et_type_of_packet.getText().toString().equals("0")){
                                file_dir_emgdata = new File(Environment.getExternalStorageDirectory()+"/Pheezee/files","EmgData");
                                if (!file_dir_emgdata.exists()) {
                                    file_dir_emgdata.mkdirs();
                                }
                                file_emgdata = new File(file_dir_emgdata, ""+s+".txt");
                                try {
                                    file_emgdata.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                                try {
                                    outputStream_emgdata = new FileOutputStream(file_emgdata, true);
                                    outputStream_emgdata.write("EMG".getBytes());
                                    outputStream_emgdata.write("\n".getBytes());
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            else if(et_type_of_packet.getText().toString().equals("1")){
                                file_dir_mpu = new File(Environment.getExternalStorageDirectory()+"/Pheezee/files","MpuData");
                                if (!file_dir_mpu.exists()) {
                                    file_dir_mpu.mkdirs();
                                }
                                file_mpu = new File(file_dir_mpu,""+s+".txt");
                                try {
                                    file_mpu.createNewFile();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                try {
                                    outputStream_mpu = new FileOutputStream(file_mpu, true);
                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                }
                            }











                            if (characteristic1_service1_uuid.equals(mCharacteristic.getUuid()))
                                bluetoothGatt.setCharacteristicNotification(mCharacteristic, true);
                            if (descriptor_characteristic1_service1_uuid.equals(mBluetoothGattDescriptor.getUuid())) {

                                if(et_type_of_packet.getText().toString().equals("0")){
                                    byte b[] = hexStringToByteArray("BB01");
                                    send(b);
                                }
                                else if(et_type_of_packet.getText().toString().equals("1")) {
                                    byte b[] = hexStringToByteArray("BB02");
                                    send(b);
                                }


//                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
//                                        bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
//                                    }
//                                });

                            }




                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (btn_startrawdatacollection.getText().equals("STOP")) {
                                        date = new Date();
                                        long l = date.getTime();
                                        Log.i("TIMESTAMP", "" + l);
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



                                        btn_startrawdatacollection.setText("START");
                                        date = new Date();
                                        l = date.getTime();
                                        Log.i("TIMESTAMP", "" + l);



                                    }
                                    Log.i("NO OF PCKETS", "" + i);

                                    if(et_type_of_packet.getText().toString().equals("0")) {
                                        MediaScannerConnection.scanFile(
                                                getApplicationContext(),
                                                new String[]{file_emgdata.getAbsolutePath()},
                                                null,
                                                new MediaScannerConnection.OnScanCompletedListener() {
                                                    @Override
                                                    public void onScanCompleted(String path, Uri uri) {
                                                        Log.v("grokkingandroid",
                                                                "file " + path + " was scanned seccessfully: " + uri);
                                                    }
                                                });
                                    }
                                    if(et_type_of_packet.getText().toString().equals("1")) {
                                        MediaScannerConnection.scanFile(
                                                getApplicationContext(),
                                                new String[]{file_mpu.getAbsolutePath()},
                                                null,
                                                new MediaScannerConnection.OnScanCompletedListener() {
                                                    @Override
                                                    public void onScanCompleted(String path, Uri uri) {
                                                        Log.v("grokkingandroid",
                                                                "file " + path + " was scanned seccessfully: " + uri);
                                                    }
                                                });
                                    }


                                    Log.i("num of packets",""+i);
                                }
                            },  x*1000);

                        }

                    } else {
                        btn_startrawdatacollection.setText("START");
                        mCountdownTimer.cancel();
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
                        Log.i("NO OF PCKETS", ""+i);
                    }
                }

                else {
                    Toast.makeText(RawDataCollection.this, "PLEASE ENTER TIMER", Toast.LENGTH_SHORT).show();
                }
            }
        });







        bluetoothAdapter = BluetoothSingelton.getmInstance().getAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            Toast.makeText(RawDataCollection.this, "Bluetooth Disabled", Toast.LENGTH_SHORT).show();
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        //bluetoothGatt = BluetoothGattSingleton.getmInstance().getAdapter();
        if(bluetoothGatt!=null){
            bluetoothGatt.disconnect();
            bluetoothGatt.close();
            Toast.makeText(RawDataCollection.this, "GATT CLOSED", Toast.LENGTH_SHORT).show();
        }

        remoteDevice = bluetoothAdapter.getRemoteDevice(getIntent().getStringExtra("deviceMacAddress"));

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                bluetoothGatt = remoteDevice.connectGatt(RawDataCollection.this,true,callback);
                if(bluetoothGatt!=null){
                    Log.i("BLGATT","not connected");
                    Toast.makeText(RawDataCollection.this, "N/C", Toast.LENGTH_SHORT).show();
                }
            }
        });



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

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        mBluetoothGattDescriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                                        bluetoothGatt.writeDescriptor(mBluetoothGattDescriptor);
                                    }
                                });
            }
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void onCharacteristicChanged(final BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            if(characteristic1_service1_uuid.equals(characteristic.getUuid())) {
                int ra[] = new int[both_mpu_xyz_size];
                int temp = 20;

                int[] emg_data = new int[emg_data_size];
                byte[] b = characteristic.getValue();
                byte sub_byte[] = new byte[b.length - 2];
                int j = 2;
                for (int i = 0; i < sub_byte.length; i++, j++) {
                    sub_byte[i] = b[j];
                }
                byte header_main = b[0];
                byte header_sub = b[1];

                Log.i("header_sub",byteToStringHexadecimal(header_sub));

                if (byteToStringHexadecimal(header_main).equals("BB")) {
                    if (byteToStringHexadecimal(header_sub).equals("02")) {
                        try {
                            outputStream_mpu = new FileOutputStream(file_mpu, true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }


                        for (int i = 0; i < ra.length; i++, temp++) {
                            ra[i] = getIntValue(sub_byte[temp], sub_byte[temp + 1]);
                            temp++;
                        }


                        String accelero = "" + ra[0] + "," + ra[1] + "," + ra[2];
                        String gyro = "" + ra[3] + "," + ra[4] + "," + ra[5];

                        String accelero2 = ra[6] + "," + ra[7] + "," + ra[8];
                        String gyro2 = ra[9] + "," + ra[10] + "," + ra[11];

                        try {

                            outputStream_mpu.write("A1(X Y Z)".getBytes());
                            outputStream_mpu.write(",".getBytes());
                            outputStream_mpu.write(accelero.getBytes());
                            outputStream_mpu.write(",,".getBytes());
                            outputStream_mpu.write("G1(X Y Z)".getBytes());
                            outputStream_mpu.write(",".getBytes());
                            outputStream_mpu.write(gyro.getBytes());
                            outputStream_mpu.write(",,".getBytes());
                            outputStream_mpu.write("A2(X Y Z)".getBytes());
                            outputStream_mpu.write(",".getBytes());
                            outputStream_mpu.write(accelero2.getBytes());
                            outputStream_mpu.write(",,".getBytes());
                            outputStream_mpu.write("G2(X Y Z)".getBytes());
                            outputStream_mpu.write(",".getBytes());
                            outputStream_mpu.write(gyro2.getBytes());
                            //outputStream.write(newstring.getBytes());
                            outputStream_mpu.write("\n".getBytes());

                            Log.i("ACCELERO ", accelero);

                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            outputStream_mpu.flush();
                            outputStream_mpu.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

                if (byteToStringHexadecimal(header_main).equals("BB")) {
                    if (byteToStringHexadecimal(header_sub).equals("01")) {
                        try {
                            outputStream_emgdata = new FileOutputStream(file_emgdata, true);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        i++;
                        byte temp_array[] = new byte[emg_num_packets];
                        for (int i = 0; i < emg_num_packets; i++) {
                            temp_array[i] = sub_byte[i];
                        }
                        emg_data = constructEmgData(temp_array);

                        String str[] = new String[emg_data_size];
                        for (int i = 0; i < emg_data.length; i++) {
                            str[i] = "" + emg_data[i];
                        }

                        try {
                            for (int i = 0; i < str.length; i++) {
                                outputStream_emgdata.write(str[i].getBytes());
                                outputStream_emgdata.write("\n".getBytes());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        try {
                            outputStream_emgdata.flush();
                            outputStream_emgdata.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


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

    private void checkExternalStoragePermissions() {
        if (hasStoragePermissionGranted()) {
            //You can do what whatever you want to do as permission is granted
        } else {
            requestExternalStoragePermission();
        }
    }

    public boolean hasStoragePermissionGranted(){
        return  ContextCompat.checkSelfPermission(RawDataCollection.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestExternalStoragePermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            ActivityCompat.requestPermissions(RawDataCollection.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE);
        }
    }

    public boolean send(byte[] data) {

        if (bluetoothGatt == null ) {
            Log.w(TAG, "BluetoothGatt not initialized");
            return false;
        }
        if (mCharacteristic == null) {
            Log.w(TAG, "Send characteristic not found");
            return false;
        }

        BluetoothGattService service = bluetoothGatt.getService(service1_uuid);

        if(service==null){
            if (mCharacteristic == null) {
                Log.w(TAG, "Send service not found");
                return false;
            }
        }
        if(characteristic1_service1_uuid.equals(mCharacteristic.getUuid())){
            Log.i("TRUE", "TRUE");
        }


        mCharacteristic.setValue(data);

        return bluetoothGatt.writeCharacteristic(mCharacteristic);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

}
