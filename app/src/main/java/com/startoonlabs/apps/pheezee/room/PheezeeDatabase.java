package com.startoonlabs.apps.pheezee.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;

@Database(entities = MqttSync.class, version = 1, exportSchema = false)
public abstract class PheezeeDatabase extends RoomDatabase {
    private static PheezeeDatabase instance;

    public abstract MqttSyncDao mqttSyncDao();


    public static synchronized PheezeeDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),PheezeeDatabase.class,"pheezee_database")
//                    .fallbackToDestructiveMigration()
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
