package com.example.sai.pheezeeapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Activities.ScanDevicesActivity;
import com.example.sai.pheezeeapp.Classes.DeviceListClass;
import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.R;

import java.util.ArrayList;

public class DeviceListArrayAdapter extends ArrayAdapter<DeviceListClass> {

    ScanDevicesActivity scanDevicesActivity;

    TextView tv_deviceName,tv_deviceMacAddress, tv_deviceBondState, tv_deviceRssi, tv_idontKnowYet;
    Button btn_connectToDevice;


    Context context;
    ArrayList<DeviceListClass> mdeviceArrayList;

    public  DeviceListArrayAdapter(Context context, ArrayList<DeviceListClass> mdeviceArrayList){
        super(context, R.layout.scanned_devices_listview_model, mdeviceArrayList);
        this.mdeviceArrayList=mdeviceArrayList;
        this.context = context;
        scanDevicesActivity = new ScanDevicesActivity();
    }


    public void updateList(ArrayList<DeviceListClass> mdeviceArrayList){
        this.mdeviceArrayList.clear();
        this.mdeviceArrayList.addAll(mdeviceArrayList);
        this.notifyDataSetChanged();
    }


    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View row = inflater.inflate(R.layout.scanned_devices_listview_model,parent,false);

        tv_deviceName = (TextView)row.findViewById(R.id.tv_deviceName);
        tv_deviceMacAddress = (TextView)row.findViewById(R.id.tv_deviceMacAdress);
        tv_deviceBondState = (TextView)row.findViewById(R.id.tv_deviceBondState);
        tv_deviceRssi = (TextView)row.findViewById(R.id.tv_deviceRssi);
        tv_idontKnowYet = (TextView)row.findViewById(R.id.tv_iDontKnowYet);

        btn_connectToDevice = (Button)row.findViewById(R.id.btn_connectToDevice);




        tv_deviceName.setText(mdeviceArrayList.get(position).getDeviceName());
        tv_deviceMacAddress.setText(mdeviceArrayList.get(position).getDeviceMacAddress());
        tv_deviceBondState.setText(mdeviceArrayList.get(position).getDeviceBondState());
        tv_deviceRssi.setText(mdeviceArrayList.get(position).getDeviceRssi());
        //tv_idontKnowYet.setText(mdeviceArrayList.get(position));
        btn_connectToDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String macAddressOfTheSelectedDevice  = mdeviceArrayList.get(position).getDeviceMacAddress();
                //Toast.makeText(context, macAddressOfTheSelectedDevice, Toast.LENGTH_SHORT).show();
                ScanDevicesActivity.selectedDeviceMacAddress = macAddressOfTheSelectedDevice;
                Toast.makeText(context, ScanDevicesActivity.selectedDeviceMacAddress, Toast.LENGTH_SHORT).show();
                Intent i = new Intent(context, PatientsView.class);
                context.startActivity(i);
            }
        });

        return row;
    }
}
