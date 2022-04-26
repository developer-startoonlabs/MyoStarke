package com.start.apps.pheezee.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;
import start.apps.pheezee.R;
import start.apps.pheezee.databinding.ActivityOnboardingBinding;
import com.start.apps.pheezee.adapters.OnBoardingAdapter;

public class OnBoardingActivity extends AppCompatActivity {


    ActivityOnboardingBinding onBoardingBinding;
    private Integer pagePosition;

    private ViewPager2.OnPageChangeCallback callback = new ViewPager2.OnPageChangeCallback() {
        @Override
        public void onPageSelected(int position) {
            super.onPageSelected(position);
            updateCircleMarker(position);
            pagePosition = position;
            if(position<3){
                onBoardingBinding.buttonFinish.setVisibility(View.INVISIBLE);
                onBoardingBinding.buttonNext.setVisibility(View.VISIBLE);
            }else{
                onBoardingBinding.buttonFinish.setVisibility(View.VISIBLE);
                onBoardingBinding.buttonNext.setVisibility(View.INVISIBLE);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onBoardingBinding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(onBoardingBinding.getRoot());
        makeStatusBarTransparent();
        OnBoardingAdapter onBoardingAdapter = new OnBoardingAdapter(this, 4);
        onBoardingBinding.viewPagerOnboarding.setAdapter(onBoardingAdapter);
        onBoardingBinding.viewPagerOnboarding.registerOnPageChangeCallback(callback);

        onBoardingBinding.buttonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("Page","Clicked");
                onBoardingBinding.viewPagerOnboarding.setCurrentItem(pagePosition+1);
            }
        });
        onBoardingBinding.buttonFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences=getSharedPreferences("OnBoarding",MODE_PRIVATE);
                SharedPreferences.Editor editor=sharedPreferences.edit();
                editor.putBoolean("flag",true);
                editor.commit();
                editor.apply();
                startActivity(new Intent(OnBoardingActivity.this,LoginActivity.class));
            }
        });
    }



    private void makeStatusBarTransparent(){
        onBoardingBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onBoardingBinding.viewPagerOnboarding.unregisterOnPageChangeCallback(callback);
    }

    private void updateCircleMarker(Integer position){
        switch (position){
            case 0:
                onBoardingBinding.onBoardingCircleOne.setBackground(getDrawable(R.drawable.bg_circle_blue));
                onBoardingBinding.onBoardingCircleTwo.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleThree.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleFour.setBackground(getDrawable(R.drawable.bg_circle_grey));
                return;
            case 1:
                onBoardingBinding.onBoardingCircleOne.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleTwo.setBackground(getDrawable(R.drawable.bg_circle_blue));
                onBoardingBinding.onBoardingCircleThree.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleFour.setBackground(getDrawable(R.drawable.bg_circle_grey));
                return;
            case 2:
                onBoardingBinding.onBoardingCircleOne.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleTwo.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleThree.setBackground(getDrawable(R.drawable.bg_circle_blue));
                onBoardingBinding.onBoardingCircleFour.setBackground(getDrawable(R.drawable.bg_circle_grey));
                return;
            case 3:
                onBoardingBinding.onBoardingCircleOne.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleTwo.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleThree.setBackground(getDrawable(R.drawable.bg_circle_grey));
                onBoardingBinding.onBoardingCircleFour.setBackground(getDrawable(R.drawable.bg_circle_blue));
                return;
        }


    }

}