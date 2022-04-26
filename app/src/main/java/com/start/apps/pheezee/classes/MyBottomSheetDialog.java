package com.start.apps.pheezee.classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import start.apps.pheezee.R;
import com.start.apps.pheezee.activities.PatientsView;
import com.start.apps.pheezee.room.Entity.PhizioPatients;

/**
 * Bottom sheet dialog that opens when clicked on three dots
 */
@SuppressLint("ValidFragment")
public class MyBottomSheetDialog extends BottomSheetDialogFragment {

    BottomSheetBehavior behavior;

   TextView tv_patient_name_section,tv_patient_id_section,tv_date_of_join,tv_Image_Container;
    ImageView iv_patient_profile_pic;
    Bitmap bitmap;

    PhizioPatients patient;

    LinearLayout ll_report,ll_edit_patient_details,ll_delete_patient,ll_archive_patient, ll_start_sceduled_session;

    @SuppressLint("ValidFragment")
    public MyBottomSheetDialog(Bitmap bitmap, PhizioPatients patient){
        this.bitmap = bitmap;
        this.patient = patient;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.layout_patient_options,container,false);
        findviews(layout);
        return layout;
    }

    /**
     * Initializes views
     * @param layout
     */
    private void findviews(View layout) {
        CoordinatorLayout layout1 = layout.findViewById(R.id.coordinator_patient_option);
        tv_patient_name_section = layout.findViewById(R.id.tv_patient_name_section);
        tv_patient_id_section = layout.findViewById(R.id.tv_patient_id_section);
        iv_patient_profile_pic = layout.findViewById(R.id.patient_profilepic_section);
//        tv_date_of_join = layout.findViewById(R.id.tv_patient_joindate_section);
        tv_Image_Container = layout.findViewById(R.id.tv_Image_container);

        ll_report = layout.findViewById(R.id.ll_report);
        ll_edit_patient_details = layout.findViewById(R.id.ll_edit_patient_details);
        ll_delete_patient = layout.findViewById(R.id.ll_delete_patient);
//        ll_archive_patient = layout.findViewById(R.id.ll_archive_patient);
        ll_start_sceduled_session = layout.findViewById(R.id.ll_start_sceduled_session);

        if(patient.isSceduled()){
            ll_start_sceduled_session.setVisibility(View.VISIBLE);
        }else {
            ll_start_sceduled_session.setVisibility(View.GONE);
        }

        /**
         * Calls update patient
         */
//        ll_archive_patient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ((PatientsView)getActivity()).updatePatientStatus(patient);
//            }
//        });

        /**
         * calls openreport
         */
        ll_report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientsView)getActivity()).openReportActivity(patient.getPatientid(),patient.getPatientname(), patient.getDateofjoin());
            }
        });

        /**
         * calls editThePatientDetails
         */
        ll_edit_patient_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientsView)getActivity()).editThePatientDetails(patient);
            }
        });

        /**
         * calls deletePatient
         */
        ll_delete_patient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientsView)getActivity()).deletePatient(patient);
            }
        });

        ll_start_sceduled_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PatientsView)getActivity()).startSceduledSession(patient);
            }
        });
        tv_patient_name_section.setText(patient.getPatientname());
//        tv_date_of_join.setText( DateOperations.getDateInMonthAndDate(patient.getDateofjoin()));
        String s = tv_patient_id_section.getText().toString();
        if(patient.getPatientid().length()>3){
            String temp = patient.getPatientid().substring(0,3);
            s = s+temp+"xxx";
        }else {
            s = s + " " + patient.getPatientid();
        }
        tv_patient_id_section.setText(patient.getPatientcasedes().toString());
        if(bitmap!=null)
            iv_patient_profile_pic.setImageBitmap(bitmap);
        else {
            Log.i("NOt change","no change");
            if(patient.getPatientname().length()==1 || patient.getPatientname().length()==2)
                tv_Image_Container.setText(patient.getPatientname().toUpperCase());
            else
                tv_Image_Container.setText(patient.getPatientname().substring(0,2).toUpperCase());
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }
        return dialog;
    }

    /**
     * making the home buttons to while, in sync with the dialog
     * @param dialog
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setWhiteNavigationBar(@NonNull Dialog dialog) {
        Window window = dialog.getWindow();
        if (window != null) {
            DisplayMetrics metrics = new DisplayMetrics();
            window.getWindowManager().getDefaultDisplay().getMetrics(metrics);

            GradientDrawable dimDrawable = new GradientDrawable();
            // ...customize your dim effect here

            GradientDrawable navigationBarDrawable = new GradientDrawable();
            navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
            navigationBarDrawable.setColor(Color.WHITE);

            Drawable[] layers = {dimDrawable, navigationBarDrawable};

            LayerDrawable windowBackground = new LayerDrawable(layers);
            windowBackground.setLayerInsetTop(1, metrics.heightPixels);

            window.setBackgroundDrawable(windowBackground);
            window.getDecorView().setFitsSystemWindows(false);
            // dark navigation bar icons
            View decorView = window.getDecorView();
            decorView.setSystemUiVisibility(decorView.getSystemUiVisibility() | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }
}
