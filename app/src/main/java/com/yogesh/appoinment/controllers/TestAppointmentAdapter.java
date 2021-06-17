package com.yogesh.appoinment.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.TestAppointment;

import java.util.ArrayList;
import java.util.List;

public class TestAppointmentAdapter extends ArrayAdapter {
    private Context context;
    private List<TestAppointment> list = new ArrayList<>();
    public TestAppointmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }
    public void add(@Nullable TestAppointment object) {
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }


    public TestAppointment getItem(int position) {
        return list.get(position);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        LabAppointmentHolder holder;
        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.single_lab_test, parent, false);

            holder = new LabAppointmentHolder();
            holder.txtDoctorName = (TextView) row.findViewById(R.id.txtDoctorName);
            holder.txtDoctorEmail = (TextView) row.findViewById(R.id.txtDoctorEmail);
            holder.txtDoctorMobile = (TextView) row.findViewById(R.id.txtDoctorMobile);
            holder.txtAppointmentDate = (TextView) row.findViewById(R.id.txtAppointmentDate);
            holder.txtTime = (TextView) row.findViewById(R.id.txtTime);
            holder.txtStatus = (TextView) row.findViewById(R.id.txtStatus);
            row.setTag(holder);

            LoginActivity.sharedPreferences = context.getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //click here for medical history
                }
            });

        } else {
            holder = (LabAppointmentHolder) row.getTag();
        }

        try {
            TestAppointment appointment = getItem(position);
            holder.txtTime.setText(appointment.getTime());
            holder.txtDoctorMobile.setText(appointment.getMobile());
            holder.txtDoctorEmail.setText(appointment.getEmail());
            holder.txtDoctorName.setText(appointment.getLab_name());
            holder.txtAppointmentDate.setText(appointment.getDate());
            holder.txtStatus.setText(appointment.getStatus());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return row;
    }

    public static class LabAppointmentHolder {
        TextView txtDoctorName, txtDoctorEmail, txtDoctorMobile, txtAppointmentDate, txtTime, txtStatus;

    }
}
