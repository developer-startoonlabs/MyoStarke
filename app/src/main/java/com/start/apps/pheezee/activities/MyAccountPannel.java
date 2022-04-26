package com.start.apps.pheezee.activities;

import static com.start.apps.pheezee.activities.PatientsView.json_phizioemail;
import static com.start.apps.pheezee.services.PheezeeBleService.hardware_version_characteristic_uuid;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.start.apps.pheezee.Usermanual;
import com.start.apps.pheezee.pojos.PatientStatusData;
import com.start.apps.pheezee.pojos.PhizioDetailsData;
import com.start.apps.pheezee.pojos.PhizioSessionReportData;
import com.start.apps.pheezee.activities.DeviceInfoActivity;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.services.PicassoCircleTransformation;
import com.start.apps.pheezee.utils.BitmapOperations;
import com.start.apps.pheezee.utils.NetworkOperations;
import com.start.apps.pheezee.utils.RegexOperations;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import start.apps.pheezee.R;

import static com.start.apps.pheezee.services.PheezeeBleService.device_state;
import static com.start.apps.pheezee.services.PheezeeBleService.jobid_device_details_update;
import static com.start.apps.pheezee.services.PheezeeBleService.jobid_device_status;


public class MyAccountPannel extends AppCompatActivity /*implements MqttSyncRepository.OnPhizioDetailsResponseListner*/ {
//        EditText et_phizio_name, et_phizio_email, et_phizio_phone,et_address, et_clinic_name, et_dob, et_experience, et_specialization, et_degree, et_gender;
//        Spinner spinner;
//        MqttSyncRepository repository;
//        TextView macaddress;
//
//
//        private String deviceMacc = "";
//        ImageView iv_phizio_profilepic, iv_phizio_clinic_logo, iv_back_button,tv_edit_profile_details,user_manual_btr,change_password_btr,warranty_btr,delete_btr;
//        LinearLayout tv_update_clinic_logo,tv_edit_profile_pic;
//        final Calendar myCalendar = Calendar.getInstance();
//        String str_devicemac;
//
//        Button btn_update, btn_cancel_update;
//        GetDataService getDataService;
//        SharedPreferences preferences;
//        JSONObject json_phizio;
//        SharedPreferences sharedPref;
//        ProgressDialog dialog;
//        private ImageView ivBasicImage;
//
//        @Override
//        protected void onCreate(Bundle savedInstanceState) {
//                super.onCreate(savedInstanceState);
//                preferences = PreferenceManager.getDefaultSharedPreferences(this);
//                if(!Objects.requireNonNull(preferences.getString("deviceMacaddress", "")).equalsIgnoreCase(""))
//                        deviceMacc = preferences.getString("deviceMacaddress","");
//                repository = new MqttSyncRepository(this.getApplication());
//
//                getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
////                tv_device_mamc = findViewById(R.id.tv_deviceinfo_device_mac);
//////                tv_device_mamc.setText(getIntent().getStringExtra("deviceMacAddress"));
////                tv_device_mamc.setText(preferences.getString("deviceMacaddress", ""));
////                Intent intent = new Intent();
////                intent.putExtra("deviceMacAddress", jobid_device_status);
////                String str1 = intent.getStringExtra("deviceMacAddress");
////                tv_device_mamc.setText(str1);
//
//                Configuration config = getResources().getConfiguration();
//                if (config.smallestScreenWidthDp >= 600) {
//                        setContentView(R.layout.activity_my_account_pannel);
//                } else {
//                        setContentView(R.layout.activity_my_account_pannel);
//                }
////                repository = new MqttSyncRepository(getApplication());
////                repository.setOnPhizioDetailsResponseListner(this);
//                //Shared Preference
//                sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
//                dialog = new ProgressDialog(this);
//                dialog.setMessage("Updating details, please wait");
//
//                try {
//                        json_phizio = new JSONObject(sharedPref.getString("phiziodetails", ""));
//                } catch (JSONException e) {
//                        e.printStackTrace();
//                }
//
//                try
//                {
//                        // Haaris
//                        PatientStatusData data = new PatientStatusData(json_phizio.getString("phizioemail"), null,null);
//
//                        Call<PhizioSessionReportData> getsession_report_count_response = getDataService.getsession_report_count(data);
////                        getsession_report_count_response.enqueue(new Callback<PhizioSessionReportData>() {
////                                @Override
////                                public void onResponse(@NonNull Call<PhizioSessionReportData> call, @NonNull Response<PhizioSessionReportData> response) {
////                                        if (response.code() == 200) {
////                                                PhizioSessionReportData res = response.body();
////                                                Session_count = res.getSession().toString();
////                                                Report_count = res.getReport().toString();
////                                                //  tv_profile_session_number.setText(Session_count);
////                                                // tv_profile_report_number.setText(Report_count);
////                                        }
////                                }
////
////                                @Override
////                                public void onFailure(@NonNull Call<PhizioSessionReportData> call, @NonNull Throwable t) {
////
////                                }
////                        });
//                }catch (JSONException e)
//                {
//                        e.printStackTrace();
//                }
//                setPhizioDetails();
//        }
//
//
//
//        /**
//         * Initializes views and sets the current values to all the view
//         */
//        private void setPhizioDetails() {
//                et_phizio_name =  findViewById(R.id.et_phizio_name);
//                et_phizio_email =  findViewById(R.id.et_phizio_email);
//                et_phizio_phone =  findViewById(R.id.et_phizio_phone);
//                tv_edit_profile_details  = findViewById(R.id.edit_phizio_details);
//                tv_edit_profile_pic = findViewById(R.id.change_profile_pic);
//                //   tv_profile_patient_number = findViewById(R.id.profile_patient_number);
//                //    tv_profile_session_number = findViewById(R.id.profile_session_number);
//                //    tv_profile_report_number = findViewById(R.id.profile_report_number);
//
//                et_address = findViewById(R.id.et_phizio_address);
//                et_clinic_name = findViewById(R.id.et_phizio_clinic_name);
//                et_dob = findViewById(R.id.et_phizio_dob);
//                et_experience = findViewById(R.id.et_phizio_experience);
//                et_specialization = findViewById(R.id.et_phizio_specialization);
//                et_degree = findViewById(R.id.et_phizio_degree);
//                et_gender = findViewById(R.id.et_phizio_gender);
//                spinner = findViewById(R.id.spinner_gender);
//                tv_update_clinic_logo = findViewById(R.id.change_profile_cliniclogo);
//                iv_phizio_clinic_logo = findViewById(R.id.iv_phizio_cliniclogo);
//                iv_back_button = findViewById(R.id.iv_back_phizio_profile);
////                user_manual_btr = findViewById(R.id.user_manual_btr);
//                change_password_btr = findViewById(R.id.change_password_btr);
////                warranty_btr = findViewById(R.id.warranty_btr);
//                delete_btr = findViewById(R.id.delete_btr);
//                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.gender_array, R.layout.custom_green_spinner   );
//                adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                spinner.setAdapter(adapter);
//
//
//                iv_back_button.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                finish();
//                        }
//                });
//                //user manual
//
////                user_manual_btr.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                                Intent intent = new Intent(getApplicationContext(), Usermanual.class);
////                                startActivity(intent);
////                        }
////                });
//
//                change_password_btr.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
//                                startActivity(intent);
//                        }
//                });
//
////                warranty_btr.setOnClickListener(new View.OnClickListener() {
////                        @Override
////                        public void onClick(View v) {
////                                str_devicemac = sharedPref.getString("deviceMacaddress", "");
////                                Log.i("macaddress", str_devicemac);
////                                repository.warrantyData(str_devicemac);
////                                repository.serialnumber(str_devicemac);
////                                Intent intent = new Intent(getApplicationContext(), WarrantyDetails.class);
////                                startActivity(intent);
////                        }
////                });
//
//
//               delete_btr.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                Intent intent = new Intent(getApplicationContext(), DeleteAccountOne.class);
//                                startActivity(intent);
//                        }
//                });
//
//
//
//
//                iv_phizio_profilepic = (ImageView)findViewById(R.id.iv_phizio_profilepic);
//                try {
//                        if(!json_phizio.getString("phizioprofilepicurl").equals("empty")) {
//                                String temp = null;
//                                try {
//                                        temp = json_phizio.getString("phizioprofilepicurl");
//                                } catch (JSONException e) {
//                                        e.printStackTrace();
//                                }
//                                temp = temp.replaceFirst("@", "%40");
//                                temp = "https://s3.ap-south-1.amazonaws.com/pheezee/" + temp;
//                                Picasso.get().load(temp)
//                                        .placeholder(R.drawable.user_icon)
//                                        .error(R.drawable.user_icon)
//                                        .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
//                                        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
//                                        .transform(new PicassoCircleTransformation())
//                                        .into(iv_phizio_profilepic);
//                        }
//                }
//                catch (JSONException e) {
//                        e.printStackTrace();
//                }
//                btn_update = findViewById(R.id.btn_update_details);
//                btn_cancel_update = findViewById(R.id.btn_cancel_update);
//
//
///**
// * Response from the server
// */
//                focuseEditTexts(false);
//                /**
//                 * gender
//                 */
//                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                                et_gender.setText(spinner.getSelectedItem().toString());
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//                        }
//                });
//                /**
//                 * Edit profile details
//                 */
//                btn_update.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                if(NetworkOperations.isNetworkAvailable(MyAccountPannel.this)) {
//                                        String str_name = et_phizio_name.getText().toString();
//                                        String str_phone = et_phizio_phone.getText().toString();
//                                        String str_phizioemail  = et_phizio_email.getText().toString();
//                                        String str_clinicname = et_clinic_name.getText().toString();
//                                        String str_dob = et_dob.getText().toString();
//                                        String str_experience = et_experience.getText().toString();
//                                        String specialization = et_specialization.getText().toString();
//                                        String degree = et_degree.getText().toString();
//                                        String gender = et_gender.getText().toString();
//                                        String address = et_address.getText().toString();
//                                        if(RegexOperations.isValidUpdatePhizioDetails(str_name,str_phone)){
//                                                PhizioDetailsData data = new PhizioDetailsData(str_name,str_phone,str_phizioemail,str_clinicname,str_dob,str_experience,specialization,degree,gender,address);
//                                                repository.updatePhizioDetails(data);
//                                                dialog.setMessage("Updating details, please wait");
//                                                dialog.show();
//                                        }
//                                        else {
//                                                showToast(RegexOperations.getNonValidStringForPhizioDetails(str_name,str_phone));
//                                        }
//                                }else {
//                                        NetworkOperations.networkError(MyAccountPannel.this);
//                                }
//                        }
//                });
//
//
//                btn_cancel_update.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                setInitialValues();
//                                btn_cancel_update.setVisibility(View.INVISIBLE);
//                                btn_update.setVisibility(View.INVISIBLE);
//                                setPaleWhiteBackground(false);
//
//                                focuseEditTexts(false);
//                        }
//                });
//
//
//                et_dob.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                                new DatePickerDialog(MyAccountPannel.this, dateChangedListener, myCalendar
//                                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//                        }
//                });
//
//                setInitialValues();
//
//        }
//
//        private void setInitialValues() {
//                try {
//                        et_phizio_name.setText(json_phizio.getString("phizioname"));
//                        et_phizio_email.setText(json_phizio.getString("phizioemail"));
//                        et_phizio_phone.setText(json_phizio.getString("phiziophone"));
//                        if(json_phizio.has("clinicname")) {
//                                et_clinic_name.setText(json_phizio.getString("clinicname"));
//                        }
//                        else
//                                et_clinic_name.setText("");
//                        if(json_phizio.has("phiziodob"))
//                                et_dob.setText(json_phizio.getString("phiziodob"));
//                        else
//                                et_dob.setText("");
//                        if(json_phizio.has("experience"))
//                                et_experience.setText(json_phizio.getString("experience"));
//                        else
//                                et_experience.setText("");
//                        if(json_phizio.has("specialization"))
//                                et_specialization.setText(json_phizio.getString("specialization"));
//                        else
//                                et_specialization.setText("");
//                        if(json_phizio.has("degree"))
//                                et_degree.setText(json_phizio.getString("degree"));
//                        else
//                                et_degree.setText("");
//                        if(json_phizio.has("gender"))
//                                et_gender.setText(json_phizio.getString("gender"));
//                        else
//                                et_gender.setText("");
//                        if(json_phizio.has("address"))
//                                et_address.setText(json_phizio.getString("address"));
//                        else
//                                et_address.setText("");
//
//                        if(json_phizio.has("cliniclogo") && !json_phizio.getString("cliniclogo").equalsIgnoreCase("/icons/clinic.png")) {
////                tv_update_clinic_logo.setText("Update Clinic Logo");
//                                String temp = null;
//                                try {
//                                        temp = json_phizio.getString("cliniclogo");
//                                } catch (JSONException e) {
//                                        e.printStackTrace();
//                                }
//                                Picasso.get().load(temp)
//                                        .placeholder(R.drawable.user_icon)
//                                        .error(R.drawable.user_icon)
//                                        .networkPolicy(NetworkPolicy.NO_CACHE,NetworkPolicy.NO_STORE)
//                                        .memoryPolicy(MemoryPolicy.NO_CACHE,MemoryPolicy.NO_STORE)
//                                        .transform(new PicassoCircleTransformation())
//                                        .into(iv_phizio_clinic_logo);
//                        }
//                } catch (JSONException e) {
//                        e.printStackTrace();
//                }
//        }
//
//        DatePickerDialog.OnDateSetListener dateChangedListener = new DatePickerDialog.OnDateSetListener() {
//                @Override
//                public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                        myCalendar.set(Calendar.YEAR, year);
//                        myCalendar.set(Calendar.MONTH, month);
//                        myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
//                        updateLabel();
//                }
//        };
//
//
//
//        private void updateLabel() {
//                String myFormat = "MM/dd/yy"; //In which you need put here
//                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
//                et_dob.setText(sdf.format(myCalendar.getTime()));
//        }
//
//        /**
//         * To change the focus of the text views
//         * @param b
//         */
//        private void focuseEditTexts(boolean b) {
//                et_phizio_name.setFocusable(b);
//                et_phizio_name.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_phizio_name.setClickable(b);
//
//                et_phizio_phone.setFocusable(b);
//                et_phizio_phone.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_phizio_phone.setClickable(b);
//
//                et_gender.setFocusable(b);
//                et_gender.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_gender.setClickable(b);
//
//                et_address.setFocusable(b);
//                et_address.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_address.setClickable(b);
//
//                et_clinic_name.setFocusable(b);
//                et_clinic_name.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_clinic_name.setClickable(b);
//
//                et_dob.setFocusable(false);
//                et_dob.setFocusableInTouchMode(false); // user touches widget on phone with touch screen
//                et_dob.setEnabled(b);
//
//                et_experience.setFocusable(b);
//                et_experience.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_experience.setClickable(b);
//
//                et_specialization.setFocusable(b);
//                et_specialization.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_specialization.setClickable(b);
//
//                et_degree.setFocusable(b);
//                et_degree.setFocusableInTouchMode(b); // user touches widget on phone with touch screen
//                et_degree.setClickable(b);
//
//
//                if(b){
//                        et_gender.setVisibility(View.INVISIBLE);
////            spinner.setVisibility(View.VISIBLE);
//                }
//                else {
////            et_gender.setVisibility(View.VISIBLE);
//                        spinner.setVisibility(View.GONE);
//                }
//        }
//
//        @SuppressLint("ResourceAsColor")
//        private void setPaleWhiteBackground(boolean flag) {
//                ColorDrawable drawable = null;
//                if(flag)
//                        drawable = new ColorDrawable(getResources().getColor(R.color.pale_white));
//                else
//                        drawable = new ColorDrawable(Color.TRANSPARENT);
//                et_gender.setBackground(drawable);
//                et_address.setBackground(drawable);
//                et_clinic_name.setBackground(drawable);
//                et_dob.setBackground(drawable);
//                et_experience.setBackground(drawable);
//                et_specialization.setBackground(drawable);
//                et_degree.setBackground(drawable);
//                et_phizio_name.setBackground(drawable);
//                et_phizio_phone.setBackground(drawable);
//        }
//
//        @Override
//        protected void onDestroy() {
//                super.onDestroy();
//                if(repository!=null){
//                        repository.unregisterPhizioDetailsResponseListner();
//                }
//        }
//
//
//        protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//                super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//                switch(requestCode) {
//                        case 5:
//                                if(resultCode == RESULT_OK){
//                                        Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                                        photo = BitmapOperations.getResizedBitmap(photo,128);
//                                        iv_phizio_profilepic.setImageBitmap(photo);
//                                        ivBasicImage.setImageBitmap(photo);
//                                        repository.updatePhizioProfilePic(et_phizio_email.getText().toString(),photo);
//                                        dialog.setMessage("Uploading image, please wait");
//                                        dialog.show();
//                                }
//                                break;
//                        case 6:
//                                if(resultCode == RESULT_OK){
//                                        Uri selectedImage = imageReturnedIntent.getData();
//                                        iv_phizio_profilepic.setImageURI(selectedImage);
//                                        ivBasicImage.setImageURI(selectedImage);
//                                        iv_phizio_profilepic.invalidate();
//                                        BitmapDrawable drawable = (BitmapDrawable) iv_phizio_profilepic.getDrawable();
//                                        Bitmap photo = drawable.getBitmap();
//                                        photo = BitmapOperations.getResizedBitmap(photo,128);
//                                        repository.updatePhizioProfilePic(et_phizio_email.getText().toString(),photo);
//                                        dialog.setMessage("Uploading image, please wait");
//                                        dialog.show();
//                                }
//                                break;
//
//                        case 7:
//                                if(resultCode == RESULT_OK){
//                                        Bitmap photo = (Bitmap) imageReturnedIntent.getExtras().get("data");
//                                        photo = BitmapOperations.getResizedBitmap(photo,128);
//                                        iv_phizio_clinic_logo.setImageBitmap(photo);
//                                        repository.updatePhizioClinicLogoPic(et_phizio_email.getText().toString(),photo);
//                                        dialog.setMessage("Uploading image, please wait");
//                                        dialog.show();
//                                }
//                                break;
//                        case 8:
//                                if(resultCode == RESULT_OK){
//                                        Uri selectedImage = imageReturnedIntent.getData();
//                                        iv_phizio_clinic_logo.setImageURI(selectedImage);
//                                        iv_phizio_clinic_logo.invalidate();
//                                        try {
//                                                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
//                                                photo = BitmapOperations.getResizedBitmap(photo,128);
//                                                repository.updatePhizioClinicLogoPic(et_phizio_email.getText().toString(),photo);
//                                                dialog.setMessage("Uploading image, please wait");
//                                                dialog.show();
//                                        } catch (IOException e) {
//                                                e.printStackTrace();
//                                        }
//
//                                }
//                                break;
//                        case 31:
//                                if(resultCode == RESULT_OK){
//                                        setPhizioDetails();
//                                        et_phizio_name.setText(imageReturnedIntent.getStringExtra("et_phizio_name"));
//                                        et_specialization.setText(imageReturnedIntent.getStringExtra("et_specialization"));
//                                        et_degree.setText(imageReturnedIntent.getStringExtra("et_degree"));
//                                        et_experience.setText(imageReturnedIntent.getStringExtra("et_experience"));
//                                        et_phizio_phone.setText(imageReturnedIntent.getStringExtra("et_phizio_phone"));
//                                        et_clinic_name.setText(imageReturnedIntent.getStringExtra("et_clinic_name"));
//                                        et_address.setText(imageReturnedIntent.getStringExtra("et_address"));
//                                }
//                                break;
//                }
//        }
//
//        private void showToast(String message){
//                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
//        }
//
//
//        @Override
//        protected void onStart() {
//                super.onStart();
//        }
//
//
//        public Context getContext(){
//                return this;
//        }
//
//        @Override
//        public void onDetailsUpdated(Boolean response) {
//                dialog.dismiss();
//                if(response){
//                        showToast("Details updated");
//                        setPaleWhiteBackground(false);
//                        btn_update.setVisibility(View.INVISIBLE);
//                        btn_cancel_update.setVisibility(View.INVISIBLE);
//
//                        focuseEditTexts(false);
//                }
//                else {
//                        showToast("Error please try again later");
//                }
//        }
//
//        @Override
//        public void onProfilePictureUpdated(Boolean response) {
//                dialog.dismiss();
//        }
//
//
//        @Override
//        public void onClinicLogoUpdated(Boolean response) {
//                dialog.dismiss();
//                if(response){
////            showToast("Updated clinic logo");
//                        if(tv_update_clinic_logo!=null){
////                tv_update_clinic_logo.setText("Update Clinic Logo");
//                        }
//                }
//        }
//}









        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account_pannel);
        //Back Button
        ImageView button = findViewById(R.id.iv_back_phizio_profile);
        button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        finish();
        }
        });
        //Change Password button
        ImageView button1 = findViewById(R.id.change_password_btr);
        button1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), ChangePassword.class);
        startActivity(intent);
        }
        });
        //Delete Account Button
        ImageView button2 = findViewById(R.id.delete_btr);
        button2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), DeleteAccountOne.class);
                startActivity(intent);
        }
        });
        //Term and Conditions
//        ImageView button3 = findViewById(iv_go3);
//        button3.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//        Intent intent = new Intent(getApplicationContext(), TeamsAndConditions.class);
//        startActivity(intent);
//        }
//        });
        //Warrenty Details
//        ImageView button4 = findViewById(iv_go4);
//        button4.setOnClickListener(new View.OnClickListener() {
//        @Override
//        public void onClick(View view) {
//
//        }
//        });

        }
        }

