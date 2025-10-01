package com.eventra.entity;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String location;
    private String organizer;
    private String status;
    private LocalDate date;
    private String category;
    private Integer capacity;



        public Event() {} // no-args constructor
     public LocalDate getDate(){
        return date;
    }
    public String getLocation(){
        return location;
    }
    public String getOrganizer(){
        return organizer;
    }
    public int getCapacity(){
        return capacity;
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
    public String getCategory(){
        return category;
    }
    public void setLocation(String location){
        this.location=location;

    }
    public void setCapacity(int cap){
        this.capacity=cap;
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
