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
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author Wouter
 */
//@Table(name="orderBillTable")
@Entity(name="OrderBill")
@NamedQueries({
    @NamedQuery(name="Order.getAll", query="select o from OrderBill o"),
}) 
public class OrderBill implements Transaction{
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    @Id
    @GeneratedValue
    private long id;
    
    @ManyToMany
    @JoinTable(
            name="ORDER_PERSON",
            joinColumns=@JoinColumn(name="ORDER_ID"),
            inverseJoinColumns=@JoinColumn(name="PERSON_ID")
    )
    private Set<Person> authors;
    
    private double totalCost;
    private LocalDate date;
    
    @Deprecated
    public OrderBill(LocalDate date, double costPerPerson){
        setCostPerPerson(costPerPerson);
        setDate(date);
    }
    
    public OrderBill(long id, double totalCost, LocalDate date){
        this.id = id;
        setTotalCost(totalCost);
        setDate(date);
        authors = new HashSet<>();
    }
    
        public OrderBill(double totalCost, LocalDate date){
        setTotalCost(totalCost);
        setDate(date);
        authors = new HashSet<>();
    }
    
        public OrderBill(){
            this(0,LocalDate.now());
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
    
    @Deprecated
    int getNumberOfPersonsForOrder(long orderId){
        throw new DomainException("unsupported operation"); 
    }
    
    /*Returns the amount of people that are signed up for this order*/
    int getNumberOfPersonsForOrder(){
        return authors.size();
    }

    @Deprecated
    public void setCostPerPerson(double amount) {
        throw new DomainException("unsupported operation");
    }
    
    public double getCostPerPerson(){
        return getTotalCost() / getNumberOfPersonsForOrder();
    }
    
        public void setDate(LocalDate date) {
        this.date = date;   
    }
    
    public LocalDate getDate(){
        return date;
    }
    
        public String getFormattedDate(){
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }
    
    public Set<Person> getAuthors(){
        return authors;
    }
    
    public void addAuthor(Person author){
        authors.add(author);
        if(!author.getOrders().contains(this)){
            author.addOrder(this);
        }
    }
    
    public void removeAuthor(Person author){
        authors.remove(author);
        if(author.getOrders().contains(this))
            author.removeOrder(this);
    }
    
   /* @Override
    public boolean equals(Object obj){
        if (obj instanceof OrderBill){
            return ((OrderBill)obj).getId() == getId();
        }
        return false;
    }*/

    @Override
    public int compareTo(Transaction transaction) {
        return this.getDate().compareTo(transaction.getDate());
    }

    @Override
    public String getType() {
        return "Order";
    }

    @Override
    public double getTransactionValue() {
        return -getTotalCost() / getNumberOfPersonsForOrder();
    }
    
    
    
}


