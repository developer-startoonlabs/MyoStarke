package com.start.apps.pheezee.room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.start.apps.pheezee.room.Entity.PhizioPatients;

import java.util.List;

@Dao
public interface PhizioPatientsDao {
    @Insert
    void insert(PhizioPatients patient);

    @Update
    void update(PhizioPatients patient);

    @Delete
    void delete(PhizioPatients patient);

    @Insert
    void insertAllPatients(List<PhizioPatients> patients);

    @Query("SELECT * from phizio_patients WHERE status LIKE 'Active'")
    LiveData<List<PhizioPatients>> getAllActivePatients();

    @Query("Select * from phizio_patients WHERE patientid=:id")
    PhizioPatients getPatient(String id);

    @Query("UPDATE phizio_patients SET status=:status WHERE patientid=:patientid")
    void updatePatientStatus(String status, String patientid);

    @Query("DELETE FROM phizio_patients WHERE patientid=:patientid")
    void deleteParticularPatient(String patientid);

    @Query("UPDATE phizio_patients SET patientprofilepicurl=:url WHERE patientid=:patientid")
    void updatePatientProfilePicUrl(String url, String patientid);

    @Query("SELECT numofsessions FROM phizio_patients WHERE patientid=:patientid")
    String getPatientSessionNumber(String patientid);

    @Query("UPDATE phizio_patients SET numofsessions=:numberOfSessions WHERE patientid=:patientid")
    void setNumberOfSessions(String numberOfSessions, String patientid);

    @Query("UPDATE phizio_patients SET  patientgender=:pateintgender, patientcasedes=:casedes, patientage=:patientage, patientname=:patientname " +
            "WHERE patientid=:patientid")
    void updatePatientDetails(String patientname, String patientid, String patientage, String casedes, String pateintgender);

    @Query("SELECT sceduled FROM phizio_patients WHERE patientid=:patientid")
    boolean isSessionSceduled(String patientid);

    @Query("UPDATE phizio_patients SET sceduled=:num WHERE patientid=:patientid")
    void updateSceduledSessionStatus(String patientid, boolean num);


}
