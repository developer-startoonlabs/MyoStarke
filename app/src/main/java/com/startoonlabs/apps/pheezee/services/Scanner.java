package com.startoonlabs.apps.pheezee.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.SparseArray;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.vision.barcode.Barcode;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;

import java.util.List;

import info.androidhive.barcode.BarcodeReader;

public class Scanner extends AppCompatActivity implements BarcodeReader.BarcodeReaderListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
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
