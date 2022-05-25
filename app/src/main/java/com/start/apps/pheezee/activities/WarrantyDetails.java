package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;
import static com.start.apps.pheezee.services.PheezeeBleService.jobid_device_status;
import static com.start.apps.pheezee.services.PheezeeBleService.serial_id;

import androidx.appcompat.app.AppCompatActivity;
import androidx.legacy.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crashlytics.internal.network.HttpResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.utils.RegexOperations;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import start.apps.pheezee.R;

public class WarrantyDetails extends AppCompatActivity implements MqttSyncRepository.OnWarrrantyDetails {
    MqttSyncRepository repository;
    SharedPreferences sharedPref;

    ImageView click,button;
    TextView textview,textview1,textview2;
    String text_view,text_view1,text_view2;

    private static final int REQUEST_EXTERNAL_STORAGe = 1;
    private static String[] permissionstorage = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    private MqttSyncRepository responce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warranty_details);
        click = findViewById(R.id.dwt_1);
        button = findViewById(R.id.ktr_b);
        textview = findViewById(R.id.text_data);
        textview1 = findViewById(R.id.text_data1);
        textview2 = findViewById(R.id.text_data2);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        text_view = sharedPref.getString("deviceMacaddress", "");
        if(text_view != null)
        {
            Toast.makeText(WarrantyDetails.this,"Please Connect Device! ",Toast.LENGTH_LONG).show();
        }
        textview.setText(text_view);

        SharedPreferences preferences1 = PreferenceManager.getDefaultSharedPreferences(this);
        text_view1 = preferences1.getString("serial_number","");
        textview1.setText(text_view1);
        SharedPreferences preferences2 = PreferenceManager.getDefaultSharedPreferences(this);
        text_view2 = preferences2.getString("time_stamp","");
        textview2.setText(text_view2);
        verifystoragepermissions(this);

            // adding beep sound
//            final MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.);
            click.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(WarrantyDetails.this, "You just Captured a Screenshot," +
                            " Open Gallery/ File Storage to view your captured Screenshot", Toast.LENGTH_SHORT).show();
                    screenshot(getWindow().getDecorView().getRootView(), "result");

//                    mediaPlayer.start();
                }
            });


                 ImageView button = findViewById(R.id.ktr_b);
                 button.setOnClickListener(new View.OnClickListener() {
                 @Override
                 public void onClick(View v) {
                     finish();
                 }

             });






        }





        protected static File screenshot(View view, String filename) {
            Date date = new Date();

            // Here we are initialising the format of our image name
            CharSequence format = android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", date);
            try {
                // Initialising the directory of storage
                String dirpath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) + "/PheezeeApp/files"+"warranty";
                File file = new File(dirpath);
                if (!file.exists()) {
                    boolean mkdir = file.mkdir();
                }

                // File name
                String path = dirpath + "/" + filename + "-" + format + ".jpeg";
                view.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache());
                view.setDrawingCacheEnabled(false);
                File imageurl = new File(path);
                FileOutputStream outputStream = new FileOutputStream(imageurl);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
                outputStream.flush();
                outputStream.close();
                return imageurl;

            } catch (IOException io) {
                io.printStackTrace();
            }
            return null;
        }

        // verifying if storage permission is given or not
        public static void verifystoragepermissions(Activity activity) {

            int permissions = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            // If storage permission is not given then request for External Storage Permission
            if (permissions != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, permissionstorage, REQUEST_EXTERNAL_STORAGe);
            }
        }

    @Override
    public void onConfirmEmail(boolean response, String message) {

    }

    @Override
    public void onSignUp(boolean response) {

    }
}