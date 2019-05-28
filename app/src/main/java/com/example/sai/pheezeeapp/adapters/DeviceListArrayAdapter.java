package com.example.sai.pheezeeapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.activities.ScanDevicesActivity;
import com.example.sai.pheezeeapp.classes.DeviceListClass;
import com.example.sai.pheezeeapp.activities.PatientsView;
import com.example.sai.pheezeeapp.R;

import java.util.ArrayList;

public class DeviceListArrayAdapter extends ArrayAdapter<DeviceListClass> {
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    ScanDevicesActivity scanDevicesActivity;

    TextView tv_deviceName,tv_deviceMacAddress, tv_deviceBondState, tv_deviceRssi;
    Button btn_connectToDevice;

    Context context;
    ArrayList<DeviceListClass> mdeviceArrayList;

    public  DeviceListArrayAdapter(Context context, ArrayList<DeviceListClass> mdeviceArrayList){
        super(context, R.layout.scanned_devices_listview_model, mdeviceArrayList);
        this.mdeviceArrayList=mdeviceArrayList;
        this.context = context;
        scanDevicesActivity = new ScanDevicesActivity();
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = preferences.edit();
    }


    public void updateList(ArrayList<DeviceListClass> mdeviceArrayList){
        this.mdeviceArrayList.clear();
        this.mdeviceArrayList.addAll(mdeviceArrayList);
        this.notifyDataSetChanged();
    }


    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.scanned_devices_listview_model,parent,false);

        tv_deviceName = row.findViewById(R.id.tv_deviceName);
        tv_deviceMacAddress = row.findViewById(R.id.tv_deviceMacAdress);
        tv_deviceBondState = row.findViewById(R.id.tv_deviceBondState);
        tv_deviceRssi = row.findViewById(R.id.tv_deviceRssi);

        btn_connectToDevice = row.findViewById(R.id.btn_connectToDevice);




        tv_deviceName.setText(mdeviceArrayList.get(position).getDeviceName());
        tv_deviceMacAddress.setText(mdeviceArrayList.get(position).getDeviceMacAddress());
        tv_deviceBondState.setText(mdeviceArrayList.get(position).getDeviceBondState());
        tv_deviceRssi.setText(mdeviceArrayList.get(position).getDeviceRssi());
        //tv_idontKnowYet.setText(mdeviceArrayList.get(position));
        btn_connectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(context, macAddressOfTheSelectedDevice, Toast.LENGTH_SHORT).show();
                editor = preferences.edit();
                ScanDevicesActivity.selectedDeviceMacAddress = mdeviceArrayList.get(position).getDeviceMacAddress();
                editor.putString("deviceMacaddress", mdeviceArrayList.get(position).getDeviceMacAddress());
                editor.commit();
                Toast.makeText(context, "connecting....", Toast.LENGTH_SHORT).show();
                editor.putString("pressed","c");
                editor.commit();
                PatientsView.disconnectDevice();
                Intent i = new Intent(context, PatientsView.class);
                context.startActivity(i);
                PatientsView.deviceState=false;
                ((Activity)context).finish();
            }
        });
        return row;
    }
}
