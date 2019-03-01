package com.example.sai.pheezeeapp.Activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.opengl.Visibility;
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

import com.example.sai.pheezeeapp.Adapters.BodyPartRecyclerView;
import com.example.sai.pheezeeapp.Classes.BodyPartSelectionModel;
import com.example.sai.pheezeeapp.Classes.ItemOffsetDecoration;
import com.example.sai.pheezeeapp.R;
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
    ArrayList<BodyPartSelectionModel> bodyPartSelectionList;
    LinearLayout ll_recent_bodypart;

    //Floating action button for done
    FloatingLayout fab_done ;

    TextView tv_body_part_recent;
    ImageView iv_back_body_part_selection;
    String[] string;
    String str_recent;

    FrameLayout fl_fab_background;

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
        tv_body_part_recent = (TextView)findViewById(R.id.tv_recently_items);
        fab_done =  findViewById(R.id.fab_done);
        fl_fab_background = findViewById(R.id.fl_fab_background);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = preferences.edit();
        str_recent = preferences.getString("recently","");
        ll_recent_bodypart = findViewById(R.id.ll_recent_section);
        iv_back_body_part_selection = findViewById(R.id.iv_back_body_part_selection);

        //int_ll
        height_fl = fl_fab_background.getHeight();


        if (str_recent.equals("")){
            tv_body_part_recent.setVisibility(View.VISIBLE);
        }
        else {
            try {
                JSONArray array = new JSONArray(str_recent);
                ImageView iv_recent_body[] = new ImageView[array.length()];
                for (int i=0;i<array.length();i++){
                    int width = dpToPixel(95);
                    iv_recent_body[i] =new ImageView(getApplicationContext());
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                            width, LinearLayout.LayoutParams.WRAP_CONTENT);
                    JSONObject object = array.getJSONObject(i);
                    int res_id = object.getInt("res_id");
                    iv_recent_body[i].setImageResource(res_id);
                    int left_padding = dpToPixel(20);
                    iv_recent_body[i].setPadding(left_padding,0,0,0);
                    Log.i("res_id",res_id+"");
                    iv_recent_body[i].setTag(i);
                    iv_recent_body[i].setScaleType(ImageView.ScaleType.FIT_XY);
                    iv_recent_body[i].setOnClickListener(onclicklistner);
                    ll_recent_bodypart.addView(iv_recent_body[i],layoutParams);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        bodyPartRecyclerView = (RecyclerView)findViewById(R.id.bodyPartRecyclerView);
        bodyPartRecyclerView.addItemDecoration(new DividerItemDecoration(this,0));
        bodyPartRecyclerView.setHasFixedSize(true);
        manager = new GridLayoutManager(this,2);
        bodyPartRecyclerView.setLayoutManager(manager);
        bodyPartSelectionList = new ArrayList<>();

        string = getResources().getStringArray(R.array.bodyPartName);
        for (int i=0;i<string.length;i++){
            BodyPartSelectionModel bodyPartSelectionModel = new BodyPartSelectionModel(myPartList[i],myPreviewList[i],string[i]);
            bodyPartSelectionList.add(bodyPartSelectionModel);
        }

        bodyPartRecyclerViewAdapter = new BodyPartRecyclerView(bodyPartSelectionList,this);


        bodyPartRecyclerView.setAdapter(bodyPartRecyclerViewAdapter);



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
//        bodyPartRecyclerViewAdapter.notifyItemChanged();
            View view = manager.findViewByPosition(Integer.parseInt(preferences.getString("bodyPartClicked","")));
            ImageView imageView = view.findViewById(R.id.bodypartImage);
            RelativeLayout rl_preview = view.findViewById(R.id.rl_preview);
            Spinner sp_set_goal = view.findViewById(R.id.sp_set_goal);
            sp_set_goal.setSelection(0);
            sp_set_goal.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            rl_preview.setVisibility(View.INVISIBLE);

    }

    public void startMonitorSession(View view){
        boolean flag = false;
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
        try {
            object.put("part_name",tv_body_part_name.getText().toString());
            object.put("res_id",myPartList[position_list_selected]);
            object.put("position",position_list_selected+"");
            object.put("str_time",str_time);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(preferences.getString("recently","").equals("")){
            array.put(object);
            editor.putString("recently",array.toString());
            editor.commit();
        }
        else {
            try {
                array = new JSONArray(preferences.getString("recently",""));
                for (int i=0;i<array.length();i++){
                    JSONObject object1 = array.getJSONObject(i);
                    if(object1.getString("position").equals(object.getString("position"))){
                        flag = true;
                        array.remove(i);
                        array1.put(object);
                        for (int j=0;j<array.length();j++){
                            array1.put(array.getJSONObject(j));
                        }
                        break;
                    }
                }

                if (flag){
                    editor.putString("recently",array1.toString());
                    editor.commit();
                }
                else {
                    array1.put(0,object);
                    for (int j=0;j<array.length();j++){
                        array1.put(array.getJSONObject(j));
                    }
                    editor.putString("recently",array1.toString());
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
