package com.startoonlabs.apps.pheezee.popup;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;

public class EditPopUpWindow {
    Context context;
    PhizioPatients patient;
    onClickListner listner;
    String json_phizioemail;

    public EditPopUpWindow(final Activity context, PhizioPatients patient, String json_phizioemail){
        this.context = context;
        this.patient = patient;
        this.json_phizioemail = json_phizioemail;
    }

    public void openEditPopUpWindow(){
        PopupWindow pw;
        final String[] case_description = {""};
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();display.getSize(size);int width = size.x;int height = size.y;
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
        pw.showAtLocation(layout, Gravity.CENTER, 0, 0);

        final TextView patientName = layout.findViewById(R.id.patientName);
        final TextView patientId = layout.findViewById(R.id.patientId);
        final TextView patientAge = layout.findViewById(R.id.patientAge);
        final TextView caseDescription = layout.findViewById(R.id.contentDescription);
        final RadioGroup radioGroup = layout.findViewById(R.id.patientGender);
        RadioButton btn_male = layout.findViewById(R.id.radioBtn_male);
        RadioButton btn_female = layout.findViewById(R.id.radioBtn_female);
        final Spinner sp_case_des = layout.findViewById(R.id.sp_case_des);
        //Adapter for spinner
        ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(context, R.layout.support_simple_spinner_dropdown_item, context.getResources().getStringArray(R.array.case_description));
        array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sp_case_des.setAdapter(array_exercise_names);

        sp_case_des.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                InputMethodManager imm=(InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                return false;
            }
        }) ;
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
                    caseDescription.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button addBtn = layout.findViewById(R.id.addBtn);
        addBtn.setText("Update");
        patientId.setVisibility(View.GONE);
        final Button cancelBtn = layout.findViewById(R.id.cancelBtn);

        patientName.setText(patient.getPatientname());
        patientAge.setText(patient.getPatientage());
        if(patient.getPatientgender().equalsIgnoreCase("M"))
            radioGroup.check(btn_male.getId());
        else
            radioGroup.check(btn_female.getId());
        caseDescription.setText(patient.getPatientcasedes());
        case_description[0] = patient.getPatientcasedes();
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(caseDescription.getVisibility()==View.VISIBLE){
                    case_description[0] = caseDescription.getText().toString();
                }
                RadioButton btn = layout.findViewById(radioGroup.getCheckedRadioButtonId());
                String patientname = patientName.getText().toString();
                String patientage = patientAge.getText().toString();
                if ((!patientname.equals(""))  && (!patientage.equals(""))&& (!case_description[0].equals("")) && btn!=null) {
                    patient.setPatientname(patientname);
                    patient.setPatientage(patientage);
                    patient.setPatientcasedes(case_description[0]);
                    patient.setPatientgender(btn.getText().toString());
                    PatientDetailsData data = new PatientDetailsData(json_phizioemail, patient.getPatientid(),
                            patient.getPatientname(),patient.getNumofsessions(), patient.getDateofjoin(), patient.getPatientage(),
                            patient.getPatientgender(), patient.getPatientcasedes(), patient.getStatus(), patient.getPatientphone(), patient.getPatientprofilepicurl());
                    listner.onAddClickListner(patient,data,true);
                    pw.dismiss();
                }
                else {
                    listner.onAddClickListner(null,null,false);
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
        void onAddClickListner(PhizioPatients patients, PatientDetailsData data, boolean isvalid);
    }

    public void setOnClickListner(onClickListner listner){
        this.listner = listner;
    }
}
