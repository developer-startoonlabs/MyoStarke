package com.startoonlabs.apps.pheezee.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.startoonlabs.apps.pheezee.R;

public class OnStartActivity extends AppCompatActivity {

    boolean isLoggedIn=false;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_start);
        final AccessToken accessToken = AccessToken.getCurrentAccessToken();

        final GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        //isLoggedIn = accessToken != null && !accessToken.isExpired();
        sharedPreferences =PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        isLoggedIn = sharedPreferences.getBoolean("isLoggedIn",false);
        Log.i("isLoggedIn",""+isLoggedIn);
        /**
         * checks if already logged in or not and calls the particular value bassed on that
         */
        Thread onstartThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    if(isLoggedIn)
                        startActivity(new Intent(OnStartActivity.this,PatientsView.class));
                    else if(account!=null)
                        startActivity(new Intent(OnStartActivity.this,PatientsView.class));
                    else
                        startActivity(new Intent(OnStartActivity.this,LoginActivity.class));
                    finish();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        onstartThread.start();
    }
}
