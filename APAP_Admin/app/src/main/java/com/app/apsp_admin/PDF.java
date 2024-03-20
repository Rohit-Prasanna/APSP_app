package com.app.apsp_admin;

public class PDF {

    public String userid;
    public String filePath;
    public String fileName;
    public String location;
    public Object timestamp;

    public PDF() {
        // Default constructor required for calls to DataSnapshot.getValue(PDF.class)
    }

    public PDF(String userid, String filePath, String fileName, String location, Object timestamp) {
        this.userid = userid;
        this.filePath = filePath;
        this.fileName = fileName;
        this.location = location;
        this.timestamp = timestamp;
    }
}
