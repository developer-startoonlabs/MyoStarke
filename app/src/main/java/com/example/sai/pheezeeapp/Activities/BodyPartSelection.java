package com.example.sai.pheezeeapp.Activities;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.sai.pheezeeapp.Adapters.BodyPartRecyclerView;
import com.example.sai.pheezeeapp.Classes.BodyPartSelectionModel;
import com.example.sai.pheezeeapp.R;

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


    TextView tv_body_part_recent;
    String[] string;

    GridLayoutManager manager;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_body_part_selection);



        tv_body_part_recent = (TextView)findViewById(R.id.tv_recently_items);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        if (preferences.getString("recenty","").equals("")){
            tv_body_part_recent.setVisibility(View.VISIBLE);
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



    }

    public void visibilityChanged(){
//        bodyPartRecyclerViewAdapter.notifyItemChanged();
            View view = manager.findViewByPosition(Integer.parseInt(preferences.getString("bodyPartClicked","")));
            ImageView imageView = view.findViewById(R.id.bodypartImage);
        FrameLayout frameLayout = view.findViewById(R.id.framelayout_preview);
        imageView.setVisibility(View.VISIBLE);
        frameLayout.setVisibility(View.INVISIBLE);
    }

}
