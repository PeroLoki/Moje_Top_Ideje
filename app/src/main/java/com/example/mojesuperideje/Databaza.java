package com.example.mojesuperideje;

import androidx.room.Database;
import androidx.room.RoomDatabase;
@Database(entities = {Baza.class}, version = 1)
public abstract class Databaza extends RoomDatabase {
    public abstract com.example.mojesuperideje.BazaDao bazaDao();
}
