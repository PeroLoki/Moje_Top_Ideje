package com.example.mojesuperideje;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
@Entity
public class Baza {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "opis")
    public String opis;

    @ColumnInfo(name = "kategorija")
    public String kategorija;

    @ColumnInfo(name = "solved")
    public boolean solved;
}
