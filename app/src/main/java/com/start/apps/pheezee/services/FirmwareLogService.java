package com.start.apps.pheezee.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import com.start.apps.pheezee.pojos.FirmwareData;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirmwareLogService extends JobService {
    private static final String CHANNEL_ID = "1";
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
                    if(!preferences.getString("firmware_log","").equalsIgnoreCase("")) {
                        FirmwareData data = new FirmwareData(preferences.getString("firmware_log", ""));
                        Call<Boolean> comment_data = getDataService.sendFirmwareLog(data);
                        comment_data.enqueue(new Callback<Boolean>() {
                            @Override
                            public void onResponse(Call<Boolean> call, Response<Boolean> response) {
                                if(response.code()==200){
                                    Boolean res = response.body();
                                    if(res!=null && res){
                                        editor = preferences.edit();
                                        editor.putString("firmware_log","");
                                        editor.apply();
                                        jobFinished(params,false);
                                    }else {
                                        jobFinished(params,true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Boolean> call, Throwable t) {
                                jobFinished(params,true);
                            }
                        });
                    }
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
