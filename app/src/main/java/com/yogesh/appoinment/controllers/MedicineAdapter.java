package com.yogesh.appoinment.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Medicine;

import java.util.ArrayList;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineHolder> {
    private Context context;
    private List<Medicine> list = new ArrayList<>();
    private static final int TYPE_ROW = 0;
    private static final int TYPE_ROW_COLORFUL = 1;

    public MedicineAdapter(Context context, List<Medicine> list) {
        this.context = context;
        this.list = list;
    }


    @NonNull
    @Override
    public MedicineHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ROW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_medicine_row, viewGroup, false);
            return new MedicineHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_medicine_row_colorful,
                    viewGroup, false);
            return new MedicineHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineHolder holder, int i) {
        final Medicine medicine = list.get(i);
        holder.txtId.setText(""+medicine.getId());
        holder.txtMedicineName.setText(medicine.getMedicine_name());
        holder.txtMorning.setText(""+medicine.getMorning());
        holder.txtAfternoon.setText(""+medicine.getAfternoon());
        holder.txtEvening.setText(""+medicine.getEvening());
        holder.txtDays.setText(""+medicine.getDays());
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (position % 2 == 0) {
            return TYPE_ROW_COLORFUL;
        }
        return TYPE_ROW;
    }

    public static class MedicineHolder extends RecyclerView.ViewHolder {
        TextView txtId, txtMedicineName, txtMorning, txtAfternoon, txtEvening, txtDays;

        public MedicineHolder(View view) {
            super(view);
            txtId = view.findViewById(R.id.txtSr);
            txtMedicineName = view.findViewById(R.id.txtMedicineName);
            txtMorning = view.findViewById(R.id.txtMorning);
            txtAfternoon = view.findViewById(R.id.txtAfternoon);
            txtEvening = view.findViewById(R.id.txtEvening);
            txtDays = view.findViewById(R.id.txtDays);
        }

    }

}
