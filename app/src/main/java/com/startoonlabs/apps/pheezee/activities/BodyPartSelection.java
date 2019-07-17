package com.startoonlabs.apps.pheezee.activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.startoonlabs.apps.pheezee.classes.BodyPartWithMmtSelectionModel;
import com.startoonlabs.apps.pheezee.classes.BodyPartSelectionModel;
import com.startoonlabs.apps.pheezee.classes.DividerItemDecorator;
import com.startoonlabs.apps.pheezee.R;
import com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView;
import com.startoonlabs.apps.pheezee.services.MqttHelper;
import com.startoonlabs.apps.pheezee.utils.PatientOperations;
import com.startoonlabs.apps.pheezee.utils.RegexOperations;
import com.startoonlabs.apps.pheezee.utils.ValueBasedColorOperations;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.bodypartSelected;
import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.orientationSelected;
import static com.startoonlabs.apps.pheezee.adapters.BodyPartWithMmtRecyclerView.selectedPosition;

public class BodyPartSelection extends AppCompatActivity {

    //Drawable arry for the body part selection

    int[] myPartList = new int[]{R.drawable.elbow_part, R.drawable.knee_part,R.drawable.ankle_part,R.drawable.hip_part,R.drawable.wrist_part,R.drawable.shoulder_part,R.drawable.other_body_part};


    static SharedPreferences preferences;
    static SharedPreferences.Editor editor;
    AlertDialog mdialog = null;
    RecyclerView bodyPartRecyclerView;
    MqttHelper mqttHelper;
    JSONObject json_phizio = null;

    //Adapter for body part recycler view
    BodyPartWithMmtRecyclerView bodyPartWithMmtRecyclerView;
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;

    ArrayList<BodyPartWithMmtSelectionModel> bodyPartWithMmtSelectionModels;
    LinearLayout ll_recent_bodypart;

    //Floating action button for done
    FloatingLayout fab_done ;

    TextView tv_body_part_recent;
    ImageView iv_back_body_part_selection;
    String[] string;
    String str_recent;public static String painscale="", muscletone="", exercisename="", commentsession="", symptoms="", musclename="";
    public static int repsselected=0;

