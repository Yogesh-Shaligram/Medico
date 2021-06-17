package com.yogesh.appoinment.models;

public class Appointment {
    private String patient_id;
    private String patient_name;
    private String patient_mobile;
    private String doctor_name;
    private String doctor_email;
    private String doctor_mobile;
    private String symptoms;
    private String appointment_date;
    private String appointment_time;
    private String status;
    private String id;

    public Appointment() {
    }



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPatient_id(String patient_id) {
        this.patient_id = patient_id;
    }

    public void setPatient_name(String patient_name) {
        this.patient_name = patient_name;
    }

    public void setPatient_mobile(String patient_mobile) {
        this.patient_mobile = patient_mobile;
    }

    public void setDoctor_name(String doctor_name) {
        this.doctor_name = doctor_name;
    }

    public void setDoctor_email(String doctor_email) {
        this.doctor_email = doctor_email;
    }

    public void setDoctor_mobile(String doctor_mobile) {
        this.doctor_mobile = doctor_mobile;
    }

    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }

    public void setAppointment_date(String appointment_date) {
        this.appointment_date = appointment_date;
    }

    public void setAppointment_time(String appointment_time) {
        this.appointment_time = appointment_time;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPatient_id() {
        return patient_id;
    }

    public String getPatient_name() {
        return patient_name;
    }

    public String getPatient_mobile() {
        return patient_mobile;
    }

    public String getDoctor_name() {
        return doctor_name;
    }

    public String getDoctor_email() {
        return doctor_email;
    }

    public String getDoctor_mobile() {
        return doctor_mobile;
    }

    public String getSymptoms() {
        return symptoms;
    }

    public String getAppointment_date() {
        return appointment_date;
    }

    public String getAppointment_time() {
        return appointment_time;
    }

    public String getStatus() {
        return status;
    }
}
