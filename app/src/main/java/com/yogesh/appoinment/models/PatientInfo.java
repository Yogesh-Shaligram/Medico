package com.yogesh.appoinment.models;

public class PatientInfo {
    private String patientName;
    private String mobile;
    private String email;
    private String address;
    private String id;

    public PatientInfo(String patientName, String mobile, String email, String address, String id) {
        this.patientName = patientName;
        this.mobile = mobile;
        this.email = email;
        this.address = address;
        this.id = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public String getMobile() {
        return mobile;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public String getId() {
        return id;
    }
}
