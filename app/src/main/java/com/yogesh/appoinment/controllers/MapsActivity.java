package com.yogesh.appoinment.controllers;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.yogesh.appoinment.HelperUtils.ServerUtility;
import com.yogesh.appoinment.R;
import com.yogesh.appoinment.models.Client;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    double lat;
    double lon;
    List<Client> list = new ArrayList<>();
//    DatabaseReference database;
    public static String SearchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        GPSTracker gpsTracker = new GPSTracker(this);
        if (gpsTracker.canGetLocation()) {
            lat = gpsTracker.getLatitude();
            lon = gpsTracker.getLongitude();
            ServerUtility.latitude = lat;
            ServerUtility.longitude = lon;
        }
//        database = FirebaseDatabase.getInstance().getReference("Doctor");
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(lat, lon);
        mMap.addMarker(new MarkerOptions().position(sydney).title("My Location").icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_on_black_24dp)));

        loadDoctorDetails();
        Toast.makeText(this, "Select doctor for appointment", Toast.LENGTH_SHORT).show();


        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 12.0f));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, "" + marker.getTitle(), Toast.LENGTH_SHORT).show();
                if (marker.getTitle().equals("My Location")) {
                } else {
                    String mobile = marker.getSnippet();
                    for (int i = 0; i < list.size(); i++) {
                        Client spInfo = list.get(i);
                        if (mobile.equals(spInfo.getPhone())) {

                            Intent intent = new Intent(MapsActivity.this, BookActivity.class);
                            intent.putExtra("Name", spInfo.getFirstName() + " " + spInfo.getLastName());
                            intent.putExtra("Email", spInfo.getClientID());
                            intent.putExtra("Mobile", spInfo.getPhone());
                            startActivity(intent);
                            finish();
                        }
                    }
                }

                return false;
            }
        });

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void loadDoctorDetails() {
        final ProgressDialog dialog = new ProgressDialog(MapsActivity.this);
        dialog.setCancelable(false);
        dialog.setMessage("Please wait...");
        dialog.setIndeterminate(false);
        dialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerUtility.url_load_doctors(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            if (dialog.isShowing())
                                dialog.dismiss();
                            JSONObject json = new JSONObject(response);
                            JSONArray array = json.getJSONArray("DoctorInfo");
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject object = array.getJSONObject(i);
                                String fname = object.getString("fname");
                                String lname = object.getString("lname");
                                String email = object.getString("email");
                                String usertype = object.getString("usertype");
                                String mobile = object.getString("mobile");
                                String specialization = object.getString("specialization");
                                String latitude = object.getString("latitude");
                                String longitude = object.getString("longitude");
                                Client client = new Client();
                                client.setFirstName(fname);
                                client.setLastName(lname);
//                                client.setClientID(object.getString("id"));
                                client.setClientID(email);
                                client.setLongitude(longitude);
                                client.setLatitude(latitude);
                                client.setSpecialization(specialization);
                                client.setPhone(mobile);
                                client.setUserType(usertype);
                                if (client.getSpecialization().toLowerCase().contains(SearchQuery.toLowerCase())) {
                                    list.add(client);
                                    double latitude1 = Double.parseDouble(client.getLatitude());
                                    double longitude1 = Double.parseDouble(client.getLongitude());
                                    LatLng sydney2 = new LatLng(latitude1, longitude1);
                                    mMap.addMarker(new MarkerOptions().position(sydney2).title(client.getFirstName()).snippet(client.getPhone()).icon(bitmapDescriptorFromVector(MapsActivity.this, R.drawable.ic_local_hospital_black_24dp)));
                                }


                            }
                        } catch (Exception e) {
                            Toast.makeText(MapsActivity.this, "Error", Toast.LENGTH_SHORT).show();
                            if (dialog.isShowing())
                                dialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MapsActivity.this, "Error..", Toast.LENGTH_SHORT).show();
                if (dialog.isShowing())
                    dialog.dismiss();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

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
        RequestQueue requestQueue = Volley.newRequestQueue(MapsActivity.this);
        requestQueue.add(stringRequest);

    }


}
