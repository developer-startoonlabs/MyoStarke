package com.start.apps.pheezee.adapters;

import static androidx.core.content.ContextCompat.startActivity;
import static com.facebook.FacebookSdk.getApplicationContext;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.start.apps.pheezee.activities.DeleteAccountTwo;
import com.start.apps.pheezee.classes.DeviceListClass;
import com.start.apps.pheezee.pojos.WarrantyData;

import start.apps.pheezee.R;
import java.util.ArrayList;

public class DeviceListArrayAdapter extends ArrayAdapter<DeviceListClass> {

    private TextView tv_deviceName,tv_deviceMacAddress, tv_deviceBondState, tv_deviceRssi;
    private Button btn_connectToDevice;

    private Context context;
    private ArrayList<DeviceListClass> mdeviceArrayList;
    private onDeviceConnectPressed connectPressed;

    public  DeviceListArrayAdapter(Context context, ArrayList<DeviceListClass> mdeviceArrayList){
        super(context, R.layout.scanned_devices_listview_model, mdeviceArrayList);
        this.mdeviceArrayList=mdeviceArrayList;
        this.context = context;
    }


    public void updateList(ArrayList<DeviceListClass> mdeviceArrayList){
        this.mdeviceArrayList.clear();
        this.mdeviceArrayList.addAll(mdeviceArrayList);
        this.notifyDataSetChanged();
    }


    public View getView(final int position, final View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                if(connectPressed!=null){
                    connectPressed.onDeviceConnectPressed(mdeviceArrayList.get(position).getDeviceMacAddress());
                }
            }
        });
        return row;
    }

    private void startActivity(Intent intent) {
    }


    public interface onDeviceConnectPressed{
        void onDeviceConnectPressed(String macAddress);
    }

    public void setOnDeviceConnectPressed(onDeviceConnectPressed connectPressed){
        this.connectPressed = connectPressed;
    }
}
