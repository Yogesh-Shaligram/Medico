package com.yogesh.appoinment.models;

public class TestAppointment {
    private String id;
    private String lab_name;
    private String email;
    private String mobile;
    private String date;
    private String time;
    private String status;

    public TestAppointment(String id, String lab_name, String email, String mobile, String date, String time, String status) {
        this.id = id;
        this.lab_name = lab_name;
        this.email = email;
        this.mobile = mobile;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getLab_name() {
        return lab_name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getStatus() {
        return status;
    }
}
