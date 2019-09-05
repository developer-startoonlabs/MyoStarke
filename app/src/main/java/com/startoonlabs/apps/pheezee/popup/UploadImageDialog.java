package com.startoonlabs.apps.pheezee.popup;

import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.activities.ScanDevicesActivity;
import com.startoonlabs.apps.pheezee.services.Scanner;

public class UploadImageDialog {

    Context context;
    AlertDialog.Builder builder = null;
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };


    public UploadImageDialog(Context context){
        this.context = context;
    }

    public void showDialog(){
        builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {
                    if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_DENIED) {
                        ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                        cameraIntent();
                    }
                    else {
                        cameraIntent();
                    }
                } else if (items[item].equals("Choose from Library")) {
                    galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void cameraIntent() {
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ((Activity)context).startActivityForResult(takePicture, 5);
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickPhoto.putExtra("patientid",1);
        ((Activity)context).startActivityForResult(pickPhoto , 6);
    }


}
