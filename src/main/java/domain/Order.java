/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Wouter
 */
public class Order {
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    private static AtomicLong nextId = new AtomicLong();
    private final long id;
    
    private double costPerPerson;
    private LocalDateTime date;
    
    public Order(double costPerPerson, LocalDateTime date){
        id = nextId.incrementAndGet();
        setCostPerPerson(costPerPerson);
        setDate(date);
    }

    public long getId(){
        return id;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;   
    }
    
    public LocalDateTime getDate(){
        return date;
    }

    public void setCostPerPerson(double amount) {
        if(amount < 0){
            throw new DomainException("an order can't have a negative cost.");
        }
        this.costPerPerson = amount;
    }
    
    public double getCostPerPerson(){
        return costPerPerson;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Order){
            return ((Order)obj).getId() == getId();
        }
        return false;
    }
    
    
}
