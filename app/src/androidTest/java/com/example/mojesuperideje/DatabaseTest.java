package com.example.mojesuperideje;
import android.content.Context;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;


public class DatabaseTest {
        private Databaza appDatabase;
        private BazaDao bazaDao;

        @Before
        public void createDb() {


            Context context = ApplicationProvider.getApplicationContext();

            appDatabase = Room.inMemoryDatabaseBuilder(context,
                    Databaza.class).build();
            bazaDao = appDatabase.bazaDao();
        }

        @After
        public void closeDB() {
            appDatabase.close();
        }

        @Test
        public void writeAndReadNote() throws Exception {
            Baza baza = new Baza();
            String TEXT = "sample note text, testing 1 2 3 ";
            baza.title = TEXT;

            bazaDao.insert(baza);

            List<Baza> notes = bazaDao.getAll();

            assertEquals(TEXT, notes.get(notes.size() - 1).title);
        }

}
