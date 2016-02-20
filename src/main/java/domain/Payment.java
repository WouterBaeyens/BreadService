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
public class Payment {
    private double amount;
    private Date date;
    
    public Payment(double amount, Date date){
        setAmount(amount);
        setDate(date);
    }
    
    private void setAmount(double amount){
        this.amount = amount;
    }
    
    public double getAmount(){
        return amount;
    }
    
    private void setDate(Date datum){
        this.date = datum;
    }
    
    public Date getDate(Date datum){
        return datum;
    }
            
}
