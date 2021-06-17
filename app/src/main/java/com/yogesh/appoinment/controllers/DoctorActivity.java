package com.yogesh.appoinment.controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yogesh.appoinment.HelperUtils.HelperUtilities;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Appointment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class DoctorActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private SharedPreferences sharedPreferences;
    private ListView appointment_list;

    //custom dialog view
    private View dialogLayout;

    Dialog alert;
    private View header;
    private ImageView imgProfile;
    private AppointmentAdapter appointmentAdapter;

    //current date
    private int year;
    private int month;
    private int day;
    private DatePickerDialog datePickerDialog1;
    public String apointmentdate = "";
    Appointment appointment;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //navigation drawer manager
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header = navigationView.getHeaderView(0);


        imgProfile = (ImageView) header.findViewById(R.id.imgProfile);
        appointment_list = (ListView) findViewById(R.id.appointment_list);

        drawerProfileInfo();
        LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        email = LoginActivity.sharedPreferences.getString(LoginActivity.EMAIL, "");

        loadMyAppointments();
        appointment_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                appointment = (Appointment) parent.getItemAtPosition(position);
                apointmentdate = appointment.getAppointment_date();


                final AlertDialog.Builder builder = new AlertDialog.Builder(DoctorActivity.this);
                builder.setMessage("Do you want to modify appointment?")
                        .setCancelable(false)
                        .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                datePickerDialog().show();
                                alert.dismiss();

                            }
                        });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alert.dismiss();
                    }
                });

                alert = builder.create();
                alert.show();

            }
        });
        year = HelperUtilities.currentYear();
        month = HelperUtilities.currentMonth();
        day = HelperUtilities.currentDay();

    }


    public void logoutDialog() {
        Dialog dialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to leave? ")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        getApplicationContext().getSharedPreferences(LoginActivity.MY_PREFERENCES, 0).edit().clear().commit();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        dialog.dismiss();
                        finish();
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    //accepted successfully..
    public Dialog registrationSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Appointment updated successfully! ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getApplicationContext(), DoctorActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

        return builder.create();
    }


    public DatePickerDialog datePickerDialog() {
        if (datePickerDialog1 == null) {
            datePickerDialog1 = new DatePickerDialog(this, getOneWayDepartureDatePickerListener(), year, month, day);
        }
        datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        return datePickerDialog1;
    }

    public DatePickerDialog.OnDateSetListener getOneWayDepartureDatePickerListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int startYear, int startMonth, int startDay) {

                //get one way departure date here

                apointmentdate = HelperUtilities.formatDate(startYear, startMonth, startDay);

                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(DoctorActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
//                                            eReminderTime.setText( selectedHour + ":" + selectedMinute);
//                        appointment.setAppointment_date(apointmentdate);
//                        appointment.setAppointment_time(selectedHour + ":" + selectedMinute);
//                        appointment.setStatus("Accepted");
//                        DatabaseReference myRef = database.getReference().child("Appointment");
//                        String key = appointment.getId();
////                                            appointment.setId(key);
//                        myRef.child(key).setValue(appointment);
                        updateAppointment(apointmentdate, selectedHour + ":" + selectedMinute, "Accepted", appointment.getId());

                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();


            }
        };
    }

    private void updateAppointment(final String date, final String time, final String status, final String id) {
        final ProgressDialog dialog = new ProgressDialog(DoctorActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_update_my_appointments(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            if (json.has(ServerUtility.TAG_SUCCESS)) {
                                registrationSuccessDialog().show();
                            }
                        } catch (Exception e) {
                            Toast.makeText(DoctorActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DoctorActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("date", date);
                params.put("time", time);
                params.put("status", status);
                params.put("id", id);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(DoctorActivity.this);
        requestQueue.add(stringRequest);
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handles navigation view item on clicks
        int id = item.getItemId();

        if (id == R.id.nav_itinerary) {

        }
        if (id == R.id.nav_patient) {
            Intent intent = new Intent(getApplicationContext(), ViewPatientActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_appointment) {
            Intent intent = new Intent(getApplicationContext(), ShowDoctors.class);
            startActivity(intent);
        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_security) {
            Intent intent = new Intent(getApplicationContext(), SecurityActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {

//            getApplicationContext().getSharedPreferences(LoginActivity.MY_PREFERENCES, 0).edit().clear().commit();
//            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//            startActivity(intent);
//            finish();
            logoutDialog();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void drawerProfileInfo() {
        try {

            TextView profileName = (TextView) header.findViewById(R.id.profileName);
            TextView profileEmail = (TextView) header.findViewById(R.id.profileEmail);


            LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
            String name = LoginActivity.sharedPreferences.getString(LoginActivity.NAME, "");
            String email = LoginActivity.sharedPreferences.getString(LoginActivity.EMAIL, "");
            profileName.setText(name);
            profileEmail.setText(email);


        } catch (SQLiteException ex) {
            Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMyAppointments() {
        final ProgressDialog dialog = new ProgressDialog(DoctorActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_load_my_appointments(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            appointmentAdapter = new AppointmentAdapter(DoctorActivity.this, R.id.appointment_list);
                            JSONArray array = json.getJSONArray("AppointmentInfo");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                Appointment appointment = new Appointment();
                                appointment.setPatient_id(object.getString("patientEmail"));
                                appointment.setPatient_name(object.getString("patientName"));
                                appointment.setPatient_mobile(object.getString("patientMobile"));
                                appointment.setDoctor_email(object.getString("doctorEmail"));
                                appointment.setDoctor_name(object.getString("doctorName"));
                                appointment.setDoctor_mobile(object.getString("doctorMobile"));
                                appointment.setSymptoms(object.getString("symptoms"));
                                appointment.setStatus(object.getString("appointment_status"));
                                appointment.setId(object.getString("id"));
                                appointment.setAppointment_date(object.getString("appointmentDate"));
                                appointment.setAppointment_time(object.getString("appointmentTime"));

                                appointmentAdapter.add(appointment);
                            }
                            appointment_list.setAdapter(appointmentAdapter);

                        } catch (Exception e) {
                            Toast.makeText(DoctorActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DoctorActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email", email);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestQueue requestQueue = Volley.newRequestQueue(DoctorActivity.this);
        requestQueue.add(stringRequest);
    }
}
