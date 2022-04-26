package com.start.apps.pheezee.room;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.start.apps.pheezee.room.Dao.DeviceStatusDao;
import com.start.apps.pheezee.room.Dao.MqttSyncDao;
import com.start.apps.pheezee.room.Dao.PhizioPatientsDao;
import com.start.apps.pheezee.room.Dao.SceduledSessionDao;
import com.start.apps.pheezee.room.Entity.DeviceStatus;
import com.start.apps.pheezee.room.Entity.MqttSync;
import com.start.apps.pheezee.room.Entity.PhizioPatients;
import com.start.apps.pheezee.room.Entity.SceduledSession;

@Database(entities = {MqttSync.class, PhizioPatients.class, DeviceStatus.class, SceduledSession.class}, version = 4, exportSchema = false)
public abstract class PheezeeDatabase extends RoomDatabase {
    private static PheezeeDatabase instance;

    public abstract MqttSyncDao mqttSyncDao();
    public abstract PhizioPatientsDao phizioPatientsDao();
    public abstract DeviceStatusDao deviceStatusDao();
    public abstract SceduledSessionDao sceduledSessionDao();

    public static synchronized PheezeeDatabase getInstance(Context context){
        if (instance==null){
            instance = Room.databaseBuilder(context.getApplicationContext(),PheezeeDatabase.class,"pheezee_database")
//                   .fallbackToDestructiveMigration()
                    .addMigrations(MIGRATION_2_3, MIGRATION_3_4)
                    .build();
        }
        return instance;
    }

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `DeviceStatus` (`uid` TEXT PRIMARY KEY NOT NULL, "
                    + "`status` INTEGER NOT NULL)");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE `phizio_patients` ADD COLUMN `sceduled` INTEGER NOT NULL DEFAULT 0");
            database.execSQL("CREATE TABLE IF NOT EXISTS `sceduled_session` (`id` INTEGER PRIMARY KEY NOT NULL, `patientid` TEXT NOT NULL, "
                    + "`sessionno` INTEGER NOT NULL, `bodypart` TEXT NOT NULL, `side` TEXT NOT NULL, `position` TEXT NOT NULL," +
                    "`exercise` TEXT NOT NULL, `muscle` TEXT NOT NULL, `reps` TEXT NOT NULL, `emg` TEXT NOT NULL," +
                    "`angleMin` TEXT NOT NULL, `angleMax` TEXT NOT NULL)");
        }
    };
}
