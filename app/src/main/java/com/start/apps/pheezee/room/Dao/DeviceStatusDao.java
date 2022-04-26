package com.start.apps.pheezee.room.Dao;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.start.apps.pheezee.room.Entity.DeviceStatus;

@Dao
public interface DeviceStatusDao {
    @Insert
    long insert(DeviceStatus deviceStatus);

    @Delete
    void delete(DeviceStatus deviceStatus);

    @Query("SELECT * from DeviceStatus WHERE uid=:uid")
    DeviceStatus getDeviceStatus(String uid);

    @Query("DELETE FROM DeviceStatus WHERE uid=:uid")
    void deleteDevice(String uid);
}