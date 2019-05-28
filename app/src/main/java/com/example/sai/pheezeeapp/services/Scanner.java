package com.example.sai.pheezeeapp.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import com.example.sai.pheezeeapp.activities.PatientsView;
import com.example.sai.pheezeeapp.R;
import com.google.android.gms.vision.barcode.Barcode;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.androidhive.barcode.BarcodeReader;

public class Scanner extends AppCompatActivity   implements BarcodeReader.BarcodeReaderListener {

//    SharedPreferences sharedPref;
//    JSONArray jsonData = new JSONArray();
//    AlertDialog.Builder builder;
//    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        //builder = new AlertDialog.Builder(this);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();

    }

    @Override
    public void onScanned(Barcode barcode) {
        Intent intent = new Intent(this, PatientsView.class);
        boolean isMac = validate(barcode.displayValue);
        Log.i("m.find", String.valueOf(isMac));
        if(isMac) {
            Log.i("mac add","true");
            editor.putString("deviceMacaddress", barcode.displayValue);
            editor.commit();
        }
//        intent.putExtra("macAddress", barcode.displayValue);
        startActivity(intent);
        finish();
    }

    @Override
    public void onScannedMultiple(List<Barcode> barcodes) {

    }

    @Override
    public void onBitmapScanned(SparseArray<Barcode> sparseArray) {

    }

    @Override
    public void onScanError(String errorMessage) {

    }

    @Override
    public void onCameraPermissionDenied() {

    }

    public boolean validate(String mac) {
        Pattern p = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");
        Matcher m = p.matcher(mac);
        return m.find();
    }

//    private void displayDialogBox(final String macAddress) {
//        builder.setTitle("Pheezee is detected");
//        builder.setMessage("Mac Address: "+ macAddress+"\nEnter the name of the bed");
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                String m_Text = input.getText().toString();
//                JSONObject jsonObject = new JSONObject();
//                try {
//                    jsonObject.put("name",m_Text);
//                    jsonObject.put("macAddress",macAddress);
//                    jsonObject.put("patientId","");
//
//                    if(!(new JSONArray(sharedPref.getString("data","")).getJSONObject(0).getString("name").equals(null)))
//                        jsonData = new JSONArray(sharedPref.getString("data",""));
//                    jsonData.put(jsonObject);
//                    editor.putString("data",jsonData.toString());
//                    editor.commit();
//
//
//                    System.out.println(new JSONArray(sharedPref.getString("data","")).getJSONObject(1).getString("macAddress"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        builder.show();
//    }
}
