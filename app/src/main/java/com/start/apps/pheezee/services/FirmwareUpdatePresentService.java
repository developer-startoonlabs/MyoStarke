package com.start.apps.pheezee.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import androidx.annotation.NonNull;

import com.start.apps.pheezee.pojos.FirmwareUpdateCheck;
import com.start.apps.pheezee.pojos.FirmwareUpdateCheckResponse;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirmwareUpdatePresentService extends JobService {

    private static final String TAG = "JobServiceExample";
    private boolean jobCancelled = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GetDataService getDataService;

    @Override
    public boolean onStartJob(JobParameters params) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        doInBackGround(params);
        return true;
    }

    private void doInBackGround(final JobParameters params) {

        if(jobCancelled){
            return;
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE );
                NetworkInfo activeNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                NetworkInfo activeWifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isConnected = activeNetInfo != null && activeNetInfo.isConnectedOrConnecting();
                boolean isWifiConnected = activeWifiInfo != null && activeWifiInfo.isConnectedOrConnecting();
                if (isConnected || isWifiConnected ){
                    FirmwareUpdateCheck check = new FirmwareUpdateCheck("");
                    Call<FirmwareUpdateCheckResponse> data = getDataService.checkFirmwareUpdateAndGetLink(check);
                    data.enqueue(new Callback<FirmwareUpdateCheckResponse>() {
                        @Override
                        public void onResponse(@NonNull Call<FirmwareUpdateCheckResponse> call, @NonNull Response<FirmwareUpdateCheckResponse> response) {
                            if(response.code()==200){
                                FirmwareUpdateCheckResponse check1 = response.body();
                                if(check1!=null) {
                                    if (check1.isFirmware_available()) {
                                        editor = preferences.edit();
                                        editor.putString("firmware_update", check1.getLatest_firmware_link());
                                        editor.putString("firmware_version", check1.getFirmware_version());
                                        editor.apply();
                                        jobFinished(params,false);
                                    }else {
                                        editor = preferences.edit();
                                        editor.putString("firmware_update", "");
                                        editor.putString("firmware_version", "");
                                        editor.apply();
                                        jobFinished(params,false);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<FirmwareUpdateCheckResponse> call, @NonNull Throwable t) {
                            jobFinished(params,false);
                        }
                    });
                }

            }
        });
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        jobCancelled = true;
        return true;
    }
}
