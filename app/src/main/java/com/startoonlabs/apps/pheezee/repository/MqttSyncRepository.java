package com.startoonlabs.apps.pheezee.repository;

import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.lifecycle.LiveData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.startoonlabs.apps.pheezee.activities.LoginActivity;
import com.startoonlabs.apps.pheezee.activities.PatientsView;
import com.startoonlabs.apps.pheezee.pojos.CommentSessionUpdateData;
import com.startoonlabs.apps.pheezee.pojos.DeletePatientData;
import com.startoonlabs.apps.pheezee.pojos.DeleteSessionData;
import com.startoonlabs.apps.pheezee.pojos.ForgotPassword;
import com.startoonlabs.apps.pheezee.pojos.GetReportData;
import com.startoonlabs.apps.pheezee.pojos.GetReportDataResponse;
import com.startoonlabs.apps.pheezee.pojos.LoginData;
import com.startoonlabs.apps.pheezee.pojos.LoginResult;
import com.startoonlabs.apps.pheezee.pojos.MmtData;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PatientImageData;
import com.startoonlabs.apps.pheezee.pojos.PatientImageUploadResponse;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;
import com.startoonlabs.apps.pheezee.pojos.PhizioDetailsData;
import com.startoonlabs.apps.pheezee.pojos.ResponseData;
import com.startoonlabs.apps.pheezee.pojos.SessionData;
import com.startoonlabs.apps.pheezee.pojos.SignUpData;
import com.startoonlabs.apps.pheezee.popup.SessionSummaryPopupWindow;
import com.startoonlabs.apps.pheezee.retrofit.GetDataService;
import com.startoonlabs.apps.pheezee.retrofit.RetrofitClientInstance;
import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Dao.PhizioPatientsDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.PheezeeDatabase;
import com.startoonlabs.apps.pheezee.utils.BitmapOperations;
import com.startoonlabs.apps.pheezee.utils.OtpGeneration;
import com.startoonlabs.apps.pheezee.utils.WriteResponseBodyToDisk;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientId;
import static com.startoonlabs.apps.pheezee.activities.SessionReportActivity.patientName;

/**
 * That interacts with database
 */
public class MqttSyncRepository {
    private MqttSyncDao mqttSyncDao;
    private PhizioPatientsDao phizioPatientsDao;
    GetDataService getDataService;
    onServerResponse listner;
    OnLoginResponse loginlistner;
    OnSignUpResponse signUpResponse;
    OnPhizioDetailsResponseListner phizioDetailsResponseListner;
    OnReportDataResponseListner reportDataResponseListner;
    GetSessionNumberResponse response;
    OnSessionDataResponse onSessionDataResponse;
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

    //Local database functions
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

    public void getPatientSessionNo(String patientid){
        new GetSessionNumber(phizioPatientsDao).execute(patientid);
    }

    public void setPatientSessionNumber(String sessionno,String patientid){
        new SetPatientSessionNumber(phizioPatientsDao).execute(sessionno,patientid);
    }


