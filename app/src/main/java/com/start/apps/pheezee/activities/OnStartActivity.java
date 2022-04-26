package com.start.apps.pheezee.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.Animatable2Compat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import start.apps.pheezee.R;
import com.start.apps.pheezee.classes.PatientActivitySingleton;
import com.start.apps.pheezee.repository.MqttSyncRepository;

import android.widget.ImageView;

public class OnStartActivity extends AppCompatActivity {

    boolean isLoggedIn=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    MqttSyncRepository repository;
    ImageView pheezee_gif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_start);


        repository = new MqttSyncRepository(getApplication());
        //isLoggedIn = accessToken != null && !accessToken.isExpired();
        sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);

        SharedPreferences sharedPreferences_ob=getSharedPreferences("OnBoarding",MODE_PRIVATE);
        SharedPreferences.Editor sharedPreferences_ob_editor=sharedPreferences_ob.edit();

        //Clearing the Activity param
        if(PatientActivitySingleton.getInstance() != null)
        {
            PatientActivitySingleton.getInstance().setPatientDetails(null,null,null,null);
        }

        if(!sharedPreferences.getBoolean("version_2.14.5",false)){
//            editor = sharedPreferences.edit();
//            editor.clear();
//            editor.commit();
//            repository.clearDatabase();
//            editor.putBoolean("version_2.14.5",true);
//            editor.apply();
        }
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);

        pheezee_gif = findViewById(R.id.pheezee_startup_logo);


        Glide.with(this).asGif().load(R.drawable.pheezee_gif_logo).listener(new RequestListener<GifDrawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<GifDrawable> target, boolean isFirstResource) {
                return false;
            }

            @Override
            public boolean onResourceReady(GifDrawable resource, Object model, Target<GifDrawable> target, DataSource dataSource, boolean isFirstResource) {
                resource.setLoopCount(1);
                resource.registerAnimationCallback(new Animatable2Compat.AnimationCallback() {
                    @Override
                    public void onAnimationEnd(Drawable drawable) {
                        //do whatever after specified number of loops complete
                        /**
                         * checks if already logged in or not and calls the particular value bassed on that
                         */
                        Thread onstartThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(0);
                                    if(isLoggedIn) {
                                        startActivity(new Intent(OnStartActivity.this, PatientsView.class));
                                    }
                                    else {
                                        if(!sharedPreferences_ob.getBoolean("flag",false)){
                                            startActivity(new Intent(OnStartActivity.this, OnBoardingActivity.class));
                                        }
                                        else {
                                            startActivity(new Intent(OnStartActivity.this, LoginActivity.class));
                                        }
                                    }
                                    finish();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                        onstartThread.start();
                    }
                });
                return false;
            }
        }).into(pheezee_gif);

    }
}