    FrameLayout fl_fab_background;
    boolean flag_recent = false;

//    GridLayoutManager manager;
    RecyclerView.LayoutManager manager;
    private String mqtt_publish_message_reference = "phizio/calibration/addpatientsession";
    private String mqtt_publish_message_reference_response = "phizio/calibration/addpatientsession/response";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);
        tv_body_part_recent = findViewById(R.id.tv_recently_items); //this textview is visible when no recent body part is selected.
        fab_done =  findViewById(R.id.fab_done);    //floating button Done.
        fl_fab_background = findViewById(R.id.fl_fab_background);

        //shared preference variable
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();

        //Mqtt helper class object.
        mqttHelper = new MqttHelper(this);


        //THis string extracts the recently selected body part from the shared preference.
        str_recent = preferences.getString("recently","");
        try {
            json_phizio = new JSONObject(preferences.getString("phiziodetails",""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        //Layout of the recently body part
        ll_recent_bodypart = findViewById(R.id.ll_recent_section);


        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);


        Log.i("str_recent",str_recent);
        if (str_recent.equals("")){
            tv_body_part_recent.setVisibility(View.VISIBLE);
        }
        else {
            try {
                JSONArray array = new JSONArray(str_recent);

                for (int i=0;i<array.length();i++){
                    JSONObject object1 = array.getJSONObject(i);
                    if(object1.getString("patientid").equals(getPatientId())) {
                        JSONArray array1 = new JSONArray(object1.getString("recent"));
                        ImageView iv_recent_body[] = new ImageView[array1.length()];
                        for (int j=0;j<array1.length();j++) {
                            flag_recent = true;
                            int width = dpToPixel(95);
                            iv_recent_body[j] = new ImageView(getApplicationContext());
                            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                                    width, LinearLayout.LayoutParams.WRAP_CONTENT);
                            JSONObject object = array1.getJSONObject(j);
                            int res_id = object.getInt("res_id");
                            String pos = object.getString("position");
                            iv_recent_body[j].setImageResource(myPartList[Integer.parseInt(pos)]);
                            iv_recent_body[j].setId(res_id);
                            int left_padding = dpToPixel(20);
                            iv_recent_body[j].setPadding(left_padding, 0, 0, 0);
                            Log.i("res_id", res_id + "");
                            iv_recent_body[j].setTag(j);
                            iv_recent_body[j].setScaleType(ImageView.ScaleType.FIT_XY);
                            iv_recent_body[j].setOnClickListener(onclicklistner);
                            ll_recent_bodypart.addView(iv_recent_body[j], layoutParams);
                        }
                    }
                }
                if(!flag_recent){
                    tv_body_part_recent.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        bodyPartRecyclerView = findViewById(R.id.bodyPartRecyclerView);
        bodyPartRecyclerView.setHasFixedSize(true);

        RecyclerView.ItemDecoration dividerItemDecoration = new DividerItemDecorator(ContextCompat.getDrawable(this, R.drawable.devider_gridview_bodypart));
        bodyPartRecyclerView.addItemDecoration(dividerItemDecoration);
        bodyPartRecyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.HORIZONTAL));
        bodyPartRecyclerView.setHasFixedSize(true);
        Configuration config = getResources().getConfiguration();
        if (config.smallestScreenWidthDp >= 600)
        {
            manager = new GridLayoutManager(this,2);    //development
        }
        else
        {
            manager = new GridLayoutManager(this,1);    //development
        }
//        manager = new GridLayoutManager(this,1);    //development
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();
        bodyPartWithMmtSelectionModels = new ArrayList<>();

        string = getResources().getStringArray(R.array.bodyPartName);

        for (int i=0;i<string.length;i++){
            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(myPartList[i],string[i]);
            bodyPartWithMmtSelectionModels.add(bp);
        }
        bodyPartWithMmtRecyclerView = new BodyPartWithMmtRecyclerView(bodyPartWithMmtSelectionModels,this);


        bodyPartRecyclerView.setAdapter(bodyPartWithMmtRecyclerView);



        //This listner is used to disable and enable the floating layout while scrolling
//        bodyPartRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                Animation animation_up = AnimationUtils.loadAnimation(BodyPartSelection.this, R.anim.slide_up_dialog);
//                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
//                    fl_fab_background.setVisibility(View.VISIBLE);
//                    fl_fab_background.startAnimation(animation_up);
//                }
//                super.onScrollStateChanged(recyclerView, newState);
//            }
//
//            @Override
//            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//                Animation animation_down = AnimationUtils.loadAnimation(BodyPartSelection.this, R.anim.slide_out_down);
//                Animation animation_up = AnimationUtils.loadAnimation(BodyPartSelection.this, R.anim.slide_up_dialog);
//                super.onScrolled(recyclerView, dx, dy);
//
//                if(!recyclerView.canScrollVertically(1)){
//                    fl_fab_background.setVisibility(View.VISIBLE);
////                    fl_fab_background.startAnimation(animation_up);
//                }
//                else if(dy>0 && fab_done.isShown()){
//                    fl_fab_background.setVisibility(View.INVISIBLE);
//                    fl_fab_background.startAnimation(animation_down);
//                }
//                else if(dy<0){
//                    fl_fab_background.setVisibility(View.VISIBLE);
//                    fl_fab_background.startAnimation(animation_up);
//                }
//            }
//        });



        //Going back to the previous activity
        iv_back_body_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    //On click listner on all the images inside the horizontal view of body part selection screen
    View.OnClickListener onclicklistner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView imageView = ((ImageView)v);
            int pos = Integer.parseInt(imageView.getTag().toString());
            Log.i("tag",String.valueOf(pos));
            JSONArray array = null;
            try {
                array = new JSONArray(str_recent);
                for (int i=0;i<array.length();i++){
                    JSONObject object1 = array.getJSONObject(i);
                    if(object1.getString("patientid").equals(getPatientId())) {
                        JSONArray array_exercises = new JSONArray(object1.getString("recent"));
                        JSONObject object = array_exercises.getJSONObject(pos);
                        Toast.makeText(BodyPartSelection.this,object.toString(),Toast.LENGTH_LONG).show();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
    /**
     *
     * @return String This method returns the patient id from by getting it from the intent.
     */
    public String getPatientId(){
        String patientID = null;

        if(!getIntent().getStringExtra("patientId").equals("")) {
            patientID = getIntent().getStringExtra("patientId");
            Log.i("test",getIntent().getStringExtra("patientId"));
        }

        return patientID;
    }


    @Override
    protected void onResume() {
        if(bodyPartRecyclerView!=null && bodyPartWithMmtRecyclerView!=null) {     //to refresh the view of the body part selection list when coming back from monitor screen.
            bodyPartRecyclerView.setAdapter(bodyPartWithMmtRecyclerView);
            selectedPosition=-1;
        }
        super.onResume();
    }

    /**
     * This method is called when pressed done.
     * @param view
     */
    public void startMonitorSession(View view) {
        if (selectedPosition != -1 && !orientationSelected.equalsIgnoreCase("")) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            int width = size.x;
            int height = size.y;
            LayoutInflater inflater = (LayoutInflater) BodyPartSelection.this
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            @SuppressLint("InflateParams") View layout = inflater.inflate(R.layout.popup_comment_session, null);

            final PopupWindow pw = new PopupWindow(layout, ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);
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
            final EditText et_exercise_name = layout.findViewById(R.id.comment_exercise_name);
            final EditText et_comment_section = layout.findViewById(R.id.comment_et_comment);
            final Button btn_set_reference = layout.findViewById(R.id.comment_btn_setreference);
            final Button btn_continue = layout.findViewById(R.id.comment_btn_continue);
            btn_set_reference.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JSONObject json_reference = PatientOperations.checkReferenceDone(orientationSelected, BodyPartSelection.this, getPatientId(), bodypartSelected);
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
                        int normal_min = ValueBasedColorOperations.getBodyPartMinValue(bodypartSelected);
                        int normal_max = ValueBasedColorOperations.getBodyPartMaxValue(bodypartSelected);
                        tv_normal_max.setText(String.valueOf(normal_max));
                        tv_normal_min.setText(String.valueOf(normal_min));
                    }
                    if (bodypartSelected.equalsIgnoreCase("others")) {
                        tv_normal_max.setVisibility(View.GONE);
                        tv_normal_min.setVisibility(View.GONE);
                        tv_normal_max_text.setVisibility(View.GONE);
                        tv_normal_min_text.setVisibility(View.GONE);
                    }

                    if (json_reference != null) {
                        try {
                            et_max_emg.setText(json_reference.getString("maxemg"));
                            et_max_angle.setText(json_reference.getString("maxangle"));
                            et_min_angle.setText(json_reference.getString("minangle"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

                                    String maxEmg = et_max_emg.getText().toString();
                                    String maxAngle = et_max_angle.getText().toString();
                                    String minEmg = String.valueOf(0);
                                    String minAngle = et_min_angle.getText().toString();
                                    flag = RegexOperations.checkIfNumeric(maxEmg);
                                    if (flag)
                                        flag = RegexOperations.checkIfNumeric(minAngle);
                                    if (flag)
                                        flag = RegexOperations.checkIfNumeric(maxAngle);
                                    if (flag)
                                        sendData(maxAngle, minAngle, maxEmg, minEmg);
                                    else
                                        Toast.makeText(BodyPartSelection.this, "Invalid Entry", Toast.LENGTH_SHORT).show();

                                    mdialog.dismiss();
                                }
                            });
                        }
                    });

                    mdialog.show();
                }
            });


            //buttom of the coment section pop up to continue to the session
            btn_continue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    exercisename = et_exercise_name.getText().toString();
                    commentsession = et_comment_section.getText().toString();


                    pw.dismiss();
                    int temp_index = -1;
                    boolean flag = false, present = false;
                    if (selectedPosition != -1) {
                        int position_list_selected = selectedPosition;
                        String str_time = repsselected + "";
                        editor = preferences.edit();

                        JSONArray array = new JSONArray();
                        JSONArray array1 = new JSONArray();
                        JSONObject object = new JSONObject();
                        JSONArray temp_array = new JSONArray();
                        JSONObject patient_object = new JSONObject();
                        try {
                            object.put("part_name", bodypartSelected);
                            object.put("res_id", myPartList[position_list_selected]);
                            object.put("position", position_list_selected + "");
                            object.put("str_time", str_time);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (preferences.getString("recently", "").equals("")) {

                            array1.put(object);
                            try {
                                patient_object.put("patientid", getPatientId());
                                patient_object.put("recent", array1.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            array.put(patient_object);
                            editor.putString("recently", array.toString());
                            editor.commit();
                        } else {
                            try {
                                array = new JSONArray(preferences.getString("recently", ""));
                                for (int i = 0; i < array.length(); i++) {
                                    JSONObject object1 = array.getJSONObject(i);
                                    if (object1.getString("patientid").equals(getPatientId())) {
                                        present = true;
                                        temp_index = i;
                                        JSONArray recent_array = new JSONArray(object1.getString("recent"));
                                        for (int j = 0; j < recent_array.length(); j++) {
                                            JSONObject recent_object = recent_array.getJSONObject(j);
                                            if (recent_object.getString("position").equals(object.getString("position"))) {
                                                flag = true;
                                                recent_array.remove(j);
                                                temp_array.put(object);
                                                for (int k = 0; k < recent_array.length(); k++) {
                                                    temp_array.put(recent_array.getJSONObject(k));
                                                }
                                                break;
                                            }
                                        }
                                        break;
                                    }

                                }

                                if (flag) {
                                    if (temp_index != -1) {
                                        array.getJSONObject(temp_index).put("recent", temp_array.toString());
                                    }
                                    editor.putString("recently", array.toString());
                                    editor.commit();
                                } else if (present == true && flag == false) {
                                    temp_array.put(0, object);
                                    JSONArray array2 = new JSONArray(array.getJSONObject(temp_index).getString("recent"));
                                    for (int j = 0; j < array2.length(); j++) {
                                        temp_array.put(array2.getJSONObject(j));
                                    }
                                    array.getJSONObject(temp_index).put("recent", temp_array.toString());
                                    editor.putString("recently", array.toString());
                                    editor.commit();
                                } else if (present == false) {
                                    JSONArray temp = new JSONArray();
                                    temp.put(object);
                                    JSONObject temp_obj = new JSONObject();
                                    temp_obj.put("patientid", getPatientId());
                                    temp_obj.put("recent", temp.toString());
                                    array.put(temp_obj);
                                    editor.putString("recently", array.toString());
                                    editor.commit();
                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        Intent intent = new Intent(BodyPartSelection.this, MonitorActivity.class);
                        //To be started here i need to putextras in the intents and send them to the moitor activity
                        intent.putExtra("deviceMacAddress", getIntent().getStringExtra("deviceMacAddress"));
                        intent.putExtra("patientId", getIntent().getStringExtra("patientId"));
                        intent.putExtra("patientName", getIntent().getStringExtra("patientName"));
                        intent.putExtra("exerciseType", bodypartSelected);
                        intent.putExtra("orientation", orientationSelected);
                        Log.i("intent", intent.toString());
                        startActivity(intent);
                    } else {
                        Toast.makeText(BodyPartSelection.this, "Please choose a bodypart!", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else {
            Toast.makeText(BodyPartSelection.this, "Please choose a bodypart and orientation!", Toast.LENGTH_SHORT).show();
        }
    }


    public static void refreshView(){
        reinitializeStatics();
        selectedPosition = -1;
        editor = preferences.edit();
        editor.putString("bodyPartClicked","");
        editor.commit();
    }


    /**
     * Sending data to the server.
     * @param maxAngle
     * @param minAngle
     * @param maxEmg
     * @param minEmg
     */
    private void sendData(String maxAngle, String minAngle, String maxEmg, String minEmg) {
        JSONObject object = new JSONObject();
        MqttMessage message = new MqttMessage();
        try {
            object.put("phizioemail", json_phizio.getString("phizioemail"));
            object.put("patientid",getPatientId());
            object.put("bodypart",bodypartSelected);
            object.put("orientation",orientationSelected);
            object.put("maxemg",maxEmg);
            object.put("maxangle",maxAngle);
            object.put("minemg",minEmg);
            object.put("minangle",minAngle);

            message.setPayload(object.toString().getBytes());
            mqttHelper.publishMqttTopic(mqtt_publish_message_reference,message);
            object.put("bodypartselected",bodypartSelected);
            object.put("orientationselected",orientationSelected);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        PatientOperations.saveReferenceSessionLocally(object,this);
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
        bodyPartWithMmtRecyclerView.removeResources();
        mqttHelper.mqttAndroidClient.unregisterResources();
        mqttHelper.mqttAndroidClient.close();
        super.onDestroy();
        painscale=""; muscletone=""; exercisename=""; commentsession=""; symptoms=""; musclename="";orientationSelected="";
        repsselected=0; selectedPosition=-1;
    }

    public static void reinitializeStatics() {
        painscale=""; muscletone=""; exercisename=""; commentsession=""; symptoms=""; musclename="";orientationSelected="";
        repsselected=0;
    }
}
