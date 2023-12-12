package com.example.gwtalenttrade;

import java.util.Objects;

//User POJO class with necessary properties, constructor, as well as getters & setters
// hashcode and equals implemented to allow comparison between user objects
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(fullName, user.fullName) && Objects.equals(gwid, user.gwid) && Objects.equals(email, user.email) && Objects.equals(dob, user.dob) && Objects.equals(phone, user.phone);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, gwid, email, dob, phone);
    }
}
