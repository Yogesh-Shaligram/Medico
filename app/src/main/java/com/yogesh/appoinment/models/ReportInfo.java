package com.yogesh.appoinment.models;

public class ReportInfo {
    private String id;
    private String report;
    private String filename;

    public ReportInfo(String id, String report, String filename) {
        this.id = id;
        this.report = report;
        this.filename = filename;
    }

    public String getId() {
        return id;
    }

    public String getReport() {
        return report;
    }

    public String getFilename() {
        return filename;
    }
}
