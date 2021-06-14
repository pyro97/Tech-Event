package com.simonepirozzi.techevent.data.db.model;

import java.util.UUID;

public class Event {
    private String email, title, date, initalTime, finalTime,
            position, cost, manager, city, photo, publishDate, description, id, state, province;
    private int priority;

    public Event() {
    }

    public Event(String email, String title, String date, String initalTime, String finalTime, String position, String cost, String manager, String city, String photo, String publishDate, String description, String state, String province, int priority) {
        this.email = email;
        this.title = title;
        this.date = date;
        this.initalTime = initalTime;
        this.finalTime = finalTime;
        this.position = position;
        this.cost = cost;
        this.manager = manager;
        this.city = city;
        this.photo = photo;
        this.publishDate = publishDate;
        this.description = description;
        this.id = UUID.randomUUID().toString();
        this.state = state;
        this.province = province;
        this.priority = priority;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getInitalTime() {
        return initalTime;
    }

    public void setInitalTime(String initalTime) {
        this.initalTime = initalTime;
    }

    public String getFinalTime() {
        return finalTime;
    }

    public void setFinalTime(String finalTime) {
        this.finalTime = finalTime;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }


    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }


}
