/*************************************************************************************************
 * JANUARY 8, 2018
 * Mentesnot Aboset
 * ************************************************************************************************/
package com.yogesh.appoinment.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Appointment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private double latitude = 0.0, longitude = 0.0;
    private SharedPreferences sharedPreferences;
    private ListView appointment_list;

    //custom dialog view
    private View dialogLayout;


    private View header;
    private ImageView imgProfile;
    private AppointmentAdapter appointmentAdapter;
    String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        } else if (id == R.id.nav_appointment) {
            bookMyAppointment();
        } else if (id == R.id.nav_test) {
            startActivity(new Intent(MainActivity.this,MyTestBooking.class));
        } else if (id == R.id.nav_chat) {
           startActivity(new Intent(MainActivity.this,SpeechActivity.class));
        } else if (id == R.id.nav_sos) {
            //get location and send location to register mobile number
            sendLocation();

        } else if (id == R.id.nav_profile) {
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_security) {
            Intent intent = new Intent(getApplicationContext(), SecurityActivity.class);
            startActivity(intent);

        }
        else if (id == R.id.nav_reports) {
            ReportsActivity.email =email;
            startActivity(new Intent(getApplicationContext(), ReportsActivity.class));
        }
        else if (id == R.id.nav_about) {
            Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout) {
            logoutDialog();

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendLocation() {
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            this.latitude = gpsTracker.getLatitude();
            this.longitude = gpsTracker.getLongitude();
        }
        //send this location
        if(this.latitude!=0.0) {
            String message = "Help required at location http://maps.google.com/?q=" + this.latitude + "," + this.longitude;
            SmsManager manager = SmsManager.getDefault();
            manager.sendTextMessage("9503986854", null, message, null, null);

        }

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

    private void bookMyAppointment() {
        //add new book alert
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(MainActivity.this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.search_doctor, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
//        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));


        Button btnSearch = (Button) dialogView.findViewById(R.id.btnAddBook);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        final EditText etISN = (EditText) dialogView.findViewById(R.id.txtQuery);


        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //issue book information

                if (etISN.getText().toString().equals("")) {
                    Toast.makeText(MainActivity.this, "Please enter query", Toast.LENGTH_SHORT).show();
                    etISN.requestFocus();
                    return;
                }

                MapsActivity.SearchQuery = etISN.getText().toString();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                alertDialog.dismiss();
            }
        });
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }

    private void loadMyAppointments() {
        final ProgressDialog dialog = new ProgressDialog(MainActivity.this);
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
                            appointmentAdapter = new AppointmentAdapter(MainActivity.this, R.id.appointment_list);
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
                            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Error..", Toast.LENGTH_SHORT).show();
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
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(stringRequest);
    }

}
