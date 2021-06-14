package com.simonepirozzi.techevent.data.db.model;

import java.util.ArrayList;

public class User {
    private ArrayList<Event> preferences;
    private String name, surname, city, mail, role, province;

    public User() { }

    public User(String n, String c, String ci, String m, String ruo, String pro) {
        name = n;
        surname = c;
        city = ci;
        mail = m;
        role = ruo;
        province = pro;
        preferences = new ArrayList<>();
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public ArrayList<Event> getPreferences() {
        return preferences;
    }

    public void setPreferences(ArrayList<Event> preferences) {
        this.preferences = preferences;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }
}
