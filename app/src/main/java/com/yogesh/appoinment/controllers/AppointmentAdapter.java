package com.yogesh.appoinment.controllers;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Appointment;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends ArrayAdapter {
    private Context context;
    private List<Appointment> list = new ArrayList<>();

    public AppointmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }


    public void add(@Nullable Appointment object) {
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    public Appointment getItem(int position) {
        return list.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        final AppointmentHolder holder;
        String userType = "";
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.single_appoinment, parent, false);

            holder = new AppointmentHolder();
            holder.txtPatientName = (TextView) row.findViewById(R.id.txtPatientName);
            holder.txtPatientEmail = (TextView) row.findViewById(R.id.txtPatientEmail);
            holder.txtPatientMobile = (TextView) row.findViewById(R.id.txtPatientMobile);
            holder.txtDoctorName = (TextView) row.findViewById(R.id.txtDoctorName);
            holder.txtDoctorEmail = (TextView) row.findViewById(R.id.txtDoctorEmail);
            holder.txtDoctorMobile = (TextView) row.findViewById(R.id.txtDoctorMobile);
            holder.txtAppointmentDate = (TextView) row.findViewById(R.id.txtAppointmentDate);
            holder.txtTime = (TextView) row.findViewById(R.id.txtTime);
            holder.txtStatus = (TextView) row.findViewById(R.id.txtStatus);
            holder.txtSymptoms = (TextView) row.findViewById(R.id.txtSymptoms);
            holder.linearLayout = (LinearLayout) row.findViewById(R.id.patient_linear);
            holder.linearLayout1 = (LinearLayout) row.findViewById(R.id.doctor_linear);
            holder.txtMedicine = (TextView) row.findViewById(R.id.txtMedicine);
            row.setTag(holder);
            LoginActivity.sharedPreferences = context.getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
//            userType = LoginActivity.sharedPreferences.getString(LoginActivity.USERTYPE, "");

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //click here for medical history
//                    Toast.makeText(context, "Clicked", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            holder = (AppointmentHolder) row.getTag();
        }

        try {
            final Appointment appointment = getItem(position);
            holder.txtTime.setText(appointment.getAppointment_time());
            holder.txtPatientEmail.setText(appointment.getPatient_id());
            holder.txtPatientMobile.setText(appointment.getPatient_mobile());
            holder.txtPatientName.setText(appointment.getPatient_name());
            holder.txtDoctorMobile.setText(appointment.getDoctor_mobile());
            holder.txtDoctorEmail.setText(appointment.getDoctor_email());
            holder.txtDoctorName.setText(appointment.getDoctor_name());
            holder.txtAppointmentDate.setText(appointment.getAppointment_date());
            holder.txtStatus.setText(appointment.getStatus());
            holder.txtSymptoms.setText(appointment.getSymptoms());

            holder.linearLayout.setVisibility(View.GONE);
            holder.linearLayout1.setVisibility(View.GONE);

            if (ServerUtility.USERTYPE.equalsIgnoreCase("doctor")) {
                holder.linearLayout.setVisibility(View.VISIBLE);
                holder.linearLayout1.setVisibility(View.GONE);
            } else {
                holder.linearLayout.setVisibility(View.GONE);
                holder.linearLayout1.setVisibility(View.VISIBLE);
            }

            final String finalUserType = userType;
            holder.txtMedicine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ViewMedicineActivity.id=appointment.getId();
                    context.startActivity(new Intent(context, ViewMedicineActivity.class));
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    public static class AppointmentHolder {
        TextView txtPatientName, txtPatientEmail, txtPatientMobile, txtDoctorName, txtDoctorEmail, txtDoctorMobile, txtAppointmentDate, txtTime, txtStatus, txtSymptoms, txtMedicine;
        LinearLayout linearLayout1, linearLayout;

    }


}
