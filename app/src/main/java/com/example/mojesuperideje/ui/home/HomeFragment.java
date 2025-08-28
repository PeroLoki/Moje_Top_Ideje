package com.example.mojesuperideje.ui.home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mojesuperideje.BazaDao;
import com.example.mojesuperideje.Databaza;
import com.example.mojesuperideje.R;
import com.example.mojesuperideje.databinding.FragmentAnalyticsBinding;
import com.example.mojesuperideje.databinding.FragmentHomeBinding;
import com.example.mojesuperideje.ui.IdejaDetaljiActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private IdeaAdapter adapter;

    private Databaza db;

    private BazaDao dao;


    private SharedPreferences prefs;
    private static final String PREFS = "view_prefs";
    private static final String KEY_MODE = "view_mode"; // "CARD" or "LIST"


    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {


        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = binding.recycler;
        adapter = new IdeaAdapter();

        prefs = requireContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        IdeaAdapter.ViewMode mode = loadMode();

        recyclerView.setAdapter(adapter);
        applyLayoutManager(mode);
        adapter.setViewMode(mode);


        binding.fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), com.example.mojesuperideje.ui.DodajIdejuActivity.class);
            startActivity(intent);
        });

        setHasOptionsMenu(true);
        return root;
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Minimal DB instance (no singleton)
        db = Room.databaseBuilder(
                requireContext().getApplicationContext(),
                Databaza.class,
                "moje_super_ideje.db"
        )
                .enableMultiInstanceInvalidation()
                .build();
        dao = db.bazaDao();

        // Observe table and update UI automatically
        dao.observeAll().observe(getViewLifecycleOwner(), items -> {
            adapter.submit(items);  // make sure IdeaAdapter has submit(List<Baza>) method
            // If you have an empty-state TextView in the layout:
            // binding.empty.setVisibility(items == null || items.isEmpty() ? View.VISIBLE : View.GONE);
        });

        adapter.setOnItemClick(item -> {
            Intent it = new Intent(requireContext(), IdejaDetaljiActivity.class);
            it.putExtra("idea_id", item.id);
            startActivity(it);
        });
    }


    private void applyLayoutManager(IdeaAdapter.ViewMode mode) {
        if (mode == IdeaAdapter.ViewMode.CARD) {
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        }
    }

    private IdeaAdapter.ViewMode loadMode() {
        String raw = prefs.getString(KEY_MODE, "CARD");
        return "LIST".equals(raw) ? IdeaAdapter.ViewMode.LIST : IdeaAdapter.ViewMode.CARD;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}