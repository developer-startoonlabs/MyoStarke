package com.example.sai.pheezeeapp.services;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;

import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.R;
import com.google.android.gms.vision.barcode.Barcode;
import java.util.List;
import info.androidhive.barcode.BarcodeReader;

public class Scanner extends AppCompatActivity   implements BarcodeReader.BarcodeReaderListener {

//    SharedPreferences sharedPref;
//    JSONArray jsonData = new JSONArray();
//    AlertDialog.Builder builder;
//    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        //builder = new AlertDialog.Builder(this);

    }

    @Override
    public void onScanned(Barcode barcode) {
        Intent intent = new Intent(this, PatientsView.class);
        intent.putExtra("macAddress", barcode.displayValue);
        startActivity(intent);
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
