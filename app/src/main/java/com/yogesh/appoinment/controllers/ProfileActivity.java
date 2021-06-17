/*************************************************************************************************
 * JANUARY 8, 2018
 * Mentesnot Aboset
 * ************************************************************************************************/
package com.yogesh.appoinment.controllers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.yogesh.appoinment.R;

import java.io.ByteArrayOutputStream;

import static android.provider.MediaStore.Images.Media.getBitmap;

public class ProfileActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1;
    private Intent intent;
    private int clientID;
    private String TAG;
    private ImageButton uploadImage;
    private ImageView profileImage;

    private TextView clientFirstname;
    private TextView clientLastName;
    private TextView clientEmail;
    private TextView clientPhone;
    private TextView fullName;
    private ImageButton editProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        profileImage = (ImageView) findViewById(R.id.profileImage);
        uploadImage = (ImageButton) findViewById(R.id.btnEditProfilePicture);
        editProfile = (ImageButton) findViewById(R.id.btnEditProfile);

        clientID = clientID();

        getProfileInformation(clientID);
        loadImage(clientID);

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent uploadImageIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(uploadImageIntent, REQUEST_CODE);
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), EditProfileActivity.class);
                startActivity(intent);
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    //gets employee profile information
    public void getProfileInformation(int employeeID) {
        try {

            clientFirstname = (TextView) findViewById(R.id.txtClientFirstName);
            clientLastName = (TextView) findViewById(R.id.txtClientLastName);
            clientEmail = (TextView) findViewById(R.id.txtClientEmail);
            clientPhone = (TextView) findViewById(R.id.txtClientPhone);
            fullName = (TextView) findViewById(R.id.txtFullName);

            LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
            String name = LoginActivity.sharedPreferences.getString(LoginActivity.NAME, "");
            String fname[] = LoginActivity.sharedPreferences.getString(LoginActivity.NAME, "").split(" ");
            String mobile = LoginActivity.sharedPreferences.getString(LoginActivity.MOBILE, "");
            String email = LoginActivity.sharedPreferences.getString(LoginActivity.EMAIL, "");


            clientFirstname.setText("First Name: " + fname[0]);
            clientLastName.setText("Last Name: " + fname[1]);
            clientPhone.setText("Phone: " + mobile);

            clientEmail.setText("Email: " + email);

            fullName.setText(name);


        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Database unavailable", Toast.LENGTH_SHORT).show();
        }
    }

    public int clientID() {
        LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
        clientID = LoginActivity.sharedPreferences.getInt(LoginActivity.CLIENT_ID, 0);
        return clientID;
    }


    //uploads image from sd card
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode) {

                case REQUEST_CODE:
                    if (resultCode == Activity.RESULT_OK && data != null) {

                        //data gives you the image uri. Try to convert that to bitmap
                        Uri selectedImage = data.getData();

                        //uploadImage.setImageURI(selectedImage);
                        Bitmap bitmap = getBitmap(this.getContentResolver(), selectedImage);

                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream);

                        // Create a byte array from ByteArrayOutputStream
                        byte[] byteArray = stream.toByteArray();


                        break;
                    } else if (resultCode == Activity.RESULT_CANCELED) {
                        Log.e(TAG, "Selecting picture cancelled");
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }

    //loads image on create
    public void loadImage(int clientID) {
        try {


            String uri = "";
            LoginActivity.sharedPreferences = getSharedPreferences(LoginActivity.MY_PREFERENCES, Context.MODE_PRIVATE);
            String usertype = LoginActivity.sharedPreferences.getString(LoginActivity.USERTYPE, "");

            if (usertype.equalsIgnoreCase("Doctor")) {
                uri = "@drawable/doctor";  // where myresource (without the extension) is the file
            } else {
                uri = "@drawable/patient";
            }
            int imageResource = getResources().getIdentifier(uri, null, getPackageName());


            Drawable res = getResources().getDrawable(imageResource);
            profileImage.setImageDrawable(res);

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Image unavailable", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
