package com.example.sai.pheezeeapp.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Classes.BodyPartWithMmtSelectionModel;
import com.example.sai.pheezeeapp.adapters.BodyPartRecyclerView;
import com.example.sai.pheezeeapp.Classes.BodyPartSelectionModel;
import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.adapters.BodyPartWithMmtRecyclerView;
import com.robertlevonyan.views.customfloatingactionbutton.FloatingLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class BodyPartSelection extends AppCompatActivity {

    //Drawable arry for the body part selection

    int[] myPartList = new int[]{R.drawable.elbow_part, R.drawable.knee_part,R.drawable.ankle_part,R.drawable.hip_part,R.drawable.wrist_part,R.drawable.shoulder_part};
    int[] myPreviewList = new int[]{R.drawable.elbow_part, R.drawable.knee_part,R.drawable.ankle_part,R.drawable.hip_part,R.drawable.wrist_part,R.drawable.shoulder_part};


    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    RecyclerView bodyPartRecyclerView;
    BodyPartRecyclerView bodyPartRecyclerViewAdapter;

    BodyPartWithMmtRecyclerView bodyPartWithMmtRecyclerView;
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;

    ArrayList<BodyPartWithMmtSelectionModel> bodyPartWithMmtSelectionModels;
    LinearLayout ll_recent_bodypart;

    //Floating action button for done
    FloatingLayout fab_done ;

    TextView tv_body_part_recent;
    ImageView iv_back_body_part_selection;
    String[] string;
    String str_recent;

    FrameLayout fl_fab_background;
    boolean flag_recent = false;

    int height_fl;

    GridLayoutManager manager;

    android.support.v7.widget.Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);
//        toolbar = findViewById(R.id.my_toolbar_bodypart);
//         toolbar.setElevation(5);
//        toolbar.setTitle("");
//        setSupportActionBar(toolbar);
        tv_body_part_recent = findViewById(R.id.tv_recently_items);
        fab_done =  findViewById(R.id.fab_done);
        fl_fab_background = findViewById(R.id.fl_fab_background);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        str_recent = preferences.getString("recently","");
        ll_recent_bodypart = findViewById(R.id.ll_recent_section);
        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);

        //int_ll
        height_fl = fl_fab_background.getHeight();

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
                            iv_recent_body[j].setImageResource(res_id);
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
        bodyPartRecyclerView.addItemDecoration(new DividerItemDecoration(this,0));
        bodyPartRecyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(this,2);
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();
        bodyPartWithMmtSelectionModels = new ArrayList<>();

        string = getResources().getStringArray(R.array.bodyPartName);
//        for (int i=0;i<string.length;i++){
//            BodyPartSelectionModel bodyPartSelectionModel = new BodyPartSelectionModel(myPartList[i],myPreviewList[i],string[i]);
//            bodyPartSelectionList.add(bodyPartSelectionModel);
//        }

        for (int i=0;i<string.length;i++){
            BodyPartWithMmtSelectionModel bp = new BodyPartWithMmtSelectionModel(myPartList[i],string[i]);
            bodyPartWithMmtSelectionModels.add(bp);
        }
        bodyPartRecyclerViewAdapter = new BodyPartRecyclerView(bodyPartSelectionList,this);
        bodyPartWithMmtRecyclerView = new BodyPartWithMmtRecyclerView(bodyPartWithMmtSelectionModels,this);

//        bodyPartRecyclerView.setAdapter(bodyPartRecyclerViewAdapter);

        bodyPartRecyclerView.setAdapter(bodyPartWithMmtRecyclerView);



        bodyPartRecyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE)
