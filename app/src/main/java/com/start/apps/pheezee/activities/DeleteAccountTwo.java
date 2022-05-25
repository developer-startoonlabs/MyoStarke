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
import android.view.WindowManager;
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
//                repository.deletePhiziouser(str_phizioemail,str_feedback,str_todelete,str_needdata);
//                showToast("");
//                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                startActivity(intent);
                final Dialog dialog = new Dialog(DeleteAccountTwo.this);
                dialog.setContentView(R.layout.notification_dialog_box);

                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);

                TextView notification_title = dialog.findViewById(R.id.notification_box_title);
                TextView notification_message = dialog.findViewById(R.id.notification_box_message);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);
                Button Notification_Button_cancel = (Button) dialog.findViewById(R.id.notification_ButtonCancel);

                Notification_Button_ok.setText("Send Request");
                Notification_Button_cancel.setText("No");

                // Setting up the notification dialog
                notification_title.setText("Delete Account");
                notification_message.setText("Are you sure you want to Delete your account?");

                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                Intent intent=new Intent(Intent.ACTION_SEND);
                                String[] recipients={"care@startoonlabs.com"};
                                String mail = json_phizioemail;
                                intent.putExtra(Intent.EXTRA_EMAIL, recipients);
                                intent.putExtra(Intent.EXTRA_SUBJECT,"Delete My Pheezee Account");
                                intent.putExtra(Intent.EXTRA_TEXT,"Dear Startoonlabs Team,\n I have an account in your Pheezee App with Mail id :"+ mail +System.getProperty("line.separator")+"Reason:"+str2+System.getProperty("line.separator")+" Thank you");
                                intent.setType("text/html");
                                intent.setPackage("com.google.android.gm");
                                startActivity(Intent.createChooser(intent, "Send mail"));

                        }
                });
                // On click Cancel
                Notification_Button_cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                                dialog.dismiss();

                        }
                });

                dialog.show();

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