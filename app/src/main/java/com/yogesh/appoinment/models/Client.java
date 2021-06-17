/*************************************************************************************************
 * JANUARY 8, 2018
 * Mentesnot Aboset
 ************************************************************************************************/
package com.yogesh.appoinment.models;


import android.graphics.Bitmap;

public class Client {

    private String clientID;
    private String firstName;
    private String lastName;
    private String phone;
    private Bitmap image;
    private String userType;
    private String latitude;
    private String longitude;
    private String specialization;
    private String address;

    public Client() {

    }

    public Client(String clientID, String firstName, String lastName, String phone, Bitmap image, String userType, String latitude, String longitude, String specialization) {
        this.clientID = clientID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.image = image;
        this.userType = userType;
        this.latitude = latitude;
        this.longitude = longitude;
        this.specialization = specialization;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public String getClientID() {
        return clientID;
    }

    public void setClientID(String clientID) {
        this.clientID = clientID;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public Bitmap getImage() {
        return image;
    }

    public void setImage(Bitmap image) {
        this.image = image;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
