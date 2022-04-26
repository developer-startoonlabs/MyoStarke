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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.start.apps.pheezee.pojos.DeviceDeactivationStatus;
import com.start.apps.pheezee.pojos.DeviceDeactivationStatusResponse;
import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;
import com.start.apps.pheezee.room.Entity.DeviceStatus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceDeactivationStatusService extends JobService {
    private static final String TAG = "HealthStatusService";
    private boolean jobCancelled = false;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private GetDataService getDataService;
    private MqttSyncRepository repository;
    @Override
    public boolean onStartJob(JobParameters params) {
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        repository = new MqttSyncRepository(this.getApplication());
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
                    Gson gson = new GsonBuilder().create();
                    String deactivation_data = preferences.getString("uid_deactivation","");
                    if(!deactivation_data.equalsIgnoreCase("")){
                        DeviceDeactivationStatus data = gson.fromJson(deactivation_data, DeviceDeactivationStatus.class);
                        Call<DeviceDeactivationStatusResponse> call = getDataService.getDeviceStatus(data);
                        call.enqueue(new Callback<DeviceDeactivationStatusResponse>() {
                            @Override
                            public void onResponse(@NonNull Call<DeviceDeactivationStatusResponse> call, @NonNull Response<DeviceDeactivationStatusResponse> response) {
                                if(response.code()==200){
                                    DeviceDeactivationStatusResponse res_deactivate = response.body();
                                    if(res_deactivate.isStatus()){
                                        editor = preferences.edit();
                                        editor.putString("uid_deactivation","");
                                        editor.apply();
                                        jobFinished(params,false);
                                    }else {
                                        if(repository!=null){
                                            DeviceStatus status = new DeviceStatus(res_deactivate.getUid(),1);
                                            repository.insertDeviceStatus(status);
//                                            Log.i("UID",res_deactivate.getUid());
                                        }
                                        jobFinished(params,true);
                                    }
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<DeviceDeactivationStatusResponse> call, @NonNull Throwable t) {
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
