/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.Date;

/**
 *
 * @author Wouter
 */
public class Order {
    
    private double costPerPerson;
    private Date date;
    
    public Order(double costPerPerson, Date date){
        setCostPerPerson(costPerPerson);
        setDate(date);
    }

    private void setDate(Date date) {
        this.date = date;   
    }
    
    public Date getDate(){
        return date;
    }

    private void setCostPerPerson(double amount) {
        this.costPerPerson = amount;
    }
    
    public double getCostPerPerson(){
        return costPerPerson;
    }
    
    
}
