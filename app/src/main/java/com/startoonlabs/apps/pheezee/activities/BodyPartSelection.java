package com.startoonlabs.apps.pheezee.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.adapters.BodyPartSelectionRecyclerViewAdapter;
import com.startoonlabs.apps.pheezee.classes.BodyPartSelectionModel;
import com.startoonlabs.apps.pheezee.classes.BodyPartWithMmtSelectionModel;

import java.util.ArrayList;

public class BodyPartSelection extends AppCompatActivity {
    //Drawable arry for the body part selection


    RecyclerView bodyPartRecyclerView;

    //Adapter for body part recycler view
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;

    ArrayList<BodyPartWithMmtSelectionModel> bodyPartWithMmtSelectionModels;

    //Floating action button for done
    ImageView iv_back_body_part_selection;
    String[] string;
    private String str_orientation, str_exercise_name, str_muscle_name, str_body_orientation, str_body_part, str_max_emg_selected="", min_angle_selected="", max_angle_selected="";
    private int int_repsselected = 0, exercise_selected_postion=-1, body_part_selected_position=-1;

//    GridLayoutManager manager;
    RecyclerView.LayoutManager manager;
    BodyPartSelectionRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);
        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);
        bodyPartRecyclerView = findViewById(R.id.bodyPartRecyclerView);
        bodyPartRecyclerView.setHasFixedSize(true);

        manager = new LinearLayoutManager(this);
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();
        bodyPartWithMmtSelectionModels = new ArrayList<>();
        string = getResources().getStringArray(R.array.bodyPartName);
        for (int i=0;i<string.length;i++){
            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(string[i]);
            bodyPartWithMmtSelectionModels.add(bp);
        }
        adapter = new BodyPartSelectionRecyclerViewAdapter(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bodyPartRecyclerView.setAdapter(adapter);
            }
        },100);


        //Going back to the previous activity
        iv_back_body_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adapter.onSetOptionsSelectedListner(new BodyPartSelectionRecyclerViewAdapter.onBodyPartOptionsSelectedListner() {
            @Override
            public void onBodyPartSelected(String bodypart) {
                str_body_part = bodypart;
            }

            @Override
            public void onOrientationSelected(String orientation) {
                str_orientation = orientation;
            }

            @Override
            public void onBodyOrientationSelected(String body_orientation) {
                str_body_orientation = body_orientation;
            }

            @Override
            public void onExerciseNameSelected(String exercise_name) {
                str_exercise_name = exercise_name;
            }

            @Override
            public void onMuscleNameSelected(String muscle_name) {
                str_muscle_name = muscle_name;
            }

            @Override
            public void onGoalSelected(int reps) {
                int_repsselected = reps;
            }

            @Override
            public void onMaxEmgUpdated(String max_emg_updated) {
                str_max_emg_selected = max_emg_updated;
            }

            @Override
            public void onMaxAngleUpdated(String max_angle_updated) {
                max_angle_selected = max_angle_updated;
            }

            @Override
            public void onMinAngleUpdated(String min_angle_updated) {
                min_angle_selected = min_angle_updated;
            }

            @Override
            public void onBodyPartSelectedPostion(int position) {
                body_part_selected_position = position;
            }

            @Override
            public void onExerciseSelectedPostion(int position) {
                exercise_selected_postion = position;
            }
        });
    }

    @Override
    protected void onResume() {
        if(adapter!=null){
            Log.i("called","called");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    bodyPartRecyclerView.setAdapter(adapter);
                }
            },100);
        }
        reinitializeStatics();
        super.onResume();
    }

    /**
     * This method is called when pressed done. Check weather any feild is not selected
     * @param view
     */
    public void startMonitorSession(View view) {
        if(isValid()){
            Log.i("body part",str_body_part);
            Log.i("body orientation",str_orientation);
            Log.i("body part orientation",str_body_orientation);
            Log.i("body exercise name",str_exercise_name);
            Log.i("body str_muscle_name",str_muscle_name);
            Log.i("body part",String.valueOf(int_repsselected));
            Log.i("body str_max_emg",str_max_emg_selected);
            Log.i("body min_angle_selected",min_angle_selected);
            Log.i("body max_angle_selected",max_angle_selected);
            int body_orientation = 0;
            if(str_body_orientation.equalsIgnoreCase("sit")) body_orientation=1;
            else if (str_body_orientation.equalsIgnoreCase("stand")) body_orientation = 2;
            else body_orientation=3;
            Intent intent = new Intent(BodyPartSelection.this, MonitorActivity.class);
                                //To be started here i need to putextras in the intents and send them to the moitor activity
            intent.putExtra("deviceMacAddress", getIntent().getStringExtra("deviceMacAddress"));
            intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
            intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
            intent.putExtra("exerciseType", str_body_part);
            intent.putExtra("orientation", str_orientation);
            intent.putExtra("bodyorientation",str_body_orientation);
            intent.putExtra("body_orientation",body_orientation);
            intent.putExtra("dateofjoin",getIntent().getStringExtra("dateofjoin"));
            intent.putExtra("repsselected",int_repsselected);
            intent.putExtra("exercisename",str_exercise_name);
            intent.putExtra("musclename",str_muscle_name);
            intent.putExtra("maxemgselected",str_max_emg_selected);
            intent.putExtra("maxangleselected",max_angle_selected);
            intent.putExtra("minangleselected",min_angle_selected);
            intent.putExtra("exerciseposition",exercise_selected_postion);
            intent.putExtra("bodypartposition",body_part_selected_position);
            startActivity(intent);

        }else {
            showToast(getInvalidMessage());
        }

//        if (selectedPosition != -1) {
//            if (!orientationSelected.equalsIgnoreCase("")) {
//                if (!musclename.equalsIgnoreCase("")) {
//                    Display display = getWindowManager().getDefaultDisplay();
//                    Point size = new Point();
//                    display.getSize(size);
//                    int width = size.x;
//                    int height = size.y;
//                    LayoutInflater inflater = (LayoutInflater) BodyPartSelection.this
//                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                    assert inflater != null;
//                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.popup_comment_session, null);
//
//                    pw = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
//    //        pw.setHeight(height - 400);
//                    pw.setWidth(width - 100);
//
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        pw.setElevation(10);
//                    }
//                    pw.setTouchable(true);
//                    pw.setOutsideTouchable(true);
//                    pw.setContentView(layout);
//                    pw.setFocusable(true);
//                    pw.showAtLocation(view, Gravity.CENTER, 0, 0);
//                    final Spinner sp_exercise_name = layout.findViewById(R.id.sp_exercise_name);
//                    final Button btn_set_reference = layout.findViewById(R.id.comment_btn_setreference);
//                    final Button btn_continue = layout.findViewById(R.id.comment_btn_continue);
//                    final RadioGroup rg_body_orientation = layout.findViewById(R.id.rg_body_orientation);
//
//                    //Adapter for spinner
//                    ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(BodyPartSelection.this, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getMusleNames(selectedPosition));
//                    array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//                    sp_exercise_name.setAdapter(array_exercise_names);
//
//
//                    //Item selected for spinner
//                    sp_exercise_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//                        @Override
//                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                            if (position != 0) {
//                                exercisename = sp_exercise_name.getSelectedItem().toString();
//                            }
//                            else {
//                                exercisename="";
//                            }
//                        }
//
//                        @Override
//                        public void onNothingSelected(AdapterView<?> parent) {
//
//                        }
//                    });
//
//                    btn_set_reference.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (!exercisename.equalsIgnoreCase("")) {
//                                final AlertDialog.Builder builder = new AlertDialog.Builder(BodyPartSelection.this);
//                                LayoutInflater inflater = getLayoutInflater();
//                                final View dialogLayout = inflater.inflate(R.layout.popup_set_reference, null);
//                                final EditText et_max_angle = dialogLayout.findViewById(R.id.setreference_et_maxangle);
//                                final EditText et_min_angle = dialogLayout.findViewById(R.id.setreference_et_minangle);
//                                final EditText et_max_emg = dialogLayout.findViewById(R.id.setreference_et_maxemg);
//                                final TextView tv_normal_max = dialogLayout.findViewById(R.id.tv_normal_max);
//                                final TextView tv_normal_min = dialogLayout.findViewById(R.id.tv_normal_min);
//                                final TextView tv_normal_max_text = dialogLayout.findViewById(R.id.normalMaxTest);
//                                final TextView tv_normal_min_text = dialogLayout.findViewById(R.id.normalMinTest);
//                                if (bodypartSelected != null) {
//                                    int normal_min = ValueBasedColorOperations.getBodyPartMinValue(selectedPosition,exercise_selected_position);
//                                    int normal_max = ValueBasedColorOperations.getBodyPartMaxValue(selectedPosition,exercise_selected_position);
//                                    tv_normal_max.setText(String.valueOf(normal_max));
//                                    tv_normal_min.setText(String.valueOf(normal_min));
//                                }
//                                if (bodypartSelected.equalsIgnoreCase("others")) {
//                                    tv_normal_max.setVisibility(View.GONE);
//                                    tv_normal_min.setVisibility(View.GONE);
//                                    tv_normal_max_text.setVisibility(View.GONE);
//                                    tv_normal_min_text.setVisibility(View.GONE);
//                                }
//
//                                builder.setPositiveButton("Submit", null);
//                                builder.setView(dialogLayout);
//                                mdialog = builder.create();
//                                mdialog.setOnShowListener(new DialogInterface.OnShowListener() {
//
//                                    @Override
//                                    public void onShow(final DialogInterface dialog) {
//
//                                        Button p = mdialog.getButton(AlertDialog.BUTTON_POSITIVE);
//                                        p.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                boolean flag = true;
//
//                                                maxEmgSelected = et_max_emg.getText().toString();
//                                                maxAngleSelected = et_max_angle.getText().toString();
//                                                minAngleSelected = et_min_angle.getText().toString();
//
//                                                mdialog.dismiss();
//                                            }
//                                        });
//                                    }
//                                });
//
//                                mdialog.show();
//                            }
//                            else {
//                                showToast("Please select Muscle name!");
//                            }
//                        }
//                    });
//                    //buttom of the coment section pop up to continue to the session
//                    btn_continue.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            int temp_index = -1;
//                            boolean flag = false, present = false;
//
//                            RadioButton btn = layout.findViewById(rg_body_orientation.getCheckedRadioButtonId());
//                            if (!exercisename.equalsIgnoreCase("") && btn!=null) {
//                                pw.dismiss();
//                                String bodyorientation = btn.getText().toString();
//                                int body_orientation = 0;
//                                if(bodyorientation.equalsIgnoreCase("sit")) body_orientation=1;
//                                else if (bodyorientation.equalsIgnoreCase("stand")) body_orientation = 2;
//                                else body_orientation=3;
//                                Intent intent = new Intent(BodyPartSelection.this, MonitorActivity.class);
//                                //To be started here i need to putextras in the intents and send them to the moitor activity
//                                intent.putExtra("deviceMacAddress", getIntent().getStringExtra("deviceMacAddress"));
//                                intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
//                                intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
//                                intent.putExtra("exerciseType", bodypartSelected);
//                                intent.putExtra("orientation", orientationSelected);
//                                intent.putExtra("bodyorientation",bodyorientation);
//                                intent.putExtra("body_orientation",body_orientation);
//                                intent.putExtra("dateofjoin",getIntent().getStringExtra("dateofjoin"));
//                                Log.i("intent", intent.toString());
//                                startActivity(intent);
//                            } else {
//                                if(btn==null){
//                                    showToast("Please select Body orientation!");
//                                }else {
//                                    showToast("Please select Muscle name!");
//                                }
//                            }
//                        }
//
//                    });
//                }
//                else {
//                    showToast("Please select Exercise name!");
//                }
//            }
//            else {
//                showToast("Please choose orientation!");
//            }
//        }
//        else {
//            showToast("Please choose a bodypart!");
//        }
    }

    public boolean isValid(){
        if(str_body_part!=null && str_orientation!=null && str_body_orientation!=null && str_exercise_name!=null && str_muscle_name!=null){
            return true;
        }
        else {
            return false;
        }
    }

    public String getInvalidMessage(){
        if(str_body_part==null){
            return "Please select body part!";
        }
        else if (str_orientation==null){
            return "Please select body part side!";
        }
        else if (str_body_orientation==null){
            return "Please select body position";
        }else if (str_exercise_name==null){
            return "Please select exercise name";
        }
        else if (str_muscle_name==null){
            return "Please select muscle name";
        }else {
            return "Please fill all details";
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * Reinitialize static variables
     */
    public void reinitializeStatics() {
        str_orientation=null; str_exercise_name=null; str_muscle_name=null; str_body_orientation=null; str_body_part=null;
        str_max_emg_selected="";min_angle_selected=""; max_angle_selected="";
        int_repsselected = 0; exercise_selected_postion=-1; body_part_selected_position=-1;
    }

    /**
     * Shows toast
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
