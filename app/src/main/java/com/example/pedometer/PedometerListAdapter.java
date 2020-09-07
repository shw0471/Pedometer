package com.example.pedometer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PedometerListAdapter extends RecyclerView.Adapter<PedometerListAdapter.ViewHolder> {

    private List<PedometerDataModel> pedometerDataModelsList = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.item, parent, false);
        ViewHolder viewHolder = new PedometerListAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.item_tv_date.setText(String.valueOf(pedometerDataModelsList.get(position).getDate()));
        holder.item_tv_step.setText(String.valueOf(pedometerDataModelsList.get(position).getSteps()));
    }

    @Override
    public int getItemCount() {
        return pedometerDataModelsList.size();
    }

    public void setList(List<PedometerDataModel> list) {
        this.pedometerDataModelsList = list;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView item_tv_date;
        TextView item_tv_step;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            item_tv_date = itemView.findViewById(R.id.item_tv_date);
            item_tv_step = itemView.findViewById(R.id.item_tv_step);
        }
    }
}
