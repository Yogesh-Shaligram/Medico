package com.yogesh.appoinment.HelperUtils;

/**
 * Created by jaja on 28-12-2015.
 */
public class ServerUtility {
    public static final String TAG_SUCCESS = "success";
    public static Boolean Isservicelogin = false;
    public static String Server_URL = "http://192.168.0.102:8084/EHRServer/";
    public static String username = "";
    public static String companyName = "";
    public static String status = "";
    public static String VALET = "VALET";
    public static String txtEmail;
    public static String txtUsername;
    public static double latitude = 0.0;
    public static double longitude = 0.0;
    public static String USERTYPE="doctor";


    public static String url_login() {
        return Server_URL + "UserLogin";
    }

    public static String url_register() {
        return Server_URL + "UserRegistration";
    }

    public static String url_load_doctors() {
        return Server_URL + "GetAllDoctors";
    }
    public static String url_load_labs() {
        return Server_URL + "GetAllLabs";
    }

    public static String url_book_appointment() {
        return Server_URL + "BookMyAppointment";
    }
    public static String url_book_test_appointment() {
        return Server_URL + "BookTestAppointment";
    }

    public static String url_load_my_appointments() {
        return Server_URL + "MyAppointments";
    }
    public static String url_load_my_lab_appointments() {
        return Server_URL + "GetMyLabAppointment";
    }

    public static String url_update_my_appointments() {
        return Server_URL + "UpdateAppointments";
    }
    public static String url_load_my_medicines() {
        return Server_URL + "MyMedicines";
    }

    public static String url_add_medicine() {
        return Server_URL + "AddMedicine";
    }

    public static String url_load_patientInfo() {
        return Server_URL + "GetPatientInfo";
    }

    public static String url_load_patient_history() {
        return Server_URL + "GetPatientHistory";
    }

    public static String url_load_reports() {
        return Server_URL + "GetPatientReports";
    }
}
