package com.example.sai.pheezeeapp.dashboard;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.sai.pheezeeapp.Activities.PatientsView;
import com.example.sai.pheezeeapp.R;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements ReportFragment.OnListFragmentInteractionListener,MonitorFragment.OnFragmentInteractionListener {

    Context context;
    String patientId;
    TabLayout tabLayout;
    MqttAndroidClient client;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPref.edit();
        patientId = getIntent().getStringExtra("patientId");
        try {
            JSONArray data = new JSONArray(sharedPref.getString("patientsData",""));
            for(int i = 0; i<data.length();i++){
                if(data.getJSONObject(i).getString("patientId").equals(patientId)){
                    JSONObject tempObj = new JSONObject();
                    tempObj.put("patientId",patientId);
                    tempObj.put("patientName",data.getJSONObject(i).getString("patientName"));
                    data.remove(i);
                    data.put(tempObj);
                    editor.putString("patientsData",data.toString());
                    editor.apply();
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        context = getApplicationContext();


        ViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setNestedScrollingEnabled(true);
        setupViewPager(viewPager);


        tabLayout = findViewById(R.id.tabs);

        new TabLayout.TabLayoutOnPageChangeListener(tabLayout){
            @Override
            public void onPageScrollStateChanged(int state){

                Toast.makeText(DashboardActivity.this,"tab changed",Toast.LENGTH_LONG).show();
                System.out.println("tabtabtabtabtabtabtabtabtab");
            }
        };
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                if (tab.getText().equals("Report"))
                    StartMqtt();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,PatientsView.class));
        super.onBackPressed();  // optional depending on your needs
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new MonitorFragment(), "Monitoring");
        adapter.addFragment(new ReportFragment(), "Report");
        viewPager.setAdapter(adapter);
        viewPager.arrowScroll(1);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    public void StartMqtt(){
        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://18.236.141.171:1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                static final String TAG = "Mqtt Message";

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    //syncDataToServer();
                    try {
                        client.publish("newapk/getreport", patientId.getBytes(), 0, false);
                        client.close();
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                    client.setCallback(new MqttCallback() {
                        public void messageArrived(String topic, MqttMessage message) {}
                        public void connectionLost(Throwable cause) {}
                        public void deliveryComplete(IMqttDeliveryToken token) {}
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

}
