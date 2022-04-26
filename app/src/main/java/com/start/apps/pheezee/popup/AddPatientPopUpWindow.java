package com.start.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.ImageView;
import android.Manifest;
import android.content.Intent;
import androidx.appcompat.app.AlertDialog;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import static com.start.apps.pheezee.activities.PatientsView.REQ_CAMERA;
import static com.start.apps.pheezee.activities.PatientsView.REQ_GALLERY;
import android.provider.MediaStore;
import android.view.ViewGroup;
import android.widget.Toast;

import start.apps.pheezee.R;
import com.start.apps.pheezee.pojos.PatientDetailsData;
import com.start.apps.pheezee.room.Entity.PhizioPatients;
import com.start.apps.pheezee.utils.DateOperations;
import com.start.apps.pheezee.utils.TimeOperations;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddPatientPopUpWindow {
    onClickListner listner;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Context context;
    String json_phizioemail;
    Bitmap profile;
    boolean gallery_selected=false;
    boolean camera_selected=false;
    AlertDialog.Builder builder = null;
    final CharSequence[] items = { "Take Photo", "Choose from Library",
            "Cancel" };

    public AddPatientPopUpWindow(Context context, String json_phizioemail){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
    }

    public AddPatientPopUpWindow(Context context, String json_phizioemail, Bitmap photo){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
        this.profile = photo;
    }

    public void openAddPatientPopUpWindow(){
        final String[] case_description = {""};
        PopupWindow pw;
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        @SuppressLint("InflateParams") final View layout = inflater.inflate(R.layout.popup, null);


        pw = new PopupWindow(layout, width - 100,ViewGroup.LayoutParams.WRAP_CONTENT,true);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            pw.setElevation(10);
        }
        pw.setTouchable(true);
        pw.setOutsideTouchable(true);
        pw.setContentView(layout);
        pw.setFocusable(true);
        pw.setAnimationStyle(R.style.Animation);
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);


        final EditText patientName = layout.findViewById(R.id.patientName);
        final EditText patientAge = layout.findViewById(R.id.patientAge);
        final EditText caseDescription = layout.findViewById(R.id.contentDescription);
        final RadioGroup radioGroup = layout.findViewById(R.id.patientGender);
        final Spinner sp_case_des = layout.findViewById(R.id.sp_case_des);
        ImageView patient_profilepic = layout.findViewById(R.id.imageView4);
        ImageView patient_profilepic_image = layout.findViewById(R.id.profile_picture);
        //Adapter for spinner
        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_description));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_case_des.setAdapter(array_exercise_names);
        final String todaysDate = DateOperations.dateInMmDdYyyy();
        sp_case_des.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)getApplicationContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        }) ;
        Button addBtn = layout.findViewById(R.id.addBtn);
        Button cancelBtn = layout.findViewById(R.id.cancelBtn);
        if(this.profile!=null){
            patient_profilepic_image.setImageBitmap(this.profile);
            patient_profilepic_image.setVisibility(View.VISIBLE);
            patient_profilepic.setVisibility(View.GONE);
        }

        sp_case_des.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position<sp_case_des.getAdapter().getCount()-1){
                    caseDescription.setVisibility(View.GONE);
                    if(position!=0) {
                        case_description[0] = sp_case_des.getSelectedItem().toString();
                    }
                }
                if(position==sp_case_des.getAdapter().getCount()-1){
                    case_description[0] = "";
                    caseDescription.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int id = 0;
                RadioButton btn = layout.findViewById(radioGroup.getCheckedRadioButtonId());
                if(caseDescription.getVisibility()==View.VISIBLE){
                    case_description[0] = caseDescription.getText().toString();
                }
                if(sharedPref.getInt("maxid",-1)!=-1){
                    id = sharedPref.getInt("maxid",0);
                    id+=1;
                }
                String dateString = TimeOperations.getUTCdatetimeAsString();
                String patientid =  String.valueOf(id).concat(" ").concat(dateString);
                String patientname = patientName.getText().toString();
                String patientage = patientAge.getText().toString();
                if ((!patientname.equals("")) && (!patientid.equals("")) && (!patientage.equals(""))&& (!case_description[0].equals("")) && btn!=null) {
                    PhizioPatients patient = new PhizioPatients(patientid,patientname,"0",todaysDate,patientage,btn.getText().toString(),
                            case_description[0],"active","","empty", false);

                    PatientDetailsData data = new PatientDetailsData(json_phizioemail,patientid, patientname, "0",
                            todaysDate,patientage, btn.getText().toString(),case_description[0],"active", "", "empty");

                    if(sharedPref.getInt("maxid",-1)!=-1) {
                        editor.putInt("maxid", id);
                        editor.apply();
                    }
                    listner.onAddPatientClickListner(patient,data,true,profile);
                    pw.dismiss();
                }
                else {
                    listner.onAddPatientClickListner(null,null,false,null);
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });


        patient_profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Custom notification added by Haaris
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.set_profile_photo_layout);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);

                ImageView take_photo_asset =  dialog.findViewById(R.id.take_photo_asset);
                ImageView gallery_asset =  dialog.findViewById(R.id.gallery_asset);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);



                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(camera_selected==true)
                        {
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                pw.dismiss();
                                ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                                cameraIntent();
                            }
                            else {
                                pw.dismiss();
                                cameraIntent();
                            }

                            dialog.dismiss();

                        }else if(gallery_selected==true)
                        {
                            galleryIntent();
                            pw.dismiss();
                            dialog.dismiss();
                        }else
                        {
                            Toast.makeText(context, "Please select any one option.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                // On click on Continue
                take_photo_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=false;
                        camera_selected=true;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_unselected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_selected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));




                    }
                });

                // On click on Continue
                gallery_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=true;
                        camera_selected=false;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_selected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_unselected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));


                    }
                });

                dialog.show();

                // End
            }
        });

        patient_profilepic_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Custom notification added by Haaris
                // custom dialog
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.set_profile_photo_layout);


                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                lp.copyFrom(dialog.getWindow().getAttributes());
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

                dialog.getWindow().setAttributes(lp);

                ImageView take_photo_asset =  dialog.findViewById(R.id.take_photo_asset);
                ImageView gallery_asset =  dialog.findViewById(R.id.gallery_asset);

                Button Notification_Button_ok = (Button) dialog.findViewById(R.id.notification_ButtonOK);



                // On click on Continue
                Notification_Button_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {


                        if(camera_selected==true)
                        {
                            if(ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    == PackageManager.PERMISSION_DENIED) {
                                pw.dismiss();
                                ActivityCompat.requestPermissions(((Activity)context), new String[]{Manifest.permission.CAMERA}, 5);
                                cameraIntent();
                            }
                            else {
                                pw.dismiss();
                                cameraIntent();
                            }

                            dialog.dismiss();

                        }else if(gallery_selected==true)
                        {
                            galleryIntent();
                            pw.dismiss();
                            dialog.dismiss();
                        }else
                        {
                            Toast.makeText(context, "Please select any one option.", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

                // On click on Continue
                take_photo_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=false;
                        camera_selected=true;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_unselected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_selected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));




                    }
                });

                // On click on Continue
                gallery_asset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        gallery_selected=true;
                        camera_selected=false;
                        gallery_asset.setImageResource(context.getResources().getIdentifier("ic_gallery_selected", "drawable",context.getPackageName()));
                        take_photo_asset.setImageResource(context.getResources().getIdentifier("ic_camera_unselected", "drawable",context.getPackageName()));
                        Notification_Button_ok.setBackground(context.getResources().getDrawable(R.drawable.round_same_buttons));
                        Notification_Button_ok.setTextColor(ContextCompat.getColor(context,R.color.white));


                    }
                });

                dialog.show();

                // End
            }
        });
    }
    private void cameraIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            ((Activity) context).startActivityForResult(takePicture, 21);
        }else {
            ActivityCompat.requestPermissions(((Activity) context), new String[] {Manifest.permission.CAMERA}, REQ_CAMERA);
        }
    }
    private void galleryIntent() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        pickPhoto.putExtra("patientid",1);
            ((Activity) context).startActivityForResult(pickPhoto, 22);
        }else {
            ActivityCompat.requestPermissions(((Activity) context), new String[] {Manifest.permission.CAMERA}, REQ_GALLERY);
        }
    }



    public interface onClickListner{
        void onAddPatientClickListner(PhizioPatients patient, PatientDetailsData data, boolean isvalid,Bitmap photo);
    }

    public void setOnClickListner(onClickListner listner){
        this.listner = listner;
    }
}
