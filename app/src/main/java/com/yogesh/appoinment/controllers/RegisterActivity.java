/*************************************************************************************************
 * JANUARY 8, 2018
 * Mentesnot Aboset
 * ************************************************************************************************/
package com.yogesh.appoinment.controllers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.yogesh.appoinment.HelperUtils.HelperUtilities;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private double latitude = 0.0;
    private double longitude = 0.0;
    private int clientID;
    private EditText inputFirstName;
    private EditText inputLastName;
    private EditText inputEmail;
    private EditText inputPhone;
    private EditText inputConfirmPassword;
    private EditText inputPassword;
    private EditText inputSpecialization;
    private EditText inputAddress;
    private boolean isValid;
    private FirebaseAuth mAuth;
    private String TAG = "RegisterActivity";
    FirebaseDatabase database;
    private RadioGroup rdUser;
    private long max_id = 0;
    TextInputLayout txtSpecialization,textAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        txtSpecialization = (TextInputLayout) findViewById(R.id.textSpecialization);
        textAddress=findViewById(R.id.textAddress);
        Button register = (Button) findViewById(R.id.btnRegister);
        TextView linkLogin = (TextView) findViewById(R.id.linkLogin);

        rdUser = (RadioGroup) findViewById(R.id.userType);
        rdUser.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (rdUser.getCheckedRadioButtonId() == R.id.rdDoctor) {
                    txtSpecialization.setVisibility(View.VISIBLE);
                    inputAddress.setHint("Enter hospital name");
                    textAddress.setHint("Enter hospital name");
                } else {
                    txtSpecialization.setVisibility(View.GONE);
                    inputAddress.setHint("Enter Address");
                    textAddress.setHint("Enter Address");
                }
            }
        });

        inputAddress=findViewById(R.id.txtAddress);
        inputSpecialization = (EditText) findViewById(R.id.txtSpecialization);
        inputFirstName = (EditText) findViewById(R.id.txtFirstName);
        inputLastName = (EditText) findViewById(R.id.txtLastName);
        inputEmail = (EditText) findViewById(R.id.txtEmail);
        inputPhone = (EditText) findViewById(R.id.txtPhone);
        inputPassword = (EditText) findViewById(R.id.txtPassword);
        inputConfirmPassword = (EditText) findViewById(R.id.txtConfirmPassword);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                registerEmployee();
            }
        });

        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);

            }
        });

        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            this.latitude = gpsTracker.getLatitude();
            this.longitude = gpsTracker.getLongitude();
        }
    }

    //registers new employee
    public void registerEmployee() {


        isValid = isValidUserInput();


        if (rdUser.getCheckedRadioButtonId() == R.id.rdDoctor) {

            if (inputSpecialization.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter specialization", Toast.LENGTH_SHORT).show();
                return;
            }
            if (inputAddress.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter hospital name", Toast.LENGTH_SHORT).show();
                return;
            }

        }else
        {
            if (inputAddress.getText().toString().equals("")) {
                Toast.makeText(RegisterActivity.this, "Please enter address", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        if (isValid) {

            final String userType;
            if (rdUser.getCheckedRadioButtonId() == R.id.rdDoctor) {
                userType = "Doctor";
            } else {
                userType = "Patient";
            }

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_register(),
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject json = new JSONObject(response);
                                if (json.has(ServerUtility.TAG_SUCCESS)) {
                                    registrationSuccessDialog().show();
                                } else {
                                    accountExistsAlert().show();
                                }
                            } catch (Exception e) {
                                Toast.makeText(RegisterActivity.this, "Registration failed..", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }) {

                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("email", inputEmail.getText().toString());
                    params.put("password",inputPassword.getText().toString());
                    params.put("fname", inputFirstName.getText().toString());
                    params.put("lname", inputLastName.getText().toString());
                    params.put("mobile", inputPhone.getText().toString());
                    params.put("specialization", inputSpecialization.getText().toString());
                    params.put("latitude", "" + latitude);
                    params.put("longitude", "" + longitude);
                    params.put("usertype", userType);
                    params.put("hospital_name", inputAddress.getText().toString());

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
            RequestQueue requestQueue = Volley.newRequestQueue(RegisterActivity.this);
            requestQueue.add(stringRequest);
        }
    }


    public Dialog registrationSuccessDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your profile created successfully! ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });

        return builder.create();
    }

    public Dialog accountExistsAlert() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("An account with this email already exists. Please try again. ")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        return builder.create();
    }

    //validates user input
    public boolean isValidUserInput() {
        if (HelperUtilities.isEmptyOrNull(inputFirstName.getText().toString())) {
            inputFirstName.setError("Please enter your first name");
            return false;
        } else if (!HelperUtilities.isString(inputFirstName.getText().toString())) {
            inputFirstName.setError("Please enter a valid first name");
            return false;
        }

        if (HelperUtilities.isEmptyOrNull(inputLastName.getText().toString())) {
            inputLastName.setError("Please enter your last name");
            return false;
        } else if (!HelperUtilities.isString(inputLastName.getText().toString())) {
            inputLastName.setError("Please enter a valid last name");
            return false;
        }

        if (HelperUtilities.isEmptyOrNull(inputEmail.getText().toString())) {
            inputEmail.setError("Please enter your email");
            return false;
        } else if (!HelperUtilities.isValidEmail(inputEmail.getText().toString())) {
            inputEmail.setError("Please enter a valid email");
            return false;
        }

        if (HelperUtilities.isEmptyOrNull(inputPhone.getText().toString())) {
            inputPhone.setError("Please enter your phone");
            return false;
        } else if (!HelperUtilities.isValidPhone(inputPhone.getText().toString())) {
            inputPhone.setError("Please enter a valid phone");
            return false;
        }


        if (HelperUtilities.isEmptyOrNull(inputPassword.getText().toString())) {
            inputPassword.setError("Please enter your password");
            return false;
        } else if (HelperUtilities.isShortPassword(inputPassword.getText().toString())) {
            inputPassword.setError("Your password must have at least 6 characters.");
            return false;
        }

        if (HelperUtilities.isEmptyOrNull(inputConfirmPassword.getText().toString())) {
            inputConfirmPassword.setError("Please confirm password");
            return false;
        }

        if (!(inputConfirmPassword.getText().toString().equals(inputPassword.getText().toString()))) {

            inputConfirmPassword.setError("The password doesn't match.");
            return false;
        }


        return true;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
