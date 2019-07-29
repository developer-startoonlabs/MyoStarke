package com.startoonlabs.apps.pheezee.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;

import java.util.List;

/**
 * That interacts with database
 */
public class MqttSyncRepository {
    private MqttSyncDao mqttSyncDao;
    /**
     * Live object returned to get the item count in the database to update the sync button view
     */
    private LiveData<Long> count;

    public MqttSyncRepository(Application application){
        PheezeeDatabase database =PheezeeDatabase.getInstance(application);
        mqttSyncDao = database.mqttSyncDao();
        this.count = mqttSyncDao.getEntityCount();
    }

    public LiveData<Long> getCount(){
        return count;
    }

    public long insert(final MqttSync mqttSync){
//        new InsertMqttSyncAsyncTask(mqttSyncDao).execute(mqttSync);
//        return mqttSyncDao.insert(mqttSync);
        return mqttSyncDao.insert(mqttSync);
    }

    /**
     * Called when pressed logout
     */
    public void deleteAllSync(){
        new DeleteAllMqttSync(mqttSyncDao).execute();
    }

    /**
     * deletes a entry based on id
     * @param id
     */
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
