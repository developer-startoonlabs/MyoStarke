package com.startoonlabs.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.adapters.BodyPartSelectionRecyclerViewAdapter;
import com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView;
import com.startoonlabs.apps.pheezee.classes.BodyPartSelectionModel;
import com.startoonlabs.apps.pheezee.classes.BodyPartWithMmtSelectionModel;
import com.startoonlabs.apps.pheezee.classes.DividerItemDecorator;
import com.startoonlabs.apps.pheezee.utils.MuscleOperation;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.bodypartSelected;
import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.orientationSelected;
import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.selectedPosition;

public class BodyPartSelection extends AppCompatActivity {
    //Drawable arry for the body part selection

    int[] myPartList = new int[]{R.drawable.elbow_part, R.drawable.knee_part,R.drawable.ankle_part,R.drawable.hip_part,
            R.drawable.wrist_part,R.drawable.shoulder_part,R.drawable.other_body_part};
    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    AlertDialog mdialog = null;
    RecyclerView bodyPartRecyclerView;
    JSONObject json_phizio = null;
    PopupWindow pw;

    //Adapter for body part recycler view
    BodyPartWithMmtRecyclerView bodyPartWithMmtRecyclerView;
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;

    ArrayList<BodyPartWithMmtSelectionModel> bodyPartWithMmtSelectionModels;
    LinearLayout ll_recent_bodypart;

    //Floating action button for done
    Button fab_done ;
    ImageView iv_back_body_part_selection;
    String[] string;
    private String str_orientation, str_exercise_name, str_muscle_name, str_body_orientation;
    private int int_repsselected = 0;
    public static String painscale="", muscletone="", exercisename="", commentsession="", symptoms="", musclename="", maxAngleSelected="", minAngleSelected="",maxEmgSelected="";     //musclename is actually exercise name and exercisename is musclename. As the flow changed.
    public static int repsselected=0, exercise_selected_position=-1;

//    GridLayoutManager manager;
    RecyclerView.LayoutManager manager;
    BodyPartSelectionRecyclerViewAdapter adapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);
        fab_done =  findViewById(R.id.fab_done);    //floating button Done.
        //THis string extracts the recently selected body part from the shared preference.
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        //Layout of the recently body part
        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);
        bodyPartRecyclerView = findViewById(R.id.bodyPartRecyclerView);
        bodyPartRecyclerView.setHasFixedSize(true);

