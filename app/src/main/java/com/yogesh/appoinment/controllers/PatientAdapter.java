package com.yogesh.appoinment.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.PatientInfo;

import java.util.ArrayList;
import java.util.List;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private Context context;
    private List<PatientInfo> list = new ArrayList<>();
    private static final int TYPE_ROW = 0;
    private static final int TYPE_ROW_COLORFUL = 1;

    public PatientAdapter(Context context, List<PatientInfo> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_ROW) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_patient_row, viewGroup,
                    false);
            return new PatientViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.single_patient_row_colorful,
                    viewGroup, false);
            return new PatientViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int i) {
        final PatientInfo info = list.get(i);
        holder.txtSr.setText(""+(i+1));
        holder.txtPatientName.setText(info.getPatientName());
        holder.txtPatientEmail.setText(info.getEmail());
        holder.txtPatientMobile.setText(info.getMobile());
        holder.txtAddress.setText(info.getAddress());
        holder.txtReports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open reports activity
                ReportsActivity.email = info.getEmail();
                context.startActivity(new Intent(context, ReportsActivity.class));
            }
        });
        holder.txtHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open history activity
                ViewPatientHistory.email = info.getEmail();
                context.startActivity(new Intent(context, ViewPatientHistory.class));
            }
        });
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

    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView txtPatientName, txtPatientMobile, txtPatientEmail, txtAddress, txtHistory, txtReports,txtSr;

        public PatientViewHolder(@NonNull View view) {
            super(view);
            txtSr=view.findViewById(R.id.txtSr);
            txtPatientName = view.findViewById(R.id.txtPatientName);
            txtPatientMobile = view.findViewById(R.id.txtPatientMobile);
            txtPatientEmail = view.findViewById(R.id.txtPatientEmail);
            txtAddress = view.findViewById(R.id.txtAddress);
            txtHistory = view.findViewById(R.id.txtHistory);
            txtReports = view.findViewById(R.id.txtReports);
        }
    }

}

