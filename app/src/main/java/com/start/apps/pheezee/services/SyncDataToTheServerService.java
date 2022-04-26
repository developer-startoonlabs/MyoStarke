package com.start.apps.pheezee.services;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import com.start.apps.pheezee.repository.MqttSyncRepository;
import com.start.apps.pheezee.retrofit.GetDataService;
import com.start.apps.pheezee.retrofit.RetrofitClientInstance;

public class SyncDataToTheServerService extends JobService {
    private static final String CHANNEL_ID = "1";
    private static final String TAG = "JobServiceExample";
    private boolean jobCancelled = false;
    private GetDataService getDataService;
    private MqttSyncRepository repository;


    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d("SCEDULED","SCEDULED SYNC SERVICE");
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        repository = new MqttSyncRepository(getApplication());
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
                    repository.syncDataToServer();
                    repository.setOnServerResponseListner(new MqttSyncRepository.onServerResponse() {
                        @Override
                        public void onDeletePateintResponse(boolean response) {

                        }

                        @Override
                        public void onUpdatePatientDetailsResponse(boolean response) {

                        }

                        @Override
                        public void onUpdatePatientStatusResponse(boolean response) {

                        }

                        @Override
                        public void onSyncComplete(boolean response, String message) {
                            if(response)
                                jobFinished(params,true);
                            else
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
