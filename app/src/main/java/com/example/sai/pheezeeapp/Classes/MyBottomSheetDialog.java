package com.example.sai.pheezeeapp.Classes;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.R;

@SuppressLint("ValidFragment")
public class MyBottomSheetDialog extends BottomSheetDialogFragment {

    BottomSheetBehavior behavior;

   TextView tv_patient_name_section,tv_patient_id_section,tv_date_of_join;
    ImageView iv_patient_profile_pic;
    String name,id,dateofjoin;
    Bitmap bitmap;

    @SuppressLint("ValidFragment")
    public MyBottomSheetDialog(String name, Bitmap bitmap, String id, String dateofjoin){
        this.name = name;
        this.id = id;
        this.bitmap = bitmap;
        this.dateofjoin = dateofjoin;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View layout = inflater.inflate(R.layout.layout_patient_options,container,false);

//        LinearLayout ll_edit_patient = layout.findViewById(R.id.ll_edit_patient_details);
//        ll_edit_patient.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PatientsView patientsView = new PatientsView();
//                Toast.makeText(getActivity(), "Hello", Toast.LENGTH_SHORT).show();
//            }
//        });
        findviews(layout);

        return layout;
    }

    private void findviews(View layout) {
                CoordinatorLayout layout1 = layout.findViewById(R.id.coordinator_patient_option);
        tv_patient_name_section = layout.findViewById(R.id.tv_patient_name_section);
        tv_patient_id_section = layout.findViewById(R.id.tv_patient_id_section);
        iv_patient_profile_pic = layout.findViewById(R.id.patient_profilepic_section);
        tv_date_of_join = layout.findViewById(R.id.tv_patient_joindate_section);
        tv_patient_name_section.setText(name);
        tv_date_of_join.setText(dateofjoin);
        String s = tv_patient_id_section.getText().toString();
        s=s+" "+id;
        tv_patient_id_section.setText(s);
        if(bitmap!=null)
            iv_patient_profile_pic.setImageBitmap(bitmap);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setWhiteNavigationBar(dialog);
        }


        return dialog;
    }

    public void setName(String name){
        tv_patient_name_section.setText(name);
    }

    public void setId(String id){
        tv_patient_id_section.setText(id);
    }
    public void setImage(Bitmap bitmap){
//        if(bitmap!=null)
//            iv_patient_profile_pic.setImageBitmap(bitmap);
    }

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
