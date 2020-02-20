package com.startoonlabs.apps.pheezee.room.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;
import com.startoonlabs.apps.pheezee.room.Entity.SceduledSession;

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
}
