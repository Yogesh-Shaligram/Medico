package com.yogesh.appoinment.controllers;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.yogesh.appoinment.HelperUtils.FixedGridLayoutManager;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Medicine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewMedicineActivity extends AppCompatActivity {
    MedicineAdapter adapter;
    List<Medicine> list;
    public static String id;
    Context context;
    RecyclerView rvMedicine;
    int scrollX = 0;
    HorizontalScrollView headerScroll;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_medicine);
        context = this;
        headerScroll = findViewById(R.id.headerScroll);
        rvMedicine = (RecyclerView) findViewById(R.id.rvMedicine);
        loadMyMedicine();
        rvMedicine.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                scrollX += dx;

                headerScroll.scrollTo(scrollX, 0);
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
        final FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//add medicine to particular appointment
                addMedicineAlert();
            }
        });
        LoginActivity.sharedPreferences = context.getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        String userType = LoginActivity.sharedPreferences.getString(LoginActivity.USERTYPE, "");
        if (userType.equalsIgnoreCase("Doctor")) {
            fab.setVisibility(View.VISIBLE);
        }

    }

    private void loadMyMedicine() {
        list = new ArrayList<Medicine>();
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_load_my_medicines(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            JSONArray array = json.getJSONArray("MedicineInfo");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                int id = object.getInt("id");
                                int appointment_id = object.getInt("appointment_id");
                                String medicine_name = object.getString("medicine_name");
                                int morning = object.getInt("morning");
                                int afternoon = object.getInt("afternoon");
                                int evening = object.getInt("evening");
                                int days = object.getInt("days");
                                Medicine medicine = new Medicine(i+1, appointment_id, medicine_name, morning, afternoon, evening, days);
                                list.add(medicine);
                            }
                            setUpRecyclerView();


                        } catch (Exception e) {
                            Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(ViewMedicineActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
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
        RequestQueue requestQueue = Volley.newRequestQueue(ViewMedicineActivity.this);
        requestQueue.add(stringRequest);
    }

    private void setUpRecyclerView() {
        adapter = new MedicineAdapter(context, list);
        FixedGridLayoutManager manager = new FixedGridLayoutManager();
        manager.setTotalColumnCount(1);
        rvMedicine.setLayoutManager(manager);
        rvMedicine.setAdapter(adapter);
        rvMedicine.addItemDecoration(new DividerItemDecoration(context, DividerItemDecoration.VERTICAL));
    }

    private void addMedicineAlert() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.add_medicine_layout, null);
        dialogBuilder.setView(dialogView);
        final AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Button btnAdd = (Button) dialogView.findViewById(R.id.btnAddMedicine);
        Button btnCancel = (Button) dialogView.findViewById(R.id.btnCancel);
        final EditText etName = (EditText) dialogView.findViewById(R.id.txtMedicineName);
        final EditText etDays = (EditText) dialogView.findViewById(R.id.txtDays);
        final Spinner spMorning = dialogView.findViewById(R.id.spMorning);
        final Spinner spEvening = dialogView.findViewById(R.id.spEvening);
        final Spinner spAfternoon = dialogView.findViewById(R.id.spAfternoon);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //issue book information

                if (etName.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter medicine name", Toast.LENGTH_SHORT).show();
                    etName.requestFocus();
                    return;
                }
                if (spMorning.getSelectedItemPosition() == 0 || spAfternoon.getSelectedItemPosition() == 0 ||
                        spEvening.getSelectedItemPosition() == 0) {
                    Toast.makeText(context, "Please select timing", Toast.LENGTH_SHORT).show();
                    spMorning.requestFocus();
                    return;
                }
                if (etDays.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter number of days", Toast.LENGTH_SHORT).show();
                    etDays.requestFocus();
                    return;
                }
                addMedicine(etName.getText().toString(), etDays.getText().toString(), spMorning.getSelectedItem().toString(), spAfternoon.getSelectedItem().toString(), spEvening.getSelectedItem().toString());
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

    private void addMedicine(final String name, final String days, final String mornng, final String afternoon, final String evening) {
        final ProgressDialog dialog = new ProgressDialog(context);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_add_medicine(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    dialog.dismiss();
                    JSONObject json = new JSONObject(response);
                    if (json.has(ServerUtility.TAG_SUCCESS)) {
                        Toast.makeText(context, "Medicine added", Toast.LENGTH_SHORT).show();
                        int morn = Integer.parseInt(mornng);
                        int after = Integer.parseInt(afternoon);
                        int even = Integer.parseInt(evening);
                        Medicine medicine = new Medicine(list.size()+1, Integer.parseInt(id), name, morn, after, even, Integer.parseInt(days));
                        list.add(medicine);
                        setUpRecyclerView();
                    } else {
                        Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
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

                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("days", days);
                params.put("morning", mornng);
                params.put("afternoon", afternoon);
                params.put("evening", evening);
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

}