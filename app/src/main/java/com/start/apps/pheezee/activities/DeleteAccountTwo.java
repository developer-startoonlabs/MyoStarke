package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;
import static com.start.apps.pheezee.classes.PatientActivitySingleton.phizioemail;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.MacAddress;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.app.Dialog;
import android.widget.TextView;
import android.widget.Toast;

import com.start.apps.pheezee.classes.DeviceListClass;
import com.start.apps.pheezee.pojos.DeletePatientData;
import com.start.apps.pheezee.pojos.DeletePhiziouserData;
import com.start.apps.pheezee.pojos.PhizioDetailsData;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.room.Entity.DeviceStatus;
import com.start.apps.pheezee.room.Entity.PhizioPatients;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.RegexOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import start.apps.pheezee.R;

public class DeleteAccountTwo extends AppCompatActivity implements MqttSyncRepository.OnDeletePhiziouser {
    MqttSyncRepository repository;
    private GetDataService getDataService;
    TextView receiver_msg;
    JSONObject json_phizio;
    SharedPreferences sharedPref;
    EditText phizioemail,feedback,todelete,needdata;
    String str_phizioemail,str_feedback, str_todelete, str_needdata;
    Button button1;
    private Object DeviceInfoActivity;
    private ArrayList<DeviceListClass> mdeviceArrayList;
    private Object CheckedTextView;
//    ProgressDialog progress, deletephizio_progress;
//    private static final String TAG = "Delete";

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account_two);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        repository = new MqttSyncRepository(getApplication());
        repository.setOnDeletePhiziouser(this);
        phizioemail = findViewById(R.id.received_value_id);
        Intent intent = new Intent();
        intent.putExtra("et_phizio_email", json_phizioemail);
        String str1 = intent.getStringExtra("et_phizio_email");
        phizioemail.setText(str1);
        feedback = findViewById(R.id.received_value_id1);
        Intent intent1 = getIntent();
        String str2 = intent1.getStringExtra("feedback");
        feedback.setText(str2);
        //Testing
        todelete = findViewById(R.id.received_value_id2);
        todelete.setText("all");
        needdata = findViewById(R.id.received_value_id3);
        needdata.setText("1");
        button1 = findViewById(R.id.delete_button);
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                str_phizioemail = phizioemail.getText().toString();
                str_feedback = feedback.getText().toString();
                str_todelete = todelete.getText().toString();
                str_needdata = needdata.getText().toString();
                repository.deletePhiziouser(str_phizioemail,str_feedback,str_todelete,str_needdata);
                showToast("Your Account Deleted Sucessfully");
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });


        ImageView button = findViewById(R.id.iv_back_phizio_profile);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });



//        needdata.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//                if(b)
//                {
//                    needdata.setTag("Y");
//                }
//                if(needdata.isChecked()==false)
//                {
//                    needdata.setTag("N");
//                }
//            }
//        });


    }





    @Override
    public void onConfirmEmail(boolean response, String message) {

    }

    @Override
    public void onSignUp(boolean response) {

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void showToast(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}