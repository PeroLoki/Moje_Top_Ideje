package com.example.mojesuperideje;


import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.doesNotExist;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.not;
import android.os.SystemClock;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;


import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class CreateSolveDelete {

    @Test
    public void add_solve_delete() {

        ActivityScenario.launch(MainActivity.class);


        onView(withId(R.id.fabAdd)).perform(click());

        String title = "E2E Title";
        String kategorija = "Produktivnost";
        String opis = "Ovo je opis za test.";


        onView(withId(R.id.etTitle)).perform(typeText(title), closeSoftKeyboard());
        onView(withId(R.id.etDescription)).perform(click());
        onView(withId(R.id.etDescription)).perform(typeText(opis));
        onView(withId(R.id.spinnerCategory)).perform(click());
        onView(withText(kategorija)).perform(click());

        onView(withId(R.id.btnSave)).perform(click());

        onView(withText(title)).perform(click());

        onView(withId(R.id.btnSolve)).perform(click());
        SystemClock.sleep(3000);
        onView(withId(R.id.btnSolve)).perform(click());

        onView(withId(R.id.btnDelete)).perform(click());


        onView(withText("Obriši")).perform(click());

        onView(withId(R.id.recycler))
                .check(matches(not(hasDescendant(withText(title)))));
    }

    @Test
    public void add_edit_check_medium() {
        ActivityScenario.launch(MainActivity.class);

        String original = "Title Prvi";
        String edit   = "Edit Title";

        onView(withId(R.id.fabAdd)).perform(click());
        onView(withId(R.id.etTitle)).perform(typeText(original), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());

        onView(withText(original)).perform(click());


        onView(withId(R.id.btnEdit)).perform(click());

        onView(withId(R.id.etTitle)).perform(replaceText(edit), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());

        androidx.test.espresso.Espresso.pressBackUnconditionally();

        onView(withId(R.id.recycler))
                .check(matches(hasDescendant(withText(edit))));



    }



}
