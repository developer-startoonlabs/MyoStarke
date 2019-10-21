package com.startoonlabs.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.utils.DateOperations;

import static com.facebook.FacebookSdk.getApplicationContext;

public class AddPatientPopUpWindow {
    onClickListner listner;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;
    Context context;
    String json_phizioemail;

    public AddPatientPopUpWindow(Context context, String json_phizioemail){
        this.context = context;
        this.json_phizioemail = json_phizioemail;
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPref.edit();
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

        pw = new PopupWindow(layout);
        pw.setHeight(height - 400);
        pw.setWidth(width - 100);


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
        final EditText patientId = layout.findViewById(R.id.patientId);
        if(sharedPref.getInt("maxid",-1)!=-1){
            int id = sharedPref.getInt("maxid",0);
            id+=1;
            patientId.setEnabled(false);
            patientId.setText(String.valueOf(id));
        }
        final EditText patientAge = layout.findViewById(R.id.patientAge);
        final EditText caseDescription = layout.findViewById(R.id.contentDescription);
        final RadioGroup radioGroup = layout.findViewById(R.id.patientGender);
        final Spinner sp_case_des = layout.findViewById(R.id.sp_case_des);
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
                Log.i("casedes",case_description[0]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("pojo","pojo1");
                RadioButton btn = layout.findViewById(radioGroup.getCheckedRadioButtonId());
                if(caseDescription.getVisibility()==View.VISIBLE){
                    case_description[0] = caseDescription.getText().toString();
                }
                String patientid =  patientId.getText().toString();
                String patientname = patientName.getText().toString();
                String patientage = patientAge.getText().toString();
                if ((!patientname.equals("")) && (!patientid.equals("")) && (!patientage.equals(""))&& (!case_description[0].equals("")) && btn!=null) {
                    PhizioPatients patient = new PhizioPatients(patientid,patientname,"0",todaysDate,patientage,btn.getText().toString(),
                            case_description[0],"active","","empty");

                    PatientDetailsData data = new PatientDetailsData(json_phizioemail,patientid, patientname, "0",
                            todaysDate,patientage, btn.getText().toString(),case_description[0],"active", "", "empty");

                    if(sharedPref.getInt("maxid",-1)!=-1) {
                        editor.putInt("maxid", Integer.parseInt(patientid));
                        editor.apply();
                    }
                    listner.onAddPatientClickListner(patient,data,true);
                    pw.dismiss();
                }
                else {
                    listner.onAddPatientClickListner(null,null,false);
                }

            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pw.dismiss();
            }
        });
    }



    public interface onClickListner{
        void onAddPatientClickListner(PhizioPatients patient, PatientDetailsData data, boolean isvalid);
    }

    public void setOnClickListner(onClickListner listner){
        this.listner = listner;
    }
}
