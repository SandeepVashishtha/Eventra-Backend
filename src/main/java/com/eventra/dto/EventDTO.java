package com.eventra.dto;

import java.time.LocalDate;

public class EventDTO {
    private LocalDate date;
    private String description;
    private String title;
    private String status;
    private String category;
    private int capacity;
    private String organizer;
    private String location;

    public LocalDate getDate(){
        return date;
    }
    public String getDescription(){
        return description;
    }
    public String getTitle(){
        return title;
    }
    public String getStatus(){
        return status;
    }
    public String getOrganizer(){
        return organizer;
    }
    public String getLocation(){
        return location;
    }
    public String getCategory(){
        return category;
    }
    public int getCapacity(){
        return capacity;
    }
    public void setCapacity(int capacity){
        this.capacity=capacity;
    }
    public void setLocation(String loc){
        this.location=loc;
    }
    public void setOrganizer(String org){
        this.organizer=org;
    }
    public void setDate(LocalDate date){
        this.date=date;
    }
    public void setDescription(String description){
        this.description=description;
    }
    public void setTitle(String title){
        this.title=title;
    }
    public void setStatus(String status){
        this.status=status;
    }
    public void setCategory(String category){
        this.category=category;
    }
}