    public void deleteParticularPatient(String patientid){
        new DeleteParticularPatient(phizioPatientsDao).execute(patientid);
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



    //local database async tasks
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

    private class GetSessionNumber extends AsyncTask<String,Void,String>{
        PhizioPatientsDao patientsDao;
        public GetSessionNumber(PhizioPatientsDao patientsDao){
            this.patientsDao = patientsDao;
        }

        @Override
        protected String doInBackground(String... strings) {
            String sessionno = patientsDao.getPatientSessionNumber(strings[0]);
            int sessionnum = Integer.parseInt(sessionno);
            sessionnum+=1;
            sessionno = String.valueOf(sessionnum);
            return sessionno;
        }

        @Override
        protected void onPostExecute(String s) {
            if(response!=null){
                response.onSessionNumberResponse(s);
            }
            super.onPostExecute(s);
        }
    }

    private class UpdatePatientProfilePicUrl extends AsyncTask<PatientImageUploadResponse,Void,Void>{
        PhizioPatientsDao patientsDao;
        public UpdatePatientProfilePicUrl(PhizioPatientsDao patientsDao){
            this.patientsDao = patientsDao;
        }
        @Override
        protected Void doInBackground(PatientImageUploadResponse... responses) {
            patientsDao.updatePatientProfilePicUrl(responses[0].getUrl(),responses[0].getPatientid());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    private class SetPatientSessionNumber extends AsyncTask<String,Void,Void>{

        PhizioPatientsDao patientsDao;
        public SetPatientSessionNumber(PhizioPatientsDao patientsDao){
            this.patientsDao = patientsDao;
        }

        @Override
        protected Void doInBackground(String... strings) {
            patientsDao.setNumberOfSessions(strings[0],strings[1]);
            return null;
        }
    }

    private class DeleteMultipleSyncItem extends AsyncTask<List<Integer>,Void,Void>{

        MqttSyncDao mqttSyncDao;
        public DeleteMultipleSyncItem(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }
        @Override
        protected Void doInBackground(List<Integer>... lists) {
            mqttSyncDao.deleteMultipleItems(lists[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if(listner!=null){
                listner.onSyncComplete(true,"Sync Completed");
            }
            super.onPostExecute(aVoid);
        }
    }





    //Server related
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
        final String otp = OtpGeneration.OTP(4);
        ForgotPassword password = new ForgotPassword(email,otp);
        Call<String> forgot_password = getDataService.forgotPassword(password);
        forgot_password.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                String res = response.body();
                if(res.equalsIgnoreCase("invalid")){
                    if(loginlistner!=null){
                        loginlistner.onForgotPasswordResponse(false,"Invalid email!");
                    }
                }
                else if(res.equalsIgnoreCase("nsent")){
                    if(loginlistner!=null){
                        loginlistner.onForgotPasswordResponse(false,"Email not sent!");
                    }
                }
                else {
                    if(loginlistner!=null){
                        loginlistner.onForgotPasswordResponse(true,otp);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(loginlistner!=null){
                    loginlistner.onForgotPasswordResponse(false,"Email not sent!");
                }
            }
        });
    }

    public void updatePassword(String email, String password){
        LoginData data = new LoginData(email,password);
        Call<String> update_password_call = getDataService.updatePassword(data);
        update_password_call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200) {
                    String res = response.body();
                    if (res.equalsIgnoreCase("updated")) {
                        if (loginlistner != null) {
                            loginlistner.onPasswordUpdated("Password updated!");
                        }
                    } else {
                        if (loginlistner != null) {
                            loginlistner.onPasswordUpdated("Error please try later");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(loginlistner!=null){
                    loginlistner.onPasswordUpdated("Error please try later");
                }
            }
        });
    }

    public void confirmEmail(String email){
        final String otp = OtpGeneration.OTP(4);
        ForgotPassword password = new ForgotPassword(email,otp);
        Call<String> confirm_email = getDataService.confirmEmail(password);
        confirm_email.enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                if(response.code()==200){
                    String res = response.body();
                    Log.i("res",res);
                    if(res!=null) {
                        if (res.equalsIgnoreCase("sent")) {
                            if (signUpResponse != null) {
                                signUpResponse.onConfirmEmail(true, otp);
                            }
                        } else if (res.equalsIgnoreCase("already")) {
                            if (signUpResponse != null) {
                                signUpResponse.onConfirmEmail(false, "Email already present!");
                            }
                        } else {
                            if (signUpResponse != null) {
                                signUpResponse.onConfirmEmail(false, "Email not sent, try again later!");
                            }
                        }
                    }
                    else {
                        if(signUpResponse!=null){
                            signUpResponse.onConfirmEmail(false,"Error try again later!");
                        }
                    }
                }
                else {
                    if(signUpResponse!=null){
                        signUpResponse.onConfirmEmail(false,"Error try again later!");
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(signUpResponse!=null){
                    signUpResponse.onConfirmEmail(false,"Error try again later!");
                }
            }
        });
    }

    public void signUp(SignUpData data){
        Call<String> sign_up = getDataService.signUp(data);
        sign_up.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200) {
                    String res = response.body();
                    if (res != null) {
                        if (res.equalsIgnoreCase("inserted")) {
                            editor = sharedPref.edit();
                            editor.putBoolean("isLoggedIn", true);
                            editor.commit();
                            editor = sharedPref.edit();
                            JSONObject jsonObject = new JSONObject();
                            try {
                                jsonObject.put("phizioname", data.getPhizioname());
                                jsonObject.put("phizioemail", data.getPhizioemail());
                                jsonObject.put("phiziophone", data.getPhone());
                                jsonObject.put("phizioprofilepicurl", data.getPhizioprofilepicurl());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            editor.putString("phiziodetails", jsonObject.toString());
                            editor.commit();
                            if (sharedPref.getInt("maxid", -1) == -1) {
                                editor = sharedPref.edit();
                                editor.putInt("maxid", 0);
                                editor.apply();
                            }
                            if (signUpResponse != null) {
                                signUpResponse.onSignUp(true);
                            }
                        } else {
                            if (signUpResponse != null) {
                                signUpResponse.onSignUp(false);
                            }
                        }
                    }
                }
                else {
                    if(signUpResponse!=null){
                        signUpResponse.onSignUp(false);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(signUpResponse!=null){
                    signUpResponse.onSignUp(false);
                }
            }
        });
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
                    else {
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


    public void uploadPatientImage(String patientid, String phizioemail, Bitmap bitmap){
        PatientImageData data = new PatientImageData(patientid,phizioemail,BitmapOperations.bitmapToString(bitmap));
        Call<PatientImageUploadResponse> upload_patient_image = getDataService.uploadPatientProfilePicture(data);
        upload_patient_image.enqueue(new Callback<PatientImageUploadResponse>() {
            @Override
            public void onResponse(Call<PatientImageUploadResponse> call, Response<PatientImageUploadResponse> response) {
                if(response.code()==200){
                    PatientImageUploadResponse res = response.body();
                    if(res.getIsvalid()){
                        new UpdatePatientProfilePicUrl(phizioPatientsDao).execute(res);
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientImageUploadResponse> call, Throwable t) {

            }
        });
    }

    public void updatePhizioDetails(PhizioDetailsData data){
        Call<String> update_phizio_details = getDataService.updatePhizioDetails(data);
        update_phizio_details.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    String res = response.body();
                    if(res.equalsIgnoreCase("updated")){
                        JSONObject json_phizio = null;
                        try {
                            json_phizio = new JSONObject(sharedPref.getString("phiziodetails",""));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor = sharedPref.edit();
                        try {
                            json_phizio.put("phizioname",data.getPhizioname());
                            json_phizio.put("phiziophone",data.getPhiziophone());
                            json_phizio.put("clinicname",data.getClinicname());
                            json_phizio.put("phiziodob",data.getPhiziodob());
                            json_phizio.put("experience",data.getExperience());
                            json_phizio.put("specialization",data.getSpecialization());
                            json_phizio.put("degree",data.getDegree());
                            json_phizio.put("gender",data.getGender());
                            json_phizio.put("address",data.getAddress());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.putString("phiziodetails",json_phizio.toString());
                        editor.commit();
                        if(phizioDetailsResponseListner!=null){
                            phizioDetailsResponseListner.onDetailsUpdated(true);
                        }
                    }
                    else {
                        if(phizioDetailsResponseListner!=null){
                            phizioDetailsResponseListner.onDetailsUpdated(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                if(phizioDetailsResponseListner!=null){
                    phizioDetailsResponseListner.onDetailsUpdated(false);
                }
            }
        });
    }

    public void updatePhizioProfilePic(String phizioemail, Bitmap photo){
        PatientImageData data = new PatientImageData(null,phizioemail, BitmapOperations.bitmapToString(photo));
        Call<PatientImageUploadResponse> update_phizio_pic = getDataService.updatePhizioProfilePic(data);
        update_phizio_pic.enqueue(new Callback<PatientImageUploadResponse>() {
            @Override
            public void onResponse(Call<PatientImageUploadResponse> call, Response<PatientImageUploadResponse> response) {
                if(response.code()==200){
                    PatientImageUploadResponse response1 = response.body();
                    if(response1.getIsvalid()){
                        try {
                            JSONObject object = new JSONObject(sharedPref.getString("phiziodetails",""));
                            object.put("phizioprofilepicurl",response1.getUrl());
                            editor = sharedPref.edit();
                            editor.putString("phiziodetails",object.toString());
                            editor.commit();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(phizioDetailsResponseListner!=null){
                            phizioDetailsResponseListner.onProfilePictureUpdated(true);
                        }
                    }
                    else {
                        if(phizioDetailsResponseListner!=null){
                            phizioDetailsResponseListner.onProfilePictureUpdated(false);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<PatientImageUploadResponse> call, Throwable t) {
                if(phizioDetailsResponseListner!=null){
                    phizioDetailsResponseListner.onProfilePictureUpdated(false);
                }
            }
        });
    }

    public void getReportData(String email, String patientid){
        Call<List<GetReportDataResponse>> get_report = getDataService.getReportData(new GetReportData(email,patientid));
        get_report.enqueue(new Callback<List<GetReportDataResponse>>() {
            @Override
            public void onResponse(Call<List<GetReportDataResponse>> call, Response<List<GetReportDataResponse>> response) {
                if(response.code()==200){
                    List<GetReportDataResponse> res = response.body();
                    Gson gson = new GsonBuilder().create();
                    String json = gson.toJson(res);
                    try {
                        JSONArray array = new JSONArray(json);
                        Log.i("GSON", array.toString());
                        if(reportDataResponseListner!=null){
                            reportDataResponseListner.onReportDataReceived(array,true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<List<GetReportDataResponse>> call, Throwable t) {
                Log.i("res",t.getMessage());
                if(reportDataResponseListner!=null){
                    reportDataResponseListner.onReportDataReceived(new JSONArray(),false);
                }
            }
        });
    }


    public void getDayReport(String url, String patientname){
        Call<ResponseBody> fileCall = getDataService.getReport(url);
        fileCall.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.i("Response", response.body().toString());
                File file = WriteResponseBodyToDisk.writeResponseBodyToDisk(response.body(), patientname+"-day");
                if (file != null) {
                    if(reportDataResponseListner!=null){
                        reportDataResponseListner.onDayReportReceived(file,null,true);
                    }
                }
                else {
                    if(reportDataResponseListner!=null) {
                        reportDataResponseListner.onDayReportReceived(file, "Not received!", false);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(reportDataResponseListner!=null) {
                    reportDataResponseListner.onDayReportReceived(null, "Server not responding, try again later", false);
                }
            }
        });
    }




    public void insertSessionData(SessionData data){
        Call<ResponseData> dataCall = getDataService.insertSessionData(data);
        dataCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if(response.code()==200){
                    ResponseData res = response.body();
                    if(res.getResponse().equalsIgnoreCase("inserted")){
                        deleteParticular(res.getId());
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onInsertSessionData(true,res.getResponse().toUpperCase());
                        }
                    }
                    else {
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onInsertSessionData(true,"Not inserted, Try again later");
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                if(onSessionDataResponse!=null){
                    onSessionDataResponse.onInsertSessionData(true,"Error, try again later");
                }
            }
        });
    }

    public void deleteSessionData(DeleteSessionData data){
        Call<ResponseData> dataCall = getDataService.deletePatientSession(data);
        dataCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if(response.code()==200){
                    ResponseData res = response.body();
                    if(res.getResponse().equalsIgnoreCase("deleted")){
                        deleteParticular(res.getId());
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onSessionDeleted(true,res.getResponse().toUpperCase());
                        }
                    }
                    else {
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onSessionDeleted(true,"Error, try again later");
                        }
                    }
                }
                else {
                    if(onSessionDataResponse!=null){
                        onSessionDataResponse.onSessionDeleted(true,"There is some issue with the server, please try again later");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                if(onSessionDataResponse!=null){
                    onSessionDataResponse.onSessionDeleted(true,"There is some issue with the server, please try again later");
                }
            }
        });
    }

    public void updateMmtData(MmtData data){
        Call<ResponseData> dataCall = getDataService.updateMmtData(data);
        dataCall.enqueue(new Callback<ResponseData>() {
            @Override
            public void onResponse(Call<ResponseData> call, Response<ResponseData> response) {
                if(response.code()==200){
                    ResponseData res = response.body();
                    if(res.getResponse().equalsIgnoreCase("updated")){
                        deleteParticular(res.getId());
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onMmtValuesUpdated(true,res.getResponse().toUpperCase());
                        }
                    }
                    else {
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onMmtValuesUpdated(true,"Error, try again later");
                        }
                    }
                }
                else {
                    if(onSessionDataResponse!=null){
                        onSessionDataResponse.onSessionDeleted(true,"There is some issue with the server, please try again later");
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseData> call, Throwable t) {
                if(onSessionDataResponse!=null){
                    onSessionDataResponse.onSessionDeleted(true,"There is some issue with the server, please try again later");
                }
            }
        });
    }

    public void updateCommentData(CommentSessionUpdateData data){
        Call<String> comment_data = getDataService.updateCommentData(data);
        comment_data.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if(response.code()==200){
                    if(response.body().equalsIgnoreCase("updated")){
                        if(onSessionDataResponse!=null){
                            onSessionDataResponse.onCommentSessionUpdated(true);
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });
    }

    public void syncDataToServer(){
        Log.i("hello","insidesync");
        new SyncDataAsync(mqttSyncDao).execute();
    }

    private class SyncDataAsync extends AsyncTask<Void,Void,List<MqttSync>>{

        MqttSyncDao mqttSyncDao;
        public SyncDataAsync(MqttSyncDao mqttSyncDao){
            this.mqttSyncDao = mqttSyncDao;
        }
        @Override
        protected List<MqttSync> doInBackground(Void... voids) {
            List<MqttSync> list = mqttSyncDao.getAllMqttSyncItems();
            return list;
        }

        @Override
        protected void onPostExecute(List<MqttSync> mqttSyncs) {
            super.onPostExecute(mqttSyncs);
            startSync(mqttSyncs);
        }
    }

    private void startSync(List<MqttSync> mqttSyncs) {
        Call<List<Integer>> sync_data = getDataService.syncDataToServer(mqttSyncs);
        sync_data.enqueue(new Callback<List<Integer>>() {
            @Override
            public void onResponse(Call<List<Integer>> call, Response<List<Integer>> response) {
                if(response.code()==200){
                    List<Integer> list = response.body();
                    new DeleteMultipleSyncItem(mqttSyncDao).execute(list);
                }
                else {
                    if(listner!=null){
                        listner.onSyncComplete(false,"Server busy, try again later!");
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Integer>> call, Throwable t) {
                if(listner!=null){
                    listner.onSyncComplete(false,"Server busy, try again later!");
                }
            }
        });

    }


    //Callbacks
    public interface OnSessionDataResponse{
        void onInsertSessionData(Boolean response, String message);
        void onSessionDeleted(Boolean response, String message);
        void onMmtValuesUpdated(Boolean response, String message);
        void onCommentSessionUpdated(Boolean response);
    }


    public interface GetSessionNumberResponse{
        void onSessionNumberResponse(String sessionnumber);
    }

    public interface OnReportDataResponseListner{
        void onReportDataReceived(JSONArray array, boolean response);
        void onDayReportReceived(File file, String message, Boolean response);
    }

    public interface OnPhizioDetailsResponseListner{
        void onDetailsUpdated(Boolean response);
        void onProfilePictureUpdated(Boolean response);
    }

    public interface OnSignUpResponse{
        void onConfirmEmail(boolean response, String message);
        void onSignUp(boolean response);
    }


    public interface OnLoginResponse{
        void onLoginResponse(boolean response, String message);
        void onForgotPasswordResponse(boolean response, String message);
        void onPasswordUpdated( String message);
    }

    public interface onServerResponse{
        void onDeletePateintResponse(boolean response);
        void onUpdatePatientDetailsResponse(boolean response);
        void onUpdatePatientStatusResponse(boolean response);
        void onSyncComplete(boolean response, String message);
    }

    public void setOnPhizioDetailsResponseListner(OnPhizioDetailsResponseListner phizioDetailsResponseListner){
        this.phizioDetailsResponseListner = phizioDetailsResponseListner;
    }

    public void setOnLoginResponse(OnLoginResponse loginlistner){
        this.loginlistner = loginlistner;
    }

    public void setOnServerResponseListner(onServerResponse listner){
        this.listner = listner;
    }

    public void setOnSignUpResponse(OnSignUpResponse signUpResponse){
        this.signUpResponse = signUpResponse;
    }

    public void setOnReportDataResponseListener(OnReportDataResponseListner reportDataResponseListener){
        this.reportDataResponseListner = reportDataResponseListener;
    }

    public void setOnSessionNumberResponse(GetSessionNumberResponse response){
        this.response = response;
    }

    public void setOnSessionDataResponse(OnSessionDataResponse onSessionDataResponse){
        this.onSessionDataResponse = onSessionDataResponse;
    }
}
