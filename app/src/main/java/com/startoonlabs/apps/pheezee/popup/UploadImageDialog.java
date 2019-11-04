package com.startoonlabs.apps.pheezee.popup;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.MediaStore;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class UploadImageDialog {

    Context context;
    AlertDialog.Builder builder = null;
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    private int result_code_gallery, result_code_camera;


    public UploadImageDialog(Context context, int result_code_camera, int result_code_gallery){
        this.context = context;
        this.result_code_camera = result_code_camera;
        this.result_code_gallery = result_code_gallery;
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
        ((Activity)context).startActivityForResult(takePicture, result_code_camera);
    }

    private void galleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickPhoto.putExtra("patientid",1);
        ((Activity)context).startActivityForResult(pickPhoto , result_code_gallery);
    }


}
