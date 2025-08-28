package com.example.mojesuperideje.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.mojesuperideje.Baza;
import com.example.mojesuperideje.BazaDao;
import com.example.mojesuperideje.Databaza;
import com.example.mojesuperideje.R;
import com.example.mojesuperideje.databinding.DodajIdejuBinding;
import com.example.mojesuperideje.databinding.FragmentHomeBinding;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DodajIdejuActivity extends AppCompatActivity {

    private DodajIdejuBinding binding;
    private Integer editId = null;
    private Databaza db;
    private BazaDao bazaDao;

    private final ExecutorService io = Executors.newSingleThreadExecutor();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dodaj_ideju);

        Databaza db = Room.databaseBuilder(
                getApplicationContext(),
                Databaza.class,
                "moje_super_ideje.db"
        )
                .enableMultiInstanceInvalidation()
                .build();
        bazaDao = db.bazaDao();

        binding = DodajIdejuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Dodaj ideju");
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.idea_categories, android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerCategory.setAdapter(adapter);

        binding.btnSave.setOnClickListener(v -> save());//{

        int id = getIntent().getIntExtra("edit_id", -1);
        if (id != -1) {
            editId = id;
            setTitle("Edit Idea");
            binding.btnSave.setText("Update");

            // load existing row and pre-fill
            Executors.newSingleThreadExecutor().execute(() -> {
                Baza existing = bazaDao.getById(editId);
                runOnUiThread(() -> {
                    if (existing != null) {
                        binding.etTitle.setText(existing.title);
                        binding.tilDescription.getEditText().setText(existing.opis);
                        setSpinnerToValue(binding.spinnerCategory, existing.kategorija);
                    }
                });
            });
        }

        binding.btnSave.setOnClickListener(v -> save());
    }

    private void setSpinnerToValue(android.widget.Spinner sp, String value) {
        android.widget.ArrayAdapter adapter = (android.widget.ArrayAdapter) sp.getAdapter();
        if (adapter == null) return;
        for (int i = 0; i < adapter.getCount(); i++) {
            String item = String.valueOf(adapter.getItem(i));
            if (value != null && value.equals(item)) {
                sp.setSelection(i);
                break;
            }
        }
    }





private void save() {
        String title = binding.etTitle.getText().toString().trim();
        String opis = binding.tilDescription.getEditText().getText().toString().trim();
        String kategorija = binding.spinnerCategory.getSelectedItem().toString().trim();
        Boolean solved = false;

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(kategorija)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        Baza item = new Baza();
        item.title = title;
        item.opis = opis;
        item.kategorija = kategorija;
        item.solved = false;
        if (editId != null) {
            item.id = editId; // required for @Update to know which row
            io.execute(() -> {
                bazaDao.updateIdea(editId,title,opis,kategorija,solved);
                runOnUiThread(() -> {
                    setResult(RESULT_OK);
                    finish();
                });
            });
        }else {
            io.execute(() -> {
                long id = bazaDao.insert(item);

                runOnUiThread(() -> {
                    Toast.makeText(this, "Saved (id=" + id + ")", Toast.LENGTH_SHORT).show();

                    Intent data = new Intent();
                    data.putExtra("saved_id", id);
                    setResult(RESULT_OK, data);
                    finish();

                });
            }
            );
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