//        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.devider_gridview_bodypart));
//        bodyPartRecyclerView.addItemDecoration(dividerItemDecoration);
//        bodyPartRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
//        bodyPartRecyclerView.setHasFixedSize(true);
//        Configuration config = getResources().getConfiguration();
//        if (config.smallestScreenWidthDp >= 600)
//        {
//            manager = new GridLayoutManager(this,2);    //development
//        }
//        else
//        {
//            manager = new GridLayoutManager(this,1);    //development
//        }
////        manager = new GridLayoutManager(this,1);    //development
//        bodyPartRecyclerView.setLayoutManager(manager);
//        bodyPartSelectionList = new ArrayList<>();
//        bodyPartWithMmtSelectionModels = new ArrayList<>();
//
//        string = getResources().getStringArray(R.array.bodyPartName);
//
//        for (int i=0;i<string.length;i++){
//            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(myPartList[i],string[i]);
//            bodyPartWithMmtSelectionModels.add(bp);
//        }
//        bodyPartWithMmtRecyclerView = new BodyPartWithMmtRecyclerView(bodyPartWithMmtSelectionModels,this);
//
//
//        bodyPartRecyclerView.setAdapter(bodyPartWithMmtRecyclerView);



        manager = new LinearLayoutManager(this);
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();
        bodyPartWithMmtSelectionModels = new ArrayList<>();
        string = getResources().getStringArray(R.array.bodyPartName);
        for (int i=0;i<string.length;i++){
            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(myPartList[i],string[i]);
            bodyPartWithMmtSelectionModels.add(bp);
        }
        adapter = new BodyPartSelectionRecyclerViewAdapter(bodyPartWithMmtSelectionModels,this);
        bodyPartRecyclerView.setAdapter(adapter);

        //Going back to the previous activity
        iv_back_body_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    @Override
    protected void onResume() {
        if(bodyPartRecyclerView!=null && bodyPartWithMmtRecyclerView!=null) {     //to refresh the view of the body part selection list when coming back from monitor screen.
            bodyPartRecyclerView.setAdapter(bodyPartWithMmtRecyclerView);
            selectedPosition=-1;
        }
        if(pw!=null){
            pw.dismiss();
        }
        super.onResume();
    }

    /**
     * This method is called when pressed done. Check weather any feild is not selected
     * @param view
     */
    public void startMonitorSession(View view) {
        if (selectedPosition != -1) {
            if (!orientationSelected.equalsIgnoreCase("")) {
                if (!musclename.equalsIgnoreCase("")) {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point size = new Point();
                    display.getSize(size);
                    int width = size.x;
                    int height = size.y;
                    LayoutInflater inflater = (LayoutInflater) BodyPartSelection.this
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    assert inflater != null;
                    @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.popup_comment_session, null);

                    pw = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
    //        pw.setHeight(height - 400);
                    pw.setWidth(width - 100);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        pw.setElevation(10);
                    }
                    pw.setTouchable(true);
                    pw.setOutsideTouchable(true);
                    pw.setContentView(layout);
                    pw.setFocusable(true);
                    pw.showAtLocation(view, Gravity.CENTER, 0, 0);
                    final Spinner sp_exercise_name = layout.findViewById(R.id.sp_exercise_name);
                    final Button btn_set_reference = layout.findViewById(R.id.comment_btn_setreference);
                    final Button btn_continue = layout.findViewById(R.id.comment_btn_continue);
                    final RadioGroup rg_body_orientation = layout.findViewById(R.id.rg_body_orientation);

                    //Adapter for spinner
                    ArrayAdapter<String> array_exercise_names = new ArrayAdapter<String>(BodyPartSelection.this, R.layout.support_simple_spinner_dropdown_item, MuscleOperation.getMusleNames(selectedPosition));
                    array_exercise_names.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                    sp_exercise_name.setAdapter(array_exercise_names);


                    //Item selected for spinner
                    sp_exercise_name.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if (position != 0) {
                                exercisename = sp_exercise_name.getSelectedItem().toString();
                            }
                            else {
                                exercisename="";
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });

                    btn_set_reference.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (!exercisename.equalsIgnoreCase("")) {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(BodyPartSelection.this);
                                LayoutInflater inflater = getLayoutInflater();
                                final View dialogLayout = inflater.inflate(R.layout.popup_set_reference, null);
                                final EditText et_max_angle = dialogLayout.findViewById(R.id.setreference_et_maxangle);
                                final EditText et_min_angle = dialogLayout.findViewById(R.id.setreference_et_minangle);
                                final EditText et_max_emg = dialogLayout.findViewById(R.id.setreference_et_maxemg);
                                final TextView tv_normal_max = dialogLayout.findViewById(R.id.tv_normal_max);
                                final TextView tv_normal_min = dialogLayout.findViewById(R.id.tv_normal_min);
                                final TextView tv_normal_max_text = dialogLayout.findViewById(R.id.normalMaxTest);
                                final TextView tv_normal_min_text = dialogLayout.findViewById(R.id.normalMinTest);
                                if (bodypartSelected != null) {
                                    int normal_min = ValueBasedColorOperations.getBodyPartMinValue(selectedPosition,exercise_selected_position);
                                    int normal_max = ValueBasedColorOperations.getBodyPartMaxValue(selectedPosition,exercise_selected_position);
                                    tv_normal_max.setText(String.valueOf(normal_max));
                                    tv_normal_min.setText(String.valueOf(normal_min));
                                }
                                if (bodypartSelected.equalsIgnoreCase("others")) {
                                    tv_normal_max.setVisibility(View.GONE);
                                    tv_normal_min.setVisibility(View.GONE);
                                    tv_normal_max_text.setVisibility(View.GONE);
                                    tv_normal_min_text.setVisibility(View.GONE);
                                }

                                builder.setPositiveButton("Submit", null);
                                builder.setView(dialogLayout);
                                mdialog = builder.create();
                                mdialog.setOnShowListener(new DialogInterface.OnShowListener() {

                                    @Override
                                    public void onShow(final DialogInterface dialog) {

                                        Button p = mdialog.getButton(AlertDialog.BUTTON_POSITIVE);
                                        p.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                boolean flag = true;

                                                maxEmgSelected = et_max_emg.getText().toString();
                                                maxAngleSelected = et_max_angle.getText().toString();
                                                minAngleSelected = et_min_angle.getText().toString();

                                                mdialog.dismiss();
                                            }
                                        });
                                    }
                                });

                                mdialog.show();
                            }
                            else {
                                showToast("Please select Muscle name!");
                            }
                        }
                    });
                    //buttom of the coment section pop up to continue to the session
                    btn_continue.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int temp_index = -1;
                            boolean flag = false, present = false;

                            RadioButton btn = layout.findViewById(rg_body_orientation.getCheckedRadioButtonId());
                            if (!exercisename.equalsIgnoreCase("") && btn!=null) {
                                pw.dismiss();
                                String bodyorientation = btn.getText().toString();
                                int body_orientation = 0;
                                if(bodyorientation.equalsIgnoreCase("sit")) body_orientation=1;
                                else if (bodyorientation.equalsIgnoreCase("stand")) body_orientation = 2;
                                else body_orientation=3;
                                Intent intent = new Intent(BodyPartSelection.this, MonitorActivity.class);
                                //To be started here i need to putextras in the intents and send them to the moitor activity
                                intent.putExtra("deviceMacAddress", getIntent().getStringExtra("deviceMacAddress"));
                                intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
                                intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
                                intent.putExtra("exerciseType", bodypartSelected);
                                intent.putExtra("orientation", orientationSelected);
                                intent.putExtra("bodyorientation",bodyorientation);
                                intent.putExtra("body_orientation",body_orientation);
                                intent.putExtra("dateofjoin",getIntent().getStringExtra("dateofjoin"));
                                Log.i("intent", intent.toString());
                                startActivity(intent);
                            } else {
                                if(btn==null){
                                    showToast("Please select Body orientation!");
                                }else {
                                    showToast("Please select Muscle name!");
                                }
                            }
                        }

                    });
                }
                else {
                    showToast("Please select Exercise name!");
                }
            }
            else {
                showToast("Please choose orientation!");
            }
        }
        else {
            showToast("Please choose a bodypart!");
        }
    }

    /**
     * Updates the values of some static variables
     */
    public static void refreshView(){
        reinitializeStatics();
        selectedPosition = -1;
        editor = preferences.edit();
        editor.putString("bodyPartClicked","");
        editor.commit();
    }

    public void setFabVisible(){
        fab_done.setVisibility(View.VISIBLE);
    }

    /**
     *
     * @param dp
     * @return int pixels
     */
    public int dpToPixel(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);

        return pixels;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        reinitializeStatics();
        selectedPosition=-1;
    }

    /**
     * Reinitialize static variables
     */
    public static void reinitializeStatics() {
        painscale=""; muscletone=""; exercisename=""; commentsession=""; symptoms=""; musclename="";orientationSelected=""; maxAngleSelected=""; minAngleSelected=""; maxEmgSelected="";
        repsselected=0;
    }

    /**
     * Shows toast
     * @param message
     */
    private void showToast(String message){
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
