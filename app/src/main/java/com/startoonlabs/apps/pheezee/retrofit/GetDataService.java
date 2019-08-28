package com.startoonlabs.apps.pheezee.retrofit;

import com.startoonlabs.apps.pheezee.pojos.LoginData;
import com.startoonlabs.apps.pheezee.pojos.LoginResult;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Url;

/**
 *
 */
public interface GetDataService {
    @GET
    Call<ResponseBody> getReport(@Url String url);


    @POST("/api/login/phizio")
    Call<List<LoginResult>> login(@Body LoginData object);

    @POST("/api/phizio/update/patientStatus")
    Call<String> updatePatientStatus(@Body PatientStatusData object);

    @POST("/api/phizio/updatepatientdetails")
    Call<String> updatePatientDetails(@Body PatientDetailsData patient);


}
