package com.yogesh.appoinment.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
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

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewPatientHistory extends AppCompatActivity {
    Context context;
    public static String email = "";
    LinearLayout mainLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_patient_history);
        context = this;
        mainLayout = findViewById(R.id.mainLayout);
        loadView();
    }

    private void loadView() {
        final TableLayout table = new TableLayout(this);
        table.setLayoutParams(new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        table.setShrinkAllColumns(true);
        table.setStretchAllColumns(true);
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_load_patient_history(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            ArrayList<String> list = new ArrayList<>();
                            JSONArray array = json.getJSONArray("HistoryInfo");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);


                                if (list.contains(object.getString("appointment_id"))) {
                                    TextView tv2 = new TextView(context);
                                    TextView tv3 = new TextView(context);
                                    TextView tv4 = new TextView(context);
                                    TextView tv5 = new TextView(context);
                                    TextView tv6 = new TextView(context);
                                    tv2.setText(object.getString("medicine_name"));
                                    tv3.setText(object.getString("morning"));
                                    tv4.setText(object.getString("afternoon"));
                                    tv5.setText(object.getString("evening"));
                                    tv6.setText(object.getString("days"));
                                    TableRow row2 = new TableRow(context);
                                    row2.addView(tv2);
                                    row2.addView(tv3);
                                    row2.addView(tv4);
                                    row2.addView(tv5);
                                    row2.addView(tv6);
                                    table.addView(row2);
                                } else {
                                    list.add(object.getString("appointment_id"));

                                    //add doctor name and appointment date
                                    TableRow row = new TableRow(context);
                                    TextView tv = new TextView(context);
                                    tv.setText("Doctor Name: ");
                                    tv.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tvv = new TextView(context);
                                    tvv.setText( object.getString("doctorName"));
                                    tvv.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv1 = new TextView(context);
                                    tv1.setText("Date: ");
                                    tv1.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv1v = new TextView(context);
                                    tv1v.setText( object.getString("appointmentDate"));
                                    tv1v.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv1vv = new TextView(context);
                                    tv1vv.setText("");
                                    row.addView(tv);
                                    row.addView(tvv);
                                    row.addView(tv1);
                                    row.addView(tv1v);
                                    row.addView(tv1vv);
                                    table.addView(row);

                                    TableRow row1 = new TableRow(context);
                                    TextView tv2 = new TextView(context);
                                    tv2.setText("Medicine Name");
                                    tv2.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv3 = new TextView(context);
                                    tv3.setText("Morning");
                                    tv3.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv4 = new TextView(context);
                                    tv4.setText("Afternoon");
                                    tv4.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv5 = new TextView(context);
                                    tv5.setText("Evening");
                                    tv5.setTypeface(Typeface.DEFAULT_BOLD);
                                    TextView tv6 = new TextView(context);
                                    tv6.setText("Days");
                                    tv6.setTypeface(Typeface.DEFAULT_BOLD);
                                    row1.addView(tv2);
                                    row1.addView(tv3);
                                    row1.addView(tv4);
                                    row1.addView(tv5);
                                    row1.addView(tv6);
                                    table.addView(row1);
                                    TextView tv7=new TextView(context);
                                    tv7.setText(object.getString("medicine_name"));
                                    TextView tv8=new TextView(context);
                                    tv8.setText(object.getString("morning"));
                                    TextView tv9=new TextView(context);
                                    tv9.setText(object.getString("afternoon"));
                                    TextView tv10=new TextView(context);
                                    tv10.setText(object.getString("evening"));
                                    TextView tv11=new TextView(context);
                                    tv11.setText(object.getString("days"));
                                    TableRow row2 = new TableRow(context);
                                    row2.addView(tv7);
                                    row2.addView(tv8);
                                    row2.addView(tv9);
                                    row2.addView(tv10);
                                    row2.addView(tv11);
                                    table.addView(row2);


                                }
                            }
                            mainLayout.addView(table);

                        } catch (Exception e) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Error..", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("email",email);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }
}