// IdeaDetailActivity.java
package com.example.mojesuperideje.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import com.example.mojesuperideje.Baza;
import com.example.mojesuperideje.BazaDao;
import com.example.mojesuperideje.Databaza;
import com.example.mojesuperideje.R;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class IdejaDetaljiActivity extends AppCompatActivity {

    private Databaza db;
    private BazaDao dao;
    private int ideaId;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    boolean isSolved;


    private TextView tvTitle, tvOpis, tvCategory, tvStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ideja_detalji);

        tvTitle = findViewById(R.id.tvTitle);
        tvOpis = findViewById(R.id.tvOpis);
        tvCategory = findViewById(R.id.tvCategory);
        Button btnEdit = findViewById(R.id.btnEdit);
        Button btnSolve = findViewById(R.id.btnSolve);
        tvStatus = findViewById(R.id.tvStatus);
        Button btnDelete = findViewById(R.id.btnDelete);

        ideaId = getIntent().getIntExtra("idea_id", -1);
        if (ideaId == -1) { finish(); return; }

        db = Room.databaseBuilder(getApplicationContext(), Databaza.class, "moje_super_ideje.db")
                .enableMultiInstanceInvalidation()
                .build();
        dao = db.bazaDao();

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Details");
        }

        dao.observeById(ideaId).observe(this, idea -> {
            if (idea != null) {
                tvTitle.setText(idea.title);
                tvOpis.setText(idea.opis);            // or idea.naziv if that’s your field name
                tvCategory.setText(idea.kategorija);
                isSolved = idea.solved;
                tvStatus.setText(isSolved ? "Solved" : "Unsolved");
                btnSolve.setText(isSolved ? "Mark as Unsolved" : "Mark as Solved");


            }
        });



        btnEdit.setOnClickListener(v -> {
            Intent i = new Intent(this, DodajIdejuActivity.class);
            i.putExtra("edit_id", ideaId);
            startActivity(i);
        });

        btnSolve.setOnClickListener(v -> {
            io.execute(() -> {

                dao.updateSolved(ideaId, !isSolved);
                runOnUiThread(() ->
                        Toast.makeText(this, "Marked as Solved", Toast.LENGTH_SHORT).show()
                );

            });
        });

        btnDelete.setOnClickListener(v -> showDeleteDialog(ideaId));
    }

    private void showDeleteDialog(long id) {
        runOnUiThread(() -> {

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Izbriši ideju?")
                    .setMessage("Jeste li sigurni da želite obrisati ideju?")
                    .setNegativeButton("Odustani", null)
                    .setPositiveButton("Obriši", (dialog, which) -> deleteIdeaById(ideaId))
                    .show();
        });
    }

    private void deleteIdeaById(int ideaId) {
        java.util.concurrent.Executors.newSingleThreadExecutor().execute(() -> {
            dao.deleteById(ideaId);

            runOnUiThread(() -> {
                android.widget.Toast.makeText(this, "Idea deleted", android.widget.Toast.LENGTH_SHORT).show();
                setResult(RESULT_OK, new Intent().putExtra("deleted_id", ideaId));
                finish();
            });
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
