/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

/**
 *
 * @author Wouter
 */
@Entity(name="Payment")
@NamedQueries({
    @NamedQuery(name="Payment.getAll", query="select p from Payment p"),
}) 
public class Payment implements Transaction{
    @Id
    @GeneratedValue
    private long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="authorId", updatable = true, insertable = true)
    private Person author = null;

    
    private double amount;
    private LocalDate date;
    
    public Payment(double amount, LocalDate date){
        setAmount(amount);
        setDate(date);
    }
    
    public Payment(){
        this(0, LocalDate.now());
    }
    
    public void setId(long id){
        this.id = id;
    }
    
        public long getId(){
        return id;
    }
    
    public Person getAuthor(){
        return author;
    }
        
    public void setAuthor(Person author){
        this.author = author;
        if(!author.getPayments().contains(this)){
            author.getPayments().add(this);
        }
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
    
    public void setDate(LocalDate datum){
        this.date = datum;
    }
    
    public LocalDate getDate(){
        return date;
    }
    
    public String getFormattedDate(){
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
      
 /*   @Override
    public boolean equals(Object obj){
        if (obj instanceof Payment){
            return ((Payment)obj).getId() == getId();
        }
        return false;
    }*/
    
    @Override
    public int compareTo(Transaction transaction) {
        return this.getDate().compareTo(transaction.getDate());
    }

    @Override
    public String getType() {
        return "Payment";
    }

    @Override
    public double getTransactionValue() {
        return amount;
    }
}
