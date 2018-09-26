package com.xlog.xloguser.finaldriverapp.Room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.xlog.xloguser.finaldriverapp.Room.Dao.Dao;
import com.xlog.xloguser.finaldriverapp.Room.Entity.TokenEntity;

@Database(entities = { TokenEntity.class }, version = 1)
public abstract class RmDatabase extends RoomDatabase {

    private static final String DB_NAME = "RoomDatabase.db";
    private static RmDatabase INSTANCE;

    public abstract Dao rmDao();

    public static RmDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (RmDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            RmDatabase.class, DB_NAME)
                            // Wipes and rebuilds instead of migrating
                            // if no Migration object.
                            // Migration is not part of this practical.
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;

    }
}