package com.startoonlabs.apps.pheezee.retrofit;

import com.startoonlabs.apps.pheezee.pojos.AddPatientData;
import com.startoonlabs.apps.pheezee.pojos.CommentSessionUpdateData;
import com.startoonlabs.apps.pheezee.pojos.DeletePatientData;
import com.startoonlabs.apps.pheezee.pojos.DeleteSessionData;
import com.startoonlabs.apps.pheezee.pojos.DeviceDeactivationStatus;
import com.startoonlabs.apps.pheezee.pojos.DeviceDeactivationStatusResponse;
import com.startoonlabs.apps.pheezee.pojos.DeviceDetailsData;
import com.startoonlabs.apps.pheezee.pojos.DeviceLocationStatus;
import com.startoonlabs.apps.pheezee.pojos.FirmwareData;
import com.startoonlabs.apps.pheezee.pojos.FirmwareUpdateCheck;
import com.startoonlabs.apps.pheezee.pojos.FirmwareUpdateCheckResponse;
import com.startoonlabs.apps.pheezee.pojos.ForgotPassword;
import com.startoonlabs.apps.pheezee.pojos.GetReportData;
import com.startoonlabs.apps.pheezee.pojos.GetReportDataResponse;
import com.startoonlabs.apps.pheezee.pojos.HealthData;
import com.startoonlabs.apps.pheezee.pojos.LoginData;
import com.startoonlabs.apps.pheezee.pojos.LoginResult;
import com.startoonlabs.apps.pheezee.pojos.MmtData;
import com.startoonlabs.apps.pheezee.pojos.PatientDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PatientImageData;
import com.startoonlabs.apps.pheezee.pojos.PatientImageUploadResponse;
import com.startoonlabs.apps.pheezee.pojos.PatientStatusData;
import com.startoonlabs.apps.pheezee.pojos.PhizioDetailsData;
import com.startoonlabs.apps.pheezee.pojos.PhizioEmailData;
import com.startoonlabs.apps.pheezee.pojos.ResponseData;
import com.startoonlabs.apps.pheezee.pojos.SessionData;
import com.startoonlabs.apps.pheezee.pojos.SignUpData;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;

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

    @POST("/api/phizio/addpatient")
    Call<ResponseData> addPatient(@Body AddPatientData patientData);

    @POST("/api/phizio/deletepatient")
    Call<String> deletePatient(@Body DeletePatientData data);

    @POST("/api/forgot/password")
    Call<String> forgotPassword(@Body ForgotPassword object);

    @POST("/api/phizioprofile/update/password")
    Call<String> updatePassword(@Body LoginData data);

    @POST("/api/confirm/email")
    Call<String> confirmEmail(@Body ForgotPassword object);

    @POST("/api/signup/phizio")
    Call<String> signUp(@Body SignUpData data);

    @POST("/api/phizio/update/patientProfilePic")
    Call<PatientImageUploadResponse> uploadPatientProfilePicture(@Body PatientImageData data);

    @POST("/api/phizioprofile/update")
    Call<String> updatePhizioDetails(@Body PhizioDetailsData data);

    @POST("/api/phizio/profilepic/upload")
    Call<PatientImageUploadResponse> updatePhizioProfilePic(@Body PatientImageData data);

    @POST("/api/phizio/cliniclogo/upload")
    Call<PatientImageUploadResponse> updatePhizioClinicLogoPic(@Body PatientImageData data);

    @POST("/api/patient/generate/report")
    Call<List<GetReportDataResponse>> getReportData(@Body GetReportData data);


    @POST("/api/patient/entireEmgData")
    Call<ResponseData> insertSessionData(@Body SessionData data);

    @POST("/api/phizio/patient/deletepatient/sesssion")
    Call<ResponseData> deletePatientSession(@Body DeleteSessionData data);

    @POST("/api/phizio/patient/updateMmtGrade")
    Call<ResponseData> updateMmtData(@Body MmtData data);

    @POST("/api/phizio/patient/updateCommentSection")
    Call<String> updateCommentData(@Body CommentSessionUpdateData data);

    @POST("/api/sync/data")
    Call<List<Integer>> syncDataToServer(@Body List<MqttSync> sync);

    @POST("/api/firmware/log")
    Call<Boolean> sendFirmwareLog(@Body FirmwareData log);

    @POST("/api/firmware/update/check")
    Call<FirmwareUpdateCheckResponse> checkFirmwareUpdateAndGetLink(@Body FirmwareUpdateCheck check);

    @POST("/api/insert/pheezee/health/status")
    Call<Boolean> sendHealthStatusOfDevice(@Body HealthData data);

    @POST("/api/update/device/location")
    Call<Boolean> sendDeviceLocationUpdate(@Body DeviceLocationStatus data);

    @POST("/api/insert/pheezee/device")
    Call<Boolean> sendDeviceDetailsToTheServer(@Body DeviceDetailsData data);

    @POST("/api/update/device/email/used")
    Call<Boolean> sendEmailUsedWithDevice(@Body PhizioEmailData data);

    @POST("/api/get/device/status")
    Call<DeviceDeactivationStatusResponse> getDeviceStatus(@Body DeviceDeactivationStatus status);

}
