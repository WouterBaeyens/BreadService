/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import database.DbException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wouter
 */
//@Table(name="orderBillTable")
@Entity(name="OrderBill")
@NamedQueries({
    @NamedQuery(name="Order.getAll", query="select o from OrderBill o"),
    @NamedQuery(name="Order.findOrders", query="select o from OrderBill o where o.weekNr = :w and o.yearNr = :y")
}) 
public class OrderBill implements Transaction{
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    @Id
    @Basic(optional = false)                             
    @NotNull
    //Commented out, because eclipse link can't fucking recognize that this entity is order and not person
    //sorry i'm frustrated
    //@Column(name="ORDER_ID")
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
    //private LocalDate date;
    private int yearNr;
    private int weekNr;
    
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
        
        public OrderBill(double totalCost, int weekNr, int yearNr){
            this.weekNr = weekNr;
            this.yearNr = yearNr;
            setTotalCost(totalCost);
            authors = new HashSet<>();
        }
        
        /*public OrderBill(double totalCost, int year, int week){
            setTotalCost(totalCost);
            this.year = year;
            this.week = week;
            authors = new HashSet<>();
        }*/
    
        public OrderBill(){
            this(0,LocalDate.now());
        }
    public void setId(long id){
        throw new DbException("don't chang your id!");
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
        weekNr = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        yearNr = date.get(WeekFields.ISO.weekBasedYear());
        //this.date = date;   
    }
    
    public LocalDate getDate(){
        return getLocalDate(yearNr, weekNr, 1);
        //return date;
    }
    
    private LocalDate getLocalDate(int yearNr, int weekNr, int dayNr){
        LocalDate date = LocalDate.now().withYear(yearNr)
                .with(WeekFields.ISO.weekOfYear(), weekNr)
                .with(WeekFields.ISO.dayOfWeek(), dayNr);
        return date;
    }
    
    
    public String getFormattedDate(){   
        return "Week " + weekNr + ": " 
                + getLocalDate(yearNr,weekNr,1).format(DateTimeFormatter.ISO_LOCAL_DATE) + " - " 
                + getLocalDate(yearNr,weekNr,7).format(DateTimeFormatter.ISO_LOCAL_DATE);
        
        //return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
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
    
    public void removeAllAuthors(){
        Set<Person> authorSet = new HashSet(this.getAuthors());
        authors.clear();
        for(Person author: authorSet){
            System.out.println("removed " + author);
            author.removeOrder(this);
        }
    }
    
    public int getYear(){
        return yearNr;
    }
    
    public int getWeek(){
        return weekNr;
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
    
    public String toString(){
        String authors ="";
        for(Person p : this.getAuthors()){
            authors += p.getId() + " ";
        }
        String result = "Order" + this.getId() + ": " + this.getDate() + " " + this.getTotalCost() + "$ payed by (" + authors + ")";
        return result;
    }
    
    
    
}


