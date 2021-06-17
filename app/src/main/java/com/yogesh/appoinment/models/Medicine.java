package com.yogesh.appoinment.models;

public class Medicine {
    private  int id;
    private int appointment_id;
    private String medicine_name;
    private int morning;
    private int afternoon;
    private int evening;
    private int days;

    public Medicine(int id, int appointment_id, String medicine_name, int morning, int afternoon, int evening, int days) {
        this.id = id;
        this.appointment_id = appointment_id;
        this.medicine_name = medicine_name;
        this.morning = morning;
        this.afternoon = afternoon;
        this.evening = evening;
        this.days = days;
    }

    public int getId() {
        return id;
    }

    public int getAppointment_id() {
        return appointment_id;
    }

    public String getMedicine_name() {
        return medicine_name;
    }

    public int getMorning() {
        return morning;
    }

    public int getAfternoon() {
        return afternoon;
    }

    public int getEvening() {
        return evening;
    }

    public int getDays() {
        return days;
    }
}
