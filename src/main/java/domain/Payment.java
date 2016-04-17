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
public class Payment implements Transaction{
    private static AtomicLong nextId = new AtomicLong();
    private long id;
    
    private double amount;
    private LocalDateTime date;
    

    public Payment(double amount, LocalDateTime date){
        id = -1;
        setAmount(amount);
        setDate(date);
    }
    
        private void setId(){
            this.id = id;
        }
    
        public long getId(){
        return id;
    }
    
    public void setAmount(double amount){
        if(amount < 0){
            throw new DomainException("A person can't make a negative payment.");
        }
        this.amount = amount;
    }
    
    public double getAmount(){
        return amount;
    }
    
    public void setDate(LocalDateTime datum){
        this.date = datum;
    }
    
    public LocalDateTime getDate(){
        return date;
    }
      
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Payment){
            return ((Payment)obj).getId() == getId();
        }
        return false;
    }
    
    @Override
    public int compareTo(Transaction transaction) {
        return this.getDate().compareTo(transaction.getDate());
    }
}
