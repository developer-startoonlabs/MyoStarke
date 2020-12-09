package com.startoonlabs.apps.pheezee.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseArray;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.barcode.Barcode;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.BodyPartSelection;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class Scanner extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    ImageView back_button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        back_button = findViewById(R.id.summary_go_back);

        back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onScanned(Barcode barcode) {
        Intent intent = new Intent();
        boolean isMac = RegexOperations.validate(barcode.displayValue);
        if(isMac) {
            intent.putExtra("macAddress", barcode.displayValue);
            setResult(-1, intent);
        }
        else {
            intent.putExtra("macAddress", barcode.displayValue);
            setResult(2, intent);
        }
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
}
