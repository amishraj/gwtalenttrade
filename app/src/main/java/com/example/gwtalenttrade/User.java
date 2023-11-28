package com.example.gwtalenttrade;

public class User {

    String fullName;
    String gwid;
    String email;
    String dob;

    String phone;

    public User(){

    }

    public User(String fullName, String gwid, String email, String dob, String phone) {
        this.fullName= fullName;
        this.gwid= gwid;
        this.email=email;
        this.dob=dob;
        this.phone= phone;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getGwid() {
        return gwid;
    }

    public void setGwid(String gwid) {
        this.gwid = gwid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
