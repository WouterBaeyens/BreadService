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
public class Order implements Transaction{
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    private long id;
    
    private double costPerPerson;
    private double totalCost;
    private LocalDateTime date;
    
    @Deprecated
    public Order(LocalDateTime date, double costPerPerson){
        id = -1;
        setCostPerPerson(costPerPerson);
        setDate(date);
    }
    
    public Order(long id, double totalCost, LocalDateTime date){
        this.id = id;
        setTotalCost(totalCost);
        setDate(date);
    }
    
        public Order(double totalCost, LocalDateTime date){
        this(-1,totalCost,date);
    }
    
    public void setId(long id){
        this.id = id;
    }
    
    public long getId(){
        return id;
    }
    
    public void setTotalCost(double amount){
         if(amount < 0){
            throw new DomainException("an order can't have a negative cost.");
        }
        this.totalCost = amount;
    }
    
    public double getTotalCost(){
        return totalCost;
    }
    
        /*Returns the amount of people that are signed up for this order*/
    int getNumberOfPersonsForOrder(long orderId){
        throw new DomainException("getNumberOfPersonsForOrder is not yet implemented - relation only works in oposite direction for now");
    }

    @Deprecated
    public void setCostPerPerson(double amount) {
        if(amount < 0){
            throw new DomainException("an order can't have a negative cost.");
        }
        this.costPerPerson = amount;
    }
    
    public double getCostPerPerson(){
        return costPerPerson;
    }
    
        public void setDate(LocalDateTime date) {
        this.date = date;   
    }
    
    public LocalDateTime getDate(){
        return date;
    }
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Order){
            return ((Order)obj).getId() == getId();
        }
        return false;
    }

    @Override
    public int compareTo(Transaction transaction) {
        return this.getDate().compareTo(transaction.getDate());
    }
    
    
    
}
