package com.yogesh.appoinment.controllers;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.yogesh.appoinment.HelperUtils.HelperUtilities;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Appointment;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class BookTestActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;



    private int currentTab;

    //date picker dialog
    private DatePickerDialog datePickerDialog1;

    //current date
    private int year;
    private int month;
    private int day;

    //id of date picker controls
    private final int ONE_WAY_DEPARTURE_DATE_PICKER = R.id.btnOneWayDepartureDatePicker;


    //traveller count
    private int oneWayTravellerCount = 1;
    private int roundTravellerCount = 1;

    //traveller count view
    private TextView numTraveller;

    //add and remove image button controls in the dialog
    private ImageButton imgBtnAdd;
    private ImageButton imgBtnRemove;

    //custom dialog view
    private View dialogLayout;

    //one way UI controls

    private AutoCompleteTextView txtOneWayTo;
    private Button btnOneWayDepartureDatePicker;


    //search button
    private Button btnSearch;

    private TextView txtName, txtEmail, txtMobile;

    private int tempOneWaySelectedClassID = 0;
    private int tempRoundSelectedClassID = 0;
    private String oneWayDepartureDate, roundDepartureDate, roundReturnDate;
    private View header;
    private ImageView imgProfile;
    private int clientID;
    private int tempYear;
    private int tempMonth;
    private int tempDay;

    private boolean isValidOneWayDate = true;
    private boolean isValidRoundDate = true;
    private boolean flag = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_test);

//        database = FirebaseDatabase.getInstance();
        txtName = (TextView) findViewById(R.id.txtName);
        txtEmail = (TextView) findViewById(R.id.txtEmail);
        txtMobile = (TextView) findViewById(R.id.txtMobile);


        Intent intent = getIntent();
        try {
            txtMobile.setText(intent.getStringExtra("Mobile"));
            txtEmail.setText(intent.getStringExtra("Email"));
            txtName.setText(intent.getStringExtra("Name"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        clientID = clientID();

        //tab host manager


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, HelperUtilities.SYMPTOMS);




        btnOneWayDepartureDatePicker = (Button) findViewById(R.id.btnOneWayDepartureDatePicker);


        btnSearch = (Button) findViewById(R.id.btnSearch);


        year = HelperUtilities.currentYear();
        month = HelperUtilities.currentMonth();
        day = HelperUtilities.currentDay();

//        drawerProfileInfo();
//        loadImage(clientID);


        //one way departure date picker on click listener
        btnOneWayDepartureDatePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                datePickerDialog(ONE_WAY_DEPARTURE_DATE_PICKER).show();

            }
        });


        //one way number of travellers on click listener


        //searches available flights on click
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                //book appointment
                if (isValid()) {

                    final String appointment_date = btnOneWayDepartureDatePicker.getText().toString();


                    LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
                    final String patient_name = LoginActivity.sharedPreferences.getString(LoginActivity.NAME, "");
                    final String patient_email = LoginActivity.sharedPreferences.getString(LoginActivity.EMAIL, "");
                    final String patient_mobile = LoginActivity.sharedPreferences.getString(LoginActivity.MOBILE, "");

                    final Appointment appointment = new Appointment();

                    final ProgressDialog dialog = new ProgressDialog(BookTestActivity.this);
                    dialog.setCancelable(false);
                    dialog.setMessage("Please wait...");
                    dialog.setIndeterminate(false);
                    dialog.show();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_book_test_appointment(),
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    try {
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                        JSONObject json = new JSONObject(response);
                                        if (json.has(ServerUtility.TAG_SUCCESS)) {

                                            registrationSuccessDialog(json.getString("message")).show();
                                        }
                                    } catch (Exception e) {
                                        Toast.makeText(BookTestActivity.this, "Error", Toast.LENGTH_SHORT).show();
                                        if (dialog.isShowing())
                                            dialog.dismiss();
                                    }
                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }) {

                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("patientId", patient_email);
                            params.put("labId", txtEmail.getText().toString());
                            params.put("appointmentDate", appointment_date);
                            params.put("time", "00:00");
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
                    RequestQueue requestQueue = Volley.newRequestQueue(BookTestActivity.this);
                    requestQueue.add(stringRequest);

                }

            }
        });
    }

    public Dialog registrationSuccessDialog(String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent intent = new Intent(getApplicationContext(), MyTestBooking.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                });

        return builder.create();
    }

    private boolean isValid() {

        if (btnOneWayDepartureDatePicker.getText().toString().equalsIgnoreCase("departure date")) {
            datePickerOneAlert().show();
            return false;
        }
        return true;
    }


    public Dialog datePickerOneAlert() {
        return new AlertDialog.Builder(this)
                .setMessage("Please select a appointment date.")
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                }).create();
    }




    public DatePickerDialog datePickerDialog(int datePickerId) {

        switch (datePickerId) {
            case ONE_WAY_DEPARTURE_DATE_PICKER:

                if (datePickerDialog1 == null) {
                    datePickerDialog1 = new DatePickerDialog(this, getOneWayDepartureDatePickerListener(), year, month, day);
                }
                datePickerDialog1.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                return datePickerDialog1;

        }
        return null;
    }

    public DatePickerDialog.OnDateSetListener getOneWayDepartureDatePickerListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int startYear, int startMonth, int startDay) {

                //get one way departure date here

                oneWayDepartureDate = startYear + "-" + (startMonth + 1) + "-" + startDay;
                btnOneWayDepartureDatePicker.setText(HelperUtilities.formatDate(startYear, startMonth, startDay));

            }
        };
    }

    public int clientID() {
        LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        clientID = LoginActivity.sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0);
        return clientID;
    }
}