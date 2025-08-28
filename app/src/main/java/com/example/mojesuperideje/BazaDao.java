package com.example.mojesuperideje;


import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;



import java.util.List;
@Dao
public interface BazaDao {
    @Insert
    long insert(Baza note);
    @Query("SELECT * FROM Baza")
    List<Baza> getAll();

    @Query("SELECT * FROM baza ORDER BY id DESC")
    LiveData<List<Baza>> observeAll();

    @Query("SELECT * FROM baza WHERE id = :id LIMIT 1")
    LiveData<Baza> observeById(int id);

    @Query("SELECT * FROM baza WHERE id = :id LIMIT 1")
    Baza getById(int id);

    @Query("UPDATE baza SET title = :title, opis = :opis, kategorija = :kategorija, solved = :solved WHERE id = :id")
    int updateIdea(int id, String title, String opis, String kategorija, Boolean solved);


    @Query("SELECT solved AS status, COUNT(*) AS count FROM baza GROUP BY solved")
    LiveData<List<StatusCount>> countByStatus();

    @Query("SELECT kategorija AS kategorija, COUNT(*) AS count " +
            "FROM baza GROUP BY kategorija ORDER BY count DESC")
    LiveData<List<CategoryCount>> countByCategory();

    @Query("UPDATE baza SET solved = :solved WHERE id = :id")
    int updateSolved(int id, boolean solved);


    @Query("DELETE FROM baza WHERE id = :id")
    void deleteById(long id);

}
