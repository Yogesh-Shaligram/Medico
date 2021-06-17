/*************************************************************************************************
 * JANUARY 8, 2018
 * Mentesnot Aboset
 *  ************************************************************************************************/
package com.yogesh.appoinment.controllers;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import com.yogesh.appoinment.HelperUtils.HelperUtilities;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private String TAG = "LoginActivity";
    public static final String MY_PREFERENCES = "MY_PREFS";
    public static final String EMAIL = "EMAIL";
    public static final String NAME = "NAME";
    public static final String MOBILE = "MOBILE";
    public static final String PASSWORD = "PASSWORD";
    public static final String USERTYPE = "USER";
    public static final String CLIENT_ID = "CLIENT_ID";
    public static final String LOGIN_STATUS = "LOGGED_IN";
    public static SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private EditText inputEmail, etServerIP;
    private EditText inputPassword;
    private TextView txtLoginError;
    private RadioGroup rdUser;
    private boolean isValid;

    private int accountID;
    private int clientID;
    private String usertype = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        checkAndRequestPermissions();
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences(MY_PREFERENCES, 0);
        Boolean loggedIn = sharedPreferences.getBoolean(LOGIN_STATUS, false);//login status

        //checks the login status and redirects to the main activity
        if (loggedIn) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        Button btnLogin = (Button) findViewById(R.id.btnLogin);
        TextView linkRegister = (TextView) findViewById(R.id.linkRegister);
        etServerIP = findViewById(R.id.etServerIP);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        txtLoginError = (TextView) findViewById(R.id.txtLoginError);
        rdUser = (RadioGroup) findViewById(R.id.userType);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });
        linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (etServerIP.getText().toString().equals("")) {
                    txtLoginError.setText("Enter Server IP address");
                    return;
                }
                ServerUtility.Server_URL="http://"+etServerIP.getText().toString()+"/EHRServer/";
                Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    //process login
    public void attemptLogin() {
        try {

            isValid = isValidUserInput();
            //filters the user input
            final String filteredEmail = HelperUtilities.filter(inputEmail.getText().toString());
            final String filteredPassword = HelperUtilities.filter(inputPassword.getText().toString());

            if (rdUser.getCheckedRadioButtonId() == R.id.rdDoctor) {
                usertype = "Doctor";
            } else {
                usertype = "Patient";
            }
            if (isValid) {
                ServerUtility.Server_URL="http://"+etServerIP.getText().toString()+"/EHRServer/";
                final ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
                dialog.setCancelable(false);
                dialog.setMessage("Please wait...");
                dialog.setIndeterminate(false);
                dialog.show();
                sharedPreferences = getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                editor = sharedPreferences.edit();
                StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_login(), new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            if (json.has(ServerUtility.TAG_SUCCESS)) {
                                Toast.makeText(LoginActivity.this, "Login Success..", Toast.LENGTH_SHORT).show();
                                editor.putString(USERTYPE, usertype);
                                editor.putString(NAME, json.getString("name"));
                                editor.putString(EMAIL, filteredEmail);
                                editor.putString(MOBILE, json.getString("mobile"));
                                editor.putString(PASSWORD, filteredPassword);
                                editor.commit();
                                if (usertype.equalsIgnoreCase("doctor")) {
                                    ServerUtility.USERTYPE="doctor";
                                    startActivity(new Intent(LoginActivity.this, DoctorActivity.class));
                                    finish();
                                } else {
                                    ServerUtility.USERTYPE="patient";
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (dialog.isShowing())
                            dialog.dismiss();

                        Toast.makeText(LoginActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                    }
                }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("usertype", usertype);
                        params.put("username", filteredEmail);
                        params.put("password", filteredPassword);
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
                RequestQueue requestQueue = Volley.newRequestQueue(LoginActivity.this);
                requestQueue.add(stringRequest);
            }

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT).show();
        }
    }

    //validate user input
    public boolean isValidUserInput() {
        if (HelperUtilities.isEmptyOrNull(inputEmail.getText().toString())) {
            txtLoginError.setText("Enter email");
            return false;
        }
        if (HelperUtilities.isEmptyOrNull(inputPassword.getText().toString())) {
            txtLoginError.setText("Enter password");
            return false;
        }
        if (etServerIP.getText().toString().equals("")) {
            txtLoginError.setText("Enter Server IP");
            return false;
        }
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();


    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
    }

    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;

    private boolean checkAndRequestPermissions() {
        int loc = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION);
        int loc2 = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        int sms = ContextCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (loc2 != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (loc != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (sms != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
