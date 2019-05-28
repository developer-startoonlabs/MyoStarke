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
import android.widget.Toast;

import com.example.sai.pheezeeapp.activities.PatientsView;
import com.example.sai.pheezeeapp.R;

import java.util.ArrayList;
import java.util.List;

public class DashboardActivity extends AppCompatActivity implements ReportFragment.OnListFragmentInteractionListener,MonitorFragment.OnFragmentInteractionListener {

    Context context;
    String patientId;
    TabLayout tabLayout;
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



}
