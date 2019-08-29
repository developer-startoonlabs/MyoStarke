package com.startoonlabs.apps.pheezee.repository;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.startoonlabs.apps.pheezee.activities.LoginActivity;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.pojos.DeletePatientData;
import com.startoonlabs.apps.pheezee.pojos.LoginData;
import com.startoonlabs.apps.pheezee.pojos.LoginResult;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Dao.PhizioPatientsDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * That interacts with database
 */
public class MqttSyncRepository {
    private MqttSyncDao mqttSyncDao;
    private PhizioPatientsDao phizioPatientsDao;
    GetDataService getDataService;
    onServerResponse listner;
    OnLoginResponse loginlistner;
    /**
     * Live object returned to get the item count in the database to update the sync button view
     */
    private LiveData<Long> count;
    private LiveData<List<PhizioPatients>> patients;
    private PheezeeDatabase database;
    SharedPreferences sharedPref;
    SharedPreferences.Editor editor;

    public MqttSyncRepository(Application application){
        database =PheezeeDatabase.getInstance(application);
        mqttSyncDao = database.mqttSyncDao();
        phizioPatientsDao = database.phizioPatientsDao();
        this.count = mqttSyncDao.getEntityCount();
        this.patients = phizioPatientsDao.getAllActivePatients();
        getDataService = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(application);
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

    public void updatePatientLocally(PhizioPatients patient){
        new UpdatePatient(phizioPatientsDao).execute(patient);
    }

    public void insertPatient(PhizioPatients patient){
        new InsertPhizioPatient(phizioPatientsDao).execute(patient);
    }

    public void deletePatient(PhizioPatients patient){
        new DeletePatient(phizioPatientsDao).execute(patient);
    }

    public void deleteParticularPatient(String patientid){
        new DeleteParticularPatient(phizioPatientsDao).execute(patientid);
    }

    public void insertManyPatient(List<PhizioPatients> patients){

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

    private static class InsertPhizioPatient extends AsyncTask<PhizioPatients, Void, Void>{
        PhizioPatientsDao phizioPatientsDao;
        public InsertPhizioPatient(PhizioPatientsDao dao){
            this.phizioPatientsDao = dao;
        }
        @Override
        protected Void doInBackground(PhizioPatients... patients) {
            phizioPatientsDao.insert(patients[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class DeleteParticularPatient extends AsyncTask<String, Void, Void>{
        PhizioPatientsDao phizioPatientsDao;
        public DeleteParticularPatient(PhizioPatientsDao dao){
            this.phizioPatientsDao = dao;
        }
        @Override
        protected Void doInBackground(String... patientid) {
            phizioPatientsDao.deleteParticularPatient(patientid[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private static class DeletePatient extends AsyncTask<PhizioPatients, Void, Void>{
        PhizioPatientsDao phizioPatientsDao;
        public DeletePatient(PhizioPatientsDao dao){
            this.phizioPatientsDao = dao;
        }
        @Override
        protected Void doInBackground(PhizioPatients... patient) {
            phizioPatientsDao.delete(patient[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }


    public void deletePatientFromServer(DeletePatientData deletePatientData,PhizioPatients patient){
        Call<String> delete_call = getDataService.deletePatient(deletePatientData);
        delete_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    String response_delete = response.body();
                    if(response_delete.equalsIgnoreCase("deleted")){
                        Log.i("response",response.body());
                        new DeletePatient(phizioPatientsDao).execute(patient);
                        if(listner!=null){
                            listner.onDeletePateintResponse(true);
                        }
                    }
                }
                else {
                    listner.onUpdatePatientDetailsResponse(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(listner!=null)
                    listner.onDeletePateintResponse(false);
            }
        });
    }

    //Update the patient status on server
    public void updatePatientStatusServer(PhizioPatients patient, PatientStatusData data) {
        Call<String> call = getDataService.updatePatientStatus(data);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    String status_response = response.body();
                    if(status_response.equalsIgnoreCase("inserted")){
                        updatePatientLocally(patient);
                        if(listner!=null){
                            listner.onUpdatePatientStatusResponse(true);
                        }
                    }else {
                        listner.onUpdatePatientStatusResponse(false);
                    }
                }
                else {
                    listner.onUpdatePatientDetailsResponse(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(listner!=null)
                    listner.onUpdatePatientStatusResponse(false);
            }
        });
    }

    //update patient details on the server
    public void updatePatientDetailsServer(PhizioPatients patient, PatientDetailsData data){
        Call<String> call = getDataService.updatePatientDetails(data);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    String update_response = response.body();
                    if(update_response.equalsIgnoreCase("updated")){
                        updatePatientLocally(patient);
                        if(listner!=null){
                            listner.onUpdatePatientDetailsResponse(true);
                        }
                    }
                    else {
                        listner.onUpdatePatientDetailsResponse(false);
                    }
                }
                else {
                    listner.onUpdatePatientDetailsResponse(false);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(listner!=null){
                    listner.onUpdatePatientDetailsResponse(false);
                }
            }
        });
    }

    public void forgotPassword(String email){

    }



    public void loginUser(String email,String password){
        editor = sharedPref.edit();
        final int[] maxid = {0};
        Call<List<LoginResult>> login = getDataService.login(new LoginData(email,password));
        login.enqueue(new Callback<List<LoginResult>>() {
            @Override
            public void onResponse(Call<List<LoginResult>> call, Response<List<LoginResult>> response) {
                List<LoginResult> results = response.body();
                if(results.get(0).getIsvalid()){
                    String name = results.get(0).getPhizioname();
                    editor = sharedPref.edit();
                    editor.putBoolean("isLoggedIn",true);
                    JSONObject object = new JSONObject();
                    try {
                        object.put("phizioname",results.get(0).getPhizioname());
                        object.put("phizioemail",results.get(0).getPhizioemail());
                        object.put("phiziophone",results.get(0).getPhiziophone());
                        object.put("phizioprofilepicurl",results.get(0).getPhizioprofilepicurl());
                        object.put("address",results.get(0).getAddress());
                        object.put("clinicname",results.get(0).getClinicname());
                        object.put("degree",results.get(0).getDegree());
                        object.put("experience",results.get(0).getExperience());
                        object.put("gender",results.get(0).getGender());
                        object.put("phiziodob",results.get(0).getPhiziodob());
                        object.put("specialization",results.get(0).getSpecialization());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    editor.putString("phiziodetails",object.toString());
                    editor.commit();
                    List<PhizioPatients> phiziopatients = results.get(0).getPhiziopatients();

                    if(phiziopatients.size()>0 && sharedPref.getInt("maxid",-1)==-1){
                        for (int i=0;i<phiziopatients.size();i++){
                            try {
                                int id = Integer.parseInt(phiziopatients.get(i).getPatientid());
                                if(id> maxid[0]){
                                    maxid[0] = id;
                                }
                            }catch (NumberFormatException e){
                                Log.i("Exception",e.getMessage());
                            }
                        }
                        editor = sharedPref.edit();
                        editor.putInt("maxid", maxid[0]);
                        editor.apply();
                    }
                    new InsertAllPatients(phizioPatientsDao).execute(phiziopatients);
                }
                else {
                    if(loginlistner!=null) {
                        loginlistner.onLoginResponse(false, "Invalid Credentials");
                    }
                }

            }

            @Override
            public void onFailure(Call<List<LoginResult>> call, Throwable t) {
                if(loginlistner!=null) {
                    loginlistner.onLoginResponse(false, "Invalid Credentials");
                }
            }
        });
    }


    private class InsertAllPatients extends AsyncTask<List<PhizioPatients>,Void,Void>{
        PhizioPatientsDao patientsDao;
        public InsertAllPatients(PhizioPatientsDao patientsDao){
            this.patientsDao = patientsDao;
        }
        @Override
        protected Void doInBackground(List<PhizioPatients>... lists) {
            patientsDao.insertAllPatients(lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            String email="";
            try {
                JSONObject object = new JSONObject(sharedPref.getString("phiziodetails",""));
                email = object.getString("phizioname");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(loginlistner!=null){
                loginlistner.onLoginResponse(true,email);
            }
        }
    }



    public interface OnLoginResponse{
        void onLoginResponse(boolean response, String message);
    }

    public void setOnLoginResponse(OnLoginResponse loginlistner){
        this.loginlistner = loginlistner;
    }




    public interface onServerResponse{
        void onDeletePateintResponse(boolean response);
        void onUpdatePatientDetailsResponse(boolean response);
        void onUpdatePatientStatusResponse(boolean response);
    }

    public void setOnServerResponseListner(onServerResponse listner){
        this.listner = listner;
    }
}
