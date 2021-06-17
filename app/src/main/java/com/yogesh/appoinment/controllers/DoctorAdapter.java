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
import com.yogesh.appoinment.models.Client;

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends ArrayAdapter {

    List<Client> list = new ArrayList<>();
    Context context;

    public DoctorAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.context = context;
    }


    public void add(@Nullable Client object) {
        list.add(object);
    }


    @Override
    public int getCount() {
        return list.size();
    }


    public Client getItem(int position) {
        return list.get(position);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View row = convertView;
        DoctorHolder doctorHolder;

        if (row == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = layoutInflater.inflate(R.layout.single_doctor_view, parent, false);
            doctorHolder = new DoctorHolder();
            doctorHolder.txtEmail = (TextView) row.findViewById(R.id.txtEmail);
            doctorHolder.txtName = (TextView) row.findViewById(R.id.txtName);
            doctorHolder.txtMobile = (TextView) row.findViewById(R.id.txtMobile);
            row.setTag(doctorHolder);
        } else {
            doctorHolder=(DoctorHolder)row.getTag();
        }
        Client client=getItem(position);
        doctorHolder.txtName.setText(client.getFirstName()+" "+client.getLastName());
        doctorHolder.txtMobile.setText(client.getPhone());
        doctorHolder.txtEmail.setText(client.getClientID());

        return row;
    }

    public static class DoctorHolder {
        TextView txtName, txtMobile, txtEmail;
    }
}
