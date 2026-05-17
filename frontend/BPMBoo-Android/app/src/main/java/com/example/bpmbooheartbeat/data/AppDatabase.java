// data/AppDatabase.java
package com.example.bpmbooheartbeat.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.bpmbooheartbeat.data.dao.HeartRateDao;
import com.example.bpmbooheartbeat.data.model.HeartRateRecord;

@Database(entities = {HeartRateRecord.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract HeartRateDao heartRateDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "bpm_boo_heartbeat_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}