package com.example.mojesuperideje.ui.home;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mojesuperideje.Baza;
import com.example.mojesuperideje.R;

import java.util.ArrayList;
import java.util.List;

public class IdeaAdapter extends RecyclerView.Adapter<IdeaAdapter.VH> {

    public enum ViewMode { CARD, LIST }



    public interface OnItemClick { void onClick(Baza item); }

    private OnItemClick onItemClick;

    public void setOnItemClick(OnItemClick l) { this.onItemClick = l; }
    private List<Baza> data = new ArrayList<Baza>();
    private ViewMode mode = ViewMode.CARD;

    public ViewMode getViewMode() { return mode; }

    public void submit(List<Baza> items) {
        data.clear();
        if (items != null) data.addAll(items);
        notifyDataSetChanged();
    }
    public void setViewMode(ViewMode m) { mode = m; notifyDataSetChanged(); }

    @Override public int getItemViewType(int position) {
        return (mode == ViewMode.CARD) ? 1 : 2;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_home_card : R.layout.item_home_list;
        View v = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new VH(v);
    }

    @SuppressLint("ClickableViewAccessibility")
    public void onBindViewHolder(@NonNull VH holder, int pos) {
        Baza b = data.get(pos);
        holder.title.setText(b.title);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClick != null) onItemClick.onClick(b);
        });

        holder.itemView.setOnTouchListener((v, e) -> {
            switch (e.getActionMasked()) {

                case android.view.MotionEvent.ACTION_DOWN:
                    v.animate().scaleX(0.95f).scaleY(0.95f).setDuration(80).start();
                    break;

                case android.view.MotionEvent.ACTION_UP:

                case android.view.MotionEvent.ACTION_CANCEL:
                    v.animate().scaleX(1f).scaleY(1f).setDuration(120).start();
                    break;
            }
            return false;
        });
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView title;
        VH(View itemView) { super(itemView); title = itemView.findViewById(R.id.tvTitle); }
    }
}

