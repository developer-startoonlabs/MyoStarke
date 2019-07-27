package com.startoonlabs.apps.pheezee.repository;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;


public class MqttSyncRepository {
    private MqttSyncDao mqttSyncDao;
    private LiveData<List<MqttSync>> count;

    public MqttSyncRepository(Application application){
        PheezeeDatabase database =PheezeeDatabase.getInstance(application);
        mqttSyncDao = database.mqttSyncDao();
        this.count = mqttSyncDao.getEntityCount();
    }

    public LiveData<List<MqttSync>> getCount(){
        return count;
    }

    public long insert(final MqttSync mqttSync){
//        new InsertMqttSyncAsyncTask(mqttSyncDao).execute(mqttSync);
//        return mqttSyncDao.insert(mqttSync);
        return mqttSyncDao.insert(mqttSync);
    }

    public void deleteAllSync(){
        new DeleteAllMqttSync(mqttSyncDao).execute();
    }

    public void deleteParticular(int id){
        new DeleteMqttSyncAsyncTask(mqttSyncDao).execute(id);
    }

    public List<MqttSync> getAllSyncMessages(){
        return mqttSyncDao.getAllMqttSyncItems();
    }


    private static class InsertMqttSyncAsyncTask extends AsyncTask<MqttSync, Void, Long> {

        private MqttSyncDao mqttSyncDao;

        public InsertMqttSyncAsyncTask(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }
        @Override
        protected Long doInBackground(MqttSync... notes) {
            mqttSyncDao.insert(notes[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Long aLong) {
            super.onPostExecute(aLong);

        }
    }

    private static class DeleteMqttSyncAsyncTask extends AsyncTask<Integer, Void, Void> {

        private MqttSyncDao mqttSyncDao;

        public DeleteMqttSyncAsyncTask(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }

        @Override
        protected Void doInBackground(Integer... integers) {

            mqttSyncDao.deleteParticular(integers[0]);
            Log.i("deleted",String.valueOf(integers[0]));
            return null;
        }
    }


    private static class GetAllSyncList extends AsyncTask<Void,Void,Void>{
        private MqttSyncDao mqttSyncDao;

        public GetAllSyncList(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }


        @Override
        protected Void doInBackground(Void... voids) {
            List<MqttSync> list = mqttSyncDao.getAllMqttSyncItems();
            return null;
        }
    }

    private static class DeleteAllMqttSync extends AsyncTask<Void,Void,Void>{
        private MqttSyncDao mqttSyncDao;
        public DeleteAllMqttSync(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            mqttSyncDao.deleteAllMqttSync();
            return null;
        }
    }
}
