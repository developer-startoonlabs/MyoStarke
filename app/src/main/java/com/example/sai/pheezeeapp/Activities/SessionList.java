package com.example.sai.pheezeeapp.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.patientsRecyclerView.RecyclerViewSessionListAdapter;
import com.example.sai.pheezeeapp.patientsRecyclerView.SessionListData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SessionList extends AppCompatActivity {



    RecyclerView rv_session_list;
    RecyclerViewSessionListAdapter adapter;
    ArrayList<SessionListData> arrayList;
    SharedPreferences sharedPref;
    PopupWindow report;
    View rootView;

    String patientid;

    JSONObject json_phizio;
    JSONArray jsonArray;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        LayoutInflater inflater = getLayoutInflater();
        rv_session_list = (RecyclerView)findViewById(R.id.rv_session_list);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        rv_session_list.setLayoutManager(mLayoutManager);


        patientid = this.getIntent().getStringExtra("patientid");




        arrayList = new ArrayList<>();

        //Shared preferences
        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Toast.makeText(this, ""+patientid, Toast.LENGTH_SHORT).show();

        //JSON

        try {
            json_phizio =new JSONObject(sharedPref.getString("phiziodetails", ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        try {
            if(json_phizio.has("phiziopatients")) {
                 jsonArray = new JSONArray(json_phizio.getString("phiziopatients"));
                 //Log.i("hello",jsonArray.length());
            }
                if(jsonArray.length()>0){
                    for (int i=0;i<jsonArray.length();i++){
                        Log.i("JSON ARRAY",patientid);
                        if(patientid.equals(jsonArray.getJSONObject(i).getString("patientid"))){
                            Log.i("hello","hello");
                            if (jsonArray.getJSONObject(i).has("sessions")) {
                                Log.i("hello","hello");
                                JSONArray array = new JSONArray(jsonArray.getJSONObject(i).getString("sessions"));

                                if(array.length()>0){
                                    for (int j=0;j<array.length();j++){
                                        SessionListData data = new SessionListData();
                                        data.setHeldOn(array.getJSONObject(j).getString("heldon"));
                                        Log.i("sessions",array.getJSONObject(j).getString("heldon"));
                                        arrayList.add(data);
                                    }
                                }
                            }


                        }
                    }
                }



        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new RecyclerViewSessionListAdapter(arrayList);

        rv_session_list.setAdapter(adapter);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        /*
        "heldon":"2018-11-23 18:54:09","maxangle":94,"minangle":75,"maxemg":114,"holdtime":"0 : 0","sessiontime":"00m: 05s"*/

    }


    public void showsessiondetails(View view){

        TextView tv_session_heldon = view.findViewById(R.id.tv_session_heldon);
        Toast.makeText(this, ""+tv_session_heldon.getText(), Toast.LENGTH_SHORT).show();
        try {
            if(json_phizio.has("phiziopatients")) {
                jsonArray = new JSONArray(json_phizio.getString("phiziopatients"));
                //Log.i("hello",jsonArray.length());
            }
            if(jsonArray.length()>0){
                for (int i=0;i<jsonArray.length();i++){
                    if(patientid.equals(jsonArray.getJSONObject(i).getString("patientid"))){
                        if (jsonArray.getJSONObject(i).has("sessions")) {
                            JSONArray array = new JSONArray(jsonArray.getJSONObject(i).getString("sessions"));

                            if(array.length()>0){
                                for (int j=0;j<array.length();j++){
                                    if(array.getJSONObject(j).getString("heldon").equals(tv_session_heldon.getText().toString())){
                                        String heldon = array.getJSONObject(j).getString("heldon");
                                        String maxangle = array.getJSONObject(j).getString("maxangle");
                                        String minangle = array.getJSONObject(j).getString("minangle");
                                        String maxemg = array.getJSONObject(j).getString("maxemg");
                                        String holdtime = array.getJSONObject(j).getString("holdtime");
                                        String sessiontime = array.getJSONObject(j).getString("sessiontime");
                                        String patientid = jsonArray.getJSONObject(i).getString("patientid");
                                        String patientname = jsonArray.getJSONObject(i).getString("patientname");
                                        initiatePopupWindow(patientid,patientname,heldon,maxangle,minangle,maxemg,holdtime,sessiontime);
                                    }
                                }
                            }
                        }


                    }
                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initiatePopupWindow(String patientid,String patientname,String heldon,String maxangle,String minangle,String maxemg,String holdtime,String sessiontime) {
       // Toast.makeText(this, "lololol", Toast.LENGTH_SHORT).show();

        View layout = getLayoutInflater().inflate(R.layout.session_analysis, null);

        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.setContentView(layout);
        report.setFocusable(true);
        report.showAtLocation(layout, Gravity.CENTER, 0, 0);
        //report.showAtLocation(View.inflate(R.layout.session_list), Gravity.CENTER, 0, 0);
        LinearLayout cancelbtn = layout.findViewById(R.id.cancel_action);
        LinearLayout shareIcon = layout.findViewById(R.id.shareIcon);
        final ConstraintLayout summaryView = layout.findViewById(R.id.summaryView);
        TextView popUpPatientId = layout.findViewById(R.id.patientId);
        TextView popUpPatientName = layout.findViewById(R.id.patientName);
        TextView maxAngleView = layout.findViewById(R.id.maxAngle);
        TextView repetitions = layout.findViewById(R.id.totalReps);
        TextView minAngleView  = layout.findViewById(R.id.minAngle);
        TextView maxEmgView  = layout.findViewById(R.id.maxEmg);
        TextView holdTimeView  = layout.findViewById(R.id.holdtime);
        TextView heldTime = layout.findViewById(R.id.heldtime);
        TextView sessionTime = layout.findViewById(R.id.sessionTime);
        //repetitions.setText();
        popUpPatientId.setText(patientid);
        popUpPatientName.setText(patientname);
        maxAngleView.setText(maxangle);
        minAngleView.setText(minangle);
        maxEmgView.setText(maxemg+"μA");
        holdTimeView.setText(holdtime);
        heldTime.setText(heldon);
        sessionTime.setText(sessiontime);



        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                report.dismiss();
            }
        });
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(SessionList.this,"shot",Toast.LENGTH_SHORT).show();
                //OnClickShare(summaryView);
            }
        });
    }

}
