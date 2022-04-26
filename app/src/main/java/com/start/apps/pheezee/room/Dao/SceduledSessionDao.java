package com.start.apps.pheezee.room.Dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.start.apps.pheezee.room.Entity.SceduledSession;

import java.util.List;

@Dao
public interface SceduledSessionDao {
    @Insert
    void insert(SceduledSession session);

    @Update
    void delete(SceduledSession session);

    @Update
    void update(SceduledSession session);

    @Insert
    void insertAllSceduledSessions(List<SceduledSession> sessions);

    @Query("SELECT COUNT(sessionno) from sceduled_session WHERE patientid=:patientid")
    Long getSessionPresent(String patientid);

    @Query("DELETE FROM sceduled_session WHERE patientid=:patientid")
    void delteAllSessionOfAPatient(String patientid);

    @Query("SELECT * FROM sceduled_session WHERE patientid=:patientid")
    LiveData<List<SceduledSession>> getAllSceduledSessionOfPatient(String patientid);

    @Query("SELECT * FROM sceduled_session WHERE patientid=:patientid")
    List<SceduledSession> getAllSceduledSession(String patientid);

    @Query("DELETE FROM sceduled_session WHERE patientid=:patientid AND sessionno=:sessionno")
    void removeSceduledSessionBasedOnSessionNo(String patientid, int sessionno);
}
