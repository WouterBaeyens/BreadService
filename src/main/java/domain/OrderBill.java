/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import database.DbException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

/**
 *
 * @author Wouter
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIdentityInfo(generator=JSOGGenerator.class)
//@Table(name="orderBillTable")
@Entity(name="OrderBill")
@NamedQueries({
    @NamedQuery(name="Order.truncate", query="delete from OrderBill"),
    @NamedQuery(name="Order.getAll", query="select o from OrderBill o"),
    @NamedQuery(name="Order.findOrders", query="select o from OrderBill o join o.orderWeek as w where (w.orderWeekPK.weekNr = :w and w.orderWeekPK.yearNr = :y)")
}) 
public class OrderBill implements Transaction{
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    @EmbeddedId
    private OrderWeekPK orderWeekPK;
    
    
    //@ManyToMany(mappedBy="orders", fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH})
     @ManyToMany(mappedBy="orders", fetch=FetchType.EAGER)
    
    /*@ManyToMany
    @JoinTable(
            name="ORDER_PERSON",
            joinColumns = {
                @JoinColumn(name="weekNr", referencedColumnName = "weekNr"),
                @JoinColumn(name="yearNr", referencedColumnName = "yearNr")},
            inverseJoinColumns=@JoinColumn(name="PERSON_ID")          
    )    */
    private Set<Person> authors;
    
    //no cascade persist, since that would cause errors if the week already exists
    //@OneToOne
    @OneToOne (mappedBy = "orderBill")
    /*@JoinColumns(value = {
        @JoinColumn(name="weekNrPk", referencedColumnName = "weekNr"),
        @JoinColumn(name="yearNrPk", referencedColumnName = "yearNr")})
    /*@PrimaryKeyJoinColumns(value = {
        @PrimaryKeyJoinColumn(name="weekNrPk", referencedColumnName = "weekNr"),
        @PrimaryKeyJoinColumn(name="yearNrPk", referencedColumnName = "yearNr")})*/
    @MapsId
    private OrderWeek orderWeek;
    
    
    @Column(name="totalCost")
    private double totalCost;
    
    @Transient
    private static final DateTimeFormatter euFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    
    public OrderBill(double totalCost){
        setTotalCost(totalCost);
        authors = new HashSet<>();
    }
    
        public OrderBill(double totalCost, LocalDate date){
        setTotalCost(totalCost);
        setDate(date);
        authors = new HashSet<>();
    }
        
        public OrderBill(double totalCost, int weekNr, int yearNr){
            setOrderWeek(new OrderWeek(weekNr, yearNr));
            setTotalCost(totalCost);
            authors = new HashSet<>();
        }
        
        /*public OrderBill(double totalCost, int year, int week){
            setTotalCost(totalCost);
            this.year = year;
            this.week = week;
            authors = new HashSet<>();
        }*/
    
        public OrderBill(){}
        
    public void setId(long id){
        throw new DbException("this ID is readonly!");
    }
    
    public OrderWeekPK getOrderPK(){
        return orderWeekPK;
    }
    
    public void setOrderPK(OrderWeekPK orderWeekPK){
        this.orderWeekPK = orderWeekPK;
    }
    
    public int getId(){
        return orderWeekPK == null ? -1 : orderWeekPK.hashCode();
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
    
    public double getCostPerPerson(){
        if(getNumberOfPersonsForOrder() == 0)
            throw new DomainException("ERROR: asking cost per person for order with no persons." + this.toString());
        return getTotalCost() / getNumberOfPersonsForOrder();
    }
    
    public void setDate(LocalDate date) {
        int weekNr = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int yearNr = date.get(WeekFields.ISO.weekBasedYear());
        setOrderWeek(new OrderWeek(weekNr, yearNr));
    }
    
    @JsonIgnore
    public LocalDate getDate(){
        return orderWeek.getDate();
        //return date;
    }
    

    
    public String getFormattedDate(){  
        if(orderWeek == null)
            return "No date.";
        return orderWeek.getFormattedDate();
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
        //authors.remove(author);
    }
    
    public void removeAllAuthors(){
        Set<Person> authorSet = new HashSet(this.getAuthors());
        //authors.clear();
        for(Person author: authorSet){
            System.out.println("removed [" + author + "] from [" + this + "]");
            //author.removeOrder(this);
            removeAuthor(author);
        }
    }
    
    public int getYearNr(){
        if(orderWeek == null)
            return -1;
        return orderWeek.getYearNr();
    }
    
    
    public int getWeekNr(){
        if(orderWeek == null)
            return -1;
        return orderWeek.getWeekNr();
    }
    
    public OrderWeek getOrderWeek(){
        return orderWeek;
    }
    
    public void setOrderWeek(OrderWeek orderWeek){
        this.orderWeek = orderWeek;
        this.orderWeekPK = orderWeek.getOrderWeekPK();
        if(orderWeek.getOrderBill() == null )
            orderWeek.setOrder(this);
        else if(orderWeek.getOrderBill() != this)
            throw new DomainException("ERROR: this bill is already linked to another week: (" + orderWeek + " | " + this + ")");
    }
    
    public void setOrderWeekOneWay(OrderWeek orderWeek){
        this.orderWeek = orderWeek;
        this.orderWeekPK = orderWeek.getOrderWeekPK();
    }
    
    public void removeOrderWeek(){
        OrderWeek o = orderWeek;
        orderWeek = null;
        orderWeekPK = null;
        if (o != null && o.getOrderBill() != null)
            o.removeOrderBill();
    }

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
        if(getNumberOfPersonsForOrder() == 0)
            throw new DomainException("ERROR: asking transaction per person for order with no persons." + this.toString());
        return -getTotalCost() / getNumberOfPersonsForOrder();
    }
    
    public String toString(){
        String authors ="";
        for(Person p : this.getAuthors()){
            authors += p.getId() + " ";
        }
        String result = "Order" + this.getId() + ": " + this.getFormattedDate() + " " + this.getTotalCost() + "$ payed by (" + authors + ")";
        return result;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        if(!(obj instanceof OrderBill))
            return false;
        return ((OrderBill) obj).getOrderPK().equals(this.getOrderPK());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.orderWeekPK);
        return hash;
    }
    
    
    
}


