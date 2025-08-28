package com.example.mojesuperideje;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = { Baza.class }, version = 1, exportSchema = false)
public abstract class Databaza extends RoomDatabase {
    public abstract BazaDao bazaDao();

    private static volatile Databaza INSTANCE;

    public static Databaza getInstance(Context ctx) {
        if (INSTANCE == null) {
            synchronized (Databaza.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    ctx.getApplicationContext(),
                                    Databaza.class,
                                    "moje_super_ideje.db"
                            )
                            .enableMultiInstanceInvalidation()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
