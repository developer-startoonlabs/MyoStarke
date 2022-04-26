package com.start.apps.pheezee.room.Dao;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.start.apps.pheezee.room.Entity.MqttSync;

import java.util.List;

@Dao
public interface MqttSyncDao {
    @Insert
    long insert(MqttSync mqttSync);

    @Update
    void update(MqttSync mqttSync);

    @Delete
    void delete(MqttSync mqttSync);

    @Query("Delete from mqtt_sync")
    void deleteAllMqttSync();

    @Query("select * from mqtt_sync")
    List<MqttSync> getAllMqttSyncItems();

    @Query("Delete from mqtt_sync where id=:mqttSyncId")
    void deleteParticular(int mqttSyncId);

    @Query("SELECT COUNT(topic) From mqtt_sync")
    LiveData<Long> getEntityCount();

    @Query("DELETE FROM mqtt_sync WHERE id IN (:list)")
    void deleteMultipleItems(List<Integer> list);
}
