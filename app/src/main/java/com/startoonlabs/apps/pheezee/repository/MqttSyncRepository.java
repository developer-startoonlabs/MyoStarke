package com.startoonlabs.apps.pheezee.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.startoonlabs.apps.pheezee.patientsRecyclerView.PatientsListData;
import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Dao.PhizioPatientsDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;

import java.security.PrivateKey;
import java.util.List;

/**
 * That interacts with database
 */
public class MqttSyncRepository {
    private MqttSyncDao mqttSyncDao;
    private PhizioPatientsDao phizioPatientsDao;
    /**
     * Live object returned to get the item count in the database to update the sync button view
     */
    private LiveData<Long> count;
    private LiveData<List<PhizioPatients>> patients;
    private PheezeeDatabase database;

    public MqttSyncRepository(Application application){
        database =PheezeeDatabase.getInstance(application);
        mqttSyncDao = database.mqttSyncDao();
        phizioPatientsDao = database.phizioPatientsDao();
        this.count = mqttSyncDao.getEntityCount();
        this.patients = phizioPatientsDao.getAllActivePatients();
    }


    public LiveData<List<PhizioPatients>> getAllPatietns(){
        return patients;
    }

    public void clearDatabase(){
        new DeleteDatabase(database).execute();
    }

    public LiveData<Long> getCount(){
        return count;
    }

    public long insert(final MqttSync mqttSync){
//        new InsertMqttSyncAsyncTask(mqttSyncDao).execute(mqttSync);
//        return mqttSyncDao.insert(mqttSync);
        return mqttSyncDao.insert(mqttSync);
    }

    public void updatePatient(PhizioPatients patient){
        new UpdatePatient(phizioPatientsDao).execute(patient);
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

    private static class DeleteDatabase extends AsyncTask<Void, Void, Void>{
        private PheezeeDatabase database;
        public DeleteDatabase(PheezeeDatabase database){
            this.database = database;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            database.clearAllTables();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.i("deleted","deleted");
        }
    }

    private static class UpdatePatient extends AsyncTask<PhizioPatients, Void, Void>{
        PhizioPatientsDao phizioPatientsDao;
        public UpdatePatient(PhizioPatientsDao dao){
            this.phizioPatientsDao = dao;
        }
        @Override
        protected Void doInBackground(PhizioPatients... patients) {
            phizioPatientsDao.update(patients[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

}
