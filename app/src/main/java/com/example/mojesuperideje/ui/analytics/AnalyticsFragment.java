package com.example.mojesuperideje.ui.analytics;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import com.example.mojesuperideje.*;
import com.example.mojesuperideje.databinding.FragmentAnalyticsBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.*;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.example.mojesuperideje.BazaDao;

import java.util.ArrayList;
import java.util.List;

public class AnalyticsFragment extends Fragment {

    private FragmentAnalyticsBinding binding;
    private Databaza db;
    private BazaDao dao;

    private static final java.util.concurrent.Executor IO = java.util.concurrent.Executors.newSingleThreadExecutor();

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAnalyticsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }


    @Override public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db  = Databaza.getInstance(requireContext().getApplicationContext());
        dao = db.bazaDao();

        dao.observeAll().observe(getViewLifecycleOwner(), items -> {
        });

        dao.countByStatus().observe(getViewLifecycleOwner(), this::renderStatusPie);


        dao.countByCategory().observe(getViewLifecycleOwner(), this::renderCategoryBar);
    }

    private void renderStatusPie(List<StatusCount> rows) {
        PieChart chart = binding.pieSolved;

        int solved = 0, unsolved = 0, total = 0;
        if (rows != null) {
            for (StatusCount r : rows) {
                total += r.count;
                if (r.status)   solved   = r.count;
                else if (!r.status) unsolved = r.count;
            }
        }


        List<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(solved,   "Solved"));
        entries.add(new PieEntry(unsolved, "Unsolved"));

        if (entries.isEmpty()) {
            chart.clear();
            chart.setNoDataText("No ideas yet");
            chart.invalidate();
            return;
        }

        int green  = android.graphics.Color.parseColor("#2E7D32"); // Solved
        int orange = android.graphics.Color.parseColor("#FB8C00"); // Unsolved
        List<Integer> colors = new ArrayList<>();
        for (PieEntry e : entries) {
            colors.add("Solved".equals(e.getLabel()) ? green : orange);
        }


        PieDataSet ds = new PieDataSet(entries, "");
        ds.setColors(green, orange);
        ds.setDrawValues(false);
        //ds.setValueFormatter(new PercentFormatter(chart));
        //ds.setSliceSpace(2f);
        //ds.setValueTextSize(12f);
        //ds.setValueTextColor(android.graphics.Color.WHITE);
        //ds.setValueFormatter(new com.github.mikephil.charting.formatter.PercentFormatter(chart));

        chart.setData(new PieData(ds));
        //chart.setUsePercentValues(true);
        chart.setDrawEntryLabels(false);
        //chart.setEntryLabelColor(android.graphics.Color.WHITE);
        //chart.setEntryLabelTextSize(12f);
        chart.setCenterText("Total: " + total);
        chart.invalidate();
        chart.getDescription().setEnabled(false);

        // (Optional) Always show BOTH legend items even if one count is 0:
        // Legend legend = chart.getLegend();
        // List<LegendEntry> le = new ArrayList<>();
        // LegendEntry l1 = new LegendEntry(); l1.label="Solved";   l1.formColor=green;  l1.form=Legend.LegendForm.CIRCLE;
        // LegendEntry l2 = new LegendEntry(); l2.label="Unsolved"; l2.formColor=orange; l2.form=Legend.LegendForm.CIRCLE;
        // le.add(l1); le.add(l2);
        // legend.setCustom(le);
    }


    private void renderCategoryBar(List<CategoryCount> rows) {
        BarChart chart = binding.barByCategory;
        List<BarEntry> entries = new ArrayList<>();
        List<String> labels = new ArrayList<>();

        if (rows != null) {
            for (int i = 0; i < rows.size(); i++) {
                CategoryCount r = rows.get(i);
                entries.add(new BarEntry(i, r.count));
                labels.add(r.kategorija == null ? "Unknown" : r.kategorija);
            }
        }

        BarDataSet ds = new BarDataSet(entries, "Lalala");

        ds.setColors(ColorTemplate.COLORFUL_COLORS);
        ds.setValueTextSize(12f);

        ds.setValueFormatter(new ValueFormatter() {
            @Override public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        BarData data = new BarData(ds);
        data.setBarWidth(0.7f);

        chart.setData(data);
        XAxis x = chart.getXAxis();
        x.setPosition(XAxis.XAxisPosition.BOTTOM);
        x.setGranularity(1f);
        x.setGranularityEnabled(true);
        x.setDrawGridLines(false);
        x.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                int i = (int) value; // or Math.round(value)
                return (i >= 0 && i < labels.size()) ? labels.get(i) : "";
            }
        });
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(false);

        chart.getDescription().setEnabled(false);
        chart.setFitBars(true);
        chart.invalidate();
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