//                    fab_done.setVisibility(View.VISIBLE);
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                Animation animation_down = AnimationUtils.loadAnimation(BodyPartSelection.this, R.anim.slide_out_down);
                Animation animation_up = AnimationUtils.loadAnimation(BodyPartSelection.this, R.anim.slide_up_dialog);
                super.onScrolled(recyclerView, dx, dy);

                if(!recyclerView.canScrollVertically(1)){
                    fl_fab_background.setVisibility(View.VISIBLE);
//                    fl_fab_background.startAnimation(animation_up);
                }
                else if(dy>0 && fab_done.isShown()){
                    fl_fab_background.setVisibility(View.INVISIBLE);
                    fl_fab_background.startAnimation(animation_down);
                }
                else if(dy<0){
                    fl_fab_background.setVisibility(View.VISIBLE);
                    fl_fab_background.startAnimation(animation_up);
                }
            }
        });



        iv_back_body_part_selection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    View.OnClickListener onclicklistner = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ImageView imageView = ((ImageView)v);
            int pos = Integer.parseInt(imageView.getTag().toString());
            try {
                JSONArray array = new JSONArray(str_recent);
                JSONObject object = array.getJSONObject(pos);
                Toast.makeText(BodyPartSelection.this,object.toString(),Toast.LENGTH_LONG).show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };

    public void visibilityChanged(){
            View view = manager.findViewByPosition(Integer.parseInt(preferences.getString("bodyPartClicked","")));
            ImageView imageView = view.findViewById(R.id.bodypartImage);
            RelativeLayout rl_left_right = view.findViewById(R.id.rl_left_right);
            RelativeLayout rl_left = view.findViewById(R.id.rl_left);
            RelativeLayout rl_right = view.findViewById(R.id.rl_right);
            RelativeLayout rl_mmt_and_session = view.findViewById(R.id.rl_mmt_and_session);
            RelativeLayout rl_left_section = view.findViewById(R.id.rl_left_section);
            RelativeLayout rl_right_section = view.findViewById(R.id.rl_right_section);
            RelativeLayout rl_mmt_session = view.findViewById(R.id.rl_mmt_section);
            LinearLayout ll_tv_section = view.findViewById(R.id.ll_tv_section);
            Spinner spinner = view.findViewById(R.id.sp_set_goal);



            if(rl_left_section.getVisibility()==View.VISIBLE)
                rl_left_section.setVisibility(View.INVISIBLE);

            if(rl_right_section.getVisibility()==View.VISIBLE)
                rl_right_section.setVisibility(View.INVISIBLE);

            if(rl_left_right.getVisibility()==View.VISIBLE)
                rl_left_right.setVisibility(View.INVISIBLE);
            if(rl_left.getVisibility()==View.VISIBLE)
                rl_left.setVisibility(View.INVISIBLE);
            if(rl_right.getVisibility()==View.VISIBLE)
                rl_right.setVisibility(View.INVISIBLE);
            if(rl_mmt_and_session.getVisibility()==View.VISIBLE)
                rl_mmt_and_session.setVisibility(View.INVISIBLE);
            if(rl_mmt_session.getVisibility()==View.VISIBLE)
                rl_mmt_session.setVisibility(View.INVISIBLE);
            if(ll_tv_section.getVisibility()==View.VISIBLE)
                ll_tv_section.setVisibility(View.GONE);
            if (spinner.getVisibility()==View.VISIBLE){
                spinner.setSelection(0);
                spinner.setVisibility(View.GONE);
            }
            if(imageView.getVisibility()==View.INVISIBLE)
                imageView.setVisibility(View.VISIBLE);


            imageView.setEnabled(true);
    }



    public String getPatientId(){
        String patientID = null;

        if(!getIntent().getStringExtra("patientId").equals("")) {
            patientID = getIntent().getStringExtra("patientId");
            Log.i("test",getIntent().getStringExtra("patientId"));
        }

        return patientID;
    }

    public void startMonitorSession(View view){
        int temp_index = -1;
        boolean flag = false, present = false;
        int position_list_selected = Integer.parseInt(preferences.getString("bodyPartClicked",""));
        View list_item = manager.findViewByPosition(position_list_selected);
        TextView tv_middle  = list_item.findViewById(R.id.tv_selected_goal_text);
        String str_time = tv_middle.getText().toString();
        str_time = str_time.replaceAll("[a-zA-Z]","").trim();
        TextView tv_body_part_name = list_item.findViewById(R.id.tv_body_part_name);
        Toast.makeText(this, ""+str_time+tv_body_part_name.getText(), Toast.LENGTH_SHORT).show();
        editor = preferences.edit();

        JSONArray array = new JSONArray();
        JSONArray array1 = new JSONArray();
        JSONObject object = new JSONObject();
        JSONArray temp_array  = new JSONArray();
        JSONObject patient_object = new JSONObject();
        try {
            object.put("part_name",tv_body_part_name.getText().toString());
            object.put("res_id",myPartList[position_list_selected]);
            object.put("position",position_list_selected+"");
            object.put("str_time",str_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(preferences.getString("recently","").equals("")){

            array1.put(object);
            try {
                patient_object.put("patientid",getPatientId());
                patient_object.put("recent",array1.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            array.put(patient_object);
            editor.putString("recently",array.toString());
            editor.commit();
        }
        else {
            try {
                array = new JSONArray(preferences.getString("recently",""));
                for (int i=0;i<array.length();i++){
                    JSONObject object1 = array.getJSONObject(i);
                    if(object1.getString("patientid").equals(getPatientId())){
                        present = true;
                        temp_index = i;
                        JSONArray recent_array = new JSONArray(object1.getString("recent"));
                        for (int j=0;j<recent_array.length();j++){
                            JSONObject recent_object = recent_array.getJSONObject(j);
                            if(recent_object.getString("position").equals(object.getString("position"))){
                                flag = true;
                                recent_array.remove(j);
                                temp_array.put(object);
                                for (int k=0;k<recent_array.length();k++){
                                    temp_array.put(recent_array.getJSONObject(k));
                                }
                                break;
                            }
                        }
                        break;
                    }

                }

                if (flag){
                    if(temp_index!=-1){
                        array.getJSONObject(temp_index).put("recent",temp_array.toString());
                    }
                    editor.putString("recently",array.toString());
                    editor.commit();
                }
                else if(present==true && flag==false) {
                    temp_array.put(0,object);
                    JSONArray array2 = new JSONArray(array.getJSONObject(temp_index).getString("recent"));
                    for (int j=0;j<array2.length();j++){
                        temp_array.put(array2.getJSONObject(j));
                    }
                    array.getJSONObject(temp_index).put("recent",temp_array.toString());
                    editor.putString("recently",array.toString());
                    editor.commit();
                }
                else if(present==false){
                    JSONArray temp = new JSONArray();
                    temp.put(object);
                    JSONObject temp_obj = new JSONObject();
                    temp_obj.put("patientid",getPatientId());
                    temp_obj.put("recent",temp.toString());
                    array.put(temp_obj);
                    editor.putString("recently",array.toString());
                    editor.commit();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent intent = new Intent(this, MonitorActivity.class);
        //To be started here i need to putextras in the intents and send them to the moitor activity
        intent.putExtra("deviceMacAddress",getIntent().getStringExtra("deviceMacAddress"));
        intent.putExtra("patientId",getIntent().getStringExtra("patientId"));
        intent.putExtra("patientName",getIntent().getStringExtra("patientName"));
        intent.putExtra("exerciseType",tv_body_part_name.getText().toString());
        Log.i("intent",intent.toString());
        startActivity(intent);
    }

    public void setFabVisible(){
        fab_done.setVisibility(View.VISIBLE);
    }


    public int dpToPixel(int dp){
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);

        return pixels;
    }

}
