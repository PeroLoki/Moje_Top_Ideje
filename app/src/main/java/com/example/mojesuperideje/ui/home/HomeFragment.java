package com.example.mojesuperideje.ui.home;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MenuHost;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.mojesuperideje.BazaDao;
import com.example.mojesuperideje.Databaza;
import com.example.mojesuperideje.R;

import com.example.mojesuperideje.databinding.FragmentHomeBinding;
import com.example.mojesuperideje.ui.IdejaDetaljiActivity;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private RecyclerView recyclerView;
    private IdeaAdapter adapter;
    private SharedPreferences prefs;

    private MenuItem settingsItem;
    private static final String PREF_VIEW_MODE = "view_mode";
    private Databaza db;

    private BazaDao dao;

    private static final String PREFS = "view_prefs";
    private static final String KEY_MODE = "view_mode";


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


        return root;
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        db = Room.databaseBuilder(
                        requireContext().getApplicationContext(),
                        Databaza.class,
                        "moje_super_ideje.db"
                )
                .enableMultiInstanceInvalidation()
                .build();
        dao = db.bazaDao();


        dao.observeAll().observe(getViewLifecycleOwner(), items -> {
            adapter.submit(items);

        });

        adapter.setOnItemClick(item -> {
            Intent it = new Intent(requireContext(), IdejaDetaljiActivity.class);
            it.putExtra("idea_id", item.id);
            startActivity(it);
        });

        binding.recycler.setAdapter(adapter);


        setupMenu();
    }



    private void setupMenu() {

        MenuHost menuHost = requireActivity();


        menuHost.addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                Log.d(TAG, "onCreateMenu se uaplio");
                menuInflater.inflate(R.menu.menu_home, menu);
                settingsItem = menu.findItem(R.id.action_settings);

                updateSettingsIcon();

            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {

                if (menuItem.getItemId() == R.id.action_settings) {
                    Log.d(TAG, "onMenuItemSelected se uaplio");
                    setViewMode();
                    updateSettingsIcon();

                    return true;
                }
                return false;
            }
        }, getViewLifecycleOwner(), Lifecycle.State.RESUMED);
    }

    private void updateAdatpter() {

    }

    private void applyLayoutManager(IdeaAdapter.ViewMode mode) {
        Log.d(TAG, "applyLayoutManager se uaplio");
        Log.d(TAG, "applyLayoutManager Initial mode = " + adapter.getViewMode());
        Log.d(TAG, "applyLayoutManager sada je mode = " + mode);

        if (mode == IdeaAdapter.ViewMode.CARD) {
            Log.d(TAG, "sada je mode true");
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 1));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            Log.d(TAG, "sada je mode else");
        }
    }

    private IdeaAdapter.ViewMode loadMode() {
        Log.d(TAG, " loadMode se upalio i vraca mode = " + adapter.getViewMode());

        String raw = prefs.getString(KEY_MODE, "CARD");
        Log.d(TAG, "loadMode pref je sad sada je mode = " + raw);
        return "LIST".equals(raw) ? IdeaAdapter.ViewMode.LIST : IdeaAdapter.ViewMode.CARD;
    }

    private void updateSettingsIcon() {
        Log.d(TAG, "updateSettingsIcon se uaplio");
        if (settingsItem == null) return;
        Log.d(TAG, "updateSettingsIcon se uaplio");
        Log.d(TAG, "updateSettingsIcon Initial mode = " + adapter.getViewMode());
        Log.d(TAG, "updateSettingsIcon sada je mode = " + IdeaAdapter.ViewMode.LIST);
        boolean isList = adapter.getViewMode() == IdeaAdapter.ViewMode.LIST;
        Log.d(TAG, "bool = " + isList);
        adapter.setViewMode(isList ? IdeaAdapter.ViewMode.LIST : IdeaAdapter.ViewMode.CARD);
        settingsItem.setIcon(isList ? R.drawable.outline_view_list_24 : R.drawable.outline_view_card_24);
        settingsItem.setTitle(isList ? R.string.card_view_title : R.string.list_view_title);
        Log.d(TAG, "updateSettingsIcon sada je mode = " + settingsItem.getTitle());
        requireActivity().invalidateOptionsMenu();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;

    }
}
