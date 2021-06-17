package com.yogesh.appoinment.controllers;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Client;

public class ShowDoctors extends AppCompatActivity {

    ListView list_doctors;
    DoctorAdapter doctorAdapter;
    DatabaseReference database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_doctors);
        list_doctors = (ListView) findViewById(R.id.list_view);

        list_doctors.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Client client = (Client) adapterView.getItemAtPosition(i);

                Intent intent = new Intent(ShowDoctors.this, BookActivity.class);
                intent.putExtra("Name", client.getFirstName() + " " + client.getLastName());
                intent.putExtra("Email", client.getClientID());
                intent.putExtra("Mobile", client.getPhone());
                startActivity(intent);
                finish();
            }
        });
        database = FirebaseDatabase.getInstance().getReference("Doctor");

    }


    @Override
    protected void onStart() {
        super.onStart();
        database.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                doctorAdapter = new DoctorAdapter(ShowDoctors.this, R.layout.single_doctor_view);
                for (DataSnapshot doctor : dataSnapshot.getChildren()) {
                    Client client = doctor.getValue(Client.class);
                    doctorAdapter.add(client);
                }
                list_doctors.setAdapter(doctorAdapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        Toast.makeText(this, "Select doctor for appointment", Toast.LENGTH_SHORT).show();
    }


}
