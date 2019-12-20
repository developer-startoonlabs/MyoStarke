package com.startoonlabs.apps.pheezee.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.startoonlabs.apps.pheezee.room.Dao.DeviceStatusDao;
import com.startoonlabs.apps.pheezee.room.Dao.MqttSyncDao;
import com.startoonlabs.apps.pheezee.room.Dao.PhizioPatientsDao;
import com.startoonlabs.apps.pheezee.room.Entity.DeviceStatus;
import com.startoonlabs.apps.pheezee.room.Entity.MqttSync;
import com.startoonlabs.apps.pheezee.room.Entity.PhizioPatients;

@Database(entities = {MqttSync.class, PhizioPatients.class, DeviceStatus.class}, version = 3, exportSchema = false)
public abstract class PheezeeDatabase extends RoomDatabase {
    private static PheezeeDatabase instance;

    public abstract MqttSyncDao mqttSyncDao();
    public abstract PhizioPatientsDao phizioPatientsDao();
    public abstract DeviceStatusDao deviceStatusDao();

    public static synchronized PheezeeDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),PheezeeDatabase.class,"pheezee_database")
//                   .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_2_3)
                    .build();
        }
        return instance;
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE `DeviceStatus` (`uid` TEXT PRIMARY KEY NOT NULL, "
                    + "`status` INTEGER NOT NULL)");
        }
    };
}
