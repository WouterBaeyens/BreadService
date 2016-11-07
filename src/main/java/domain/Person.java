/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.voodoodyne.jackson.jsog.JSOGGenerator;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotNull;
import jdk.nashorn.internal.objects.NativeArray;

/**
 *
 * @author Wouter
 */
//@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@JsonIdentityInfo(generator=JSOGGenerator.class)
@NamedQueries({
    @NamedQuery(name="Person.getAll", query="select p from Person p"),
    @NamedQuery(name="Person.FetchOrders", query="select p from Person p join fetch p.orders where p.id = :id"),
}) 
@Entity(name="Person")
public class Person {
    @Id
    //@NotNull
    @Column(name="PERSON_ID")
    @GeneratedValue
    private long id;

    //Note: in order to keep author and payment seperated, 
    //cascades should probably be removed and replaced by management on the payment side.
    
    //The meaning of CascadeType.ALL is that the persistence will propagate (cascade) all EntityManager operations (PERSIST, REMOVE, REFRESH, MERGE, DETACH) to the relating entities.
    //orphanRemoval normally also deletes the payment if it is no longer referenced by the source.
    //@OneToMany(mappedBy="author", orphanRemoval=true, cascade={CascadeType.ALL})
    @OneToMany(mappedBy="author", fetch=FetchType.LAZY, cascade={CascadeType.ALL})
    private Set<Payment> payments;
    
    
    
    @ManyToMany
    @JoinTable(
            name="ORDER_PERSON",
            inverseJoinColumns = {
                @JoinColumn(name="weekNr", referencedColumnName = "weekNr"),
                @JoinColumn(name="yearNr", referencedColumnName = "yearNr")},
            joinColumns=@JoinColumn(name="PERSON_ID")          
    )    
    
    //@ManyToMany(mappedBy="authors", fetch=FetchType.LAZY, cascade={CascadeType.MERGE, CascadeType.REFRESH})
    //@ManyToMany(mappedBy="authors", fetch=FetchType.LAZY)
    private Set<OrderBill> orders;

    @Column(name="name")
    private String name = "undefined";

    
    public Person(){
        this("undefined");
    };
    
    public Person(String name){
        setName(name);
        payments = new HashSet<Payment>();
        orders = new HashSet<OrderBill>();
    }

    public void setName(String name) {
        if(name == null){
            throw new DomainException("name must be defined");
        }
        this.name = name;
    }
    
    public String getName(){
        return name;
    }
    
    public void setId(long id){
        this.id = id;
    }
        
    public long getId(){
        return id;
    }
    
    //For now doesn't include orders.
    public double getSaldo(){
        double saldo = 0;
        for(Payment payment: getPayments()){
            saldo += payment.getTransactionValue();
        }
        for(OrderBill order: getOrders()){
            saldo += order.getTransactionValue();
        }
        return saldo;
    }
    
    private void setSaldo(){
        
    }
    
    public void addOrder(OrderBill order){
        orders.add(order);
        if(!order.getAuthors().contains(this)){
            order.addAuthor(this);
        }
    }
    
    public void removeOrder(OrderBill order){ 
       orders.remove(order);
        if(order.getAuthors().contains(this)){
            order.removeAuthor(this);
        }
    }
    
    public void removeAllOrders(){
        Set<OrderBill> orderSet = new HashSet(this.getOrders());
        for(OrderBill order: orderSet){
            System.out.println("removed [" + order + "] from [" + this + "]");
            order.removeAuthor(this);
        }
    }
    
    
    public Set<OrderBill> getOrders(){
        return orders;
    }
    
    public void addPayment(Payment payment){
        payments.add(payment);
        if(payment.getAuthor() != this){
            payment.setAuthor(this);
        }
    }
    
    public void removePayment(Payment payment){
        payments.remove(payment);
        if(payment.getAuthor() == this){
            payment.setAuthor(null);
        }
    }
    
    public Set<Payment> getPayments(){
        return payments;
    }
    
    public double getPaymentCurrentWeek(){
    OrderWeek week = new OrderWeek(LocalDate.now());
        for(OrderBill o : orders){
        if(o.getOrderPK().equals(week.getOrderWeekPK()))
                return o.getCostPerPerson();
    }
        return 0;
    }
    
    public boolean isRegisteredToOrder(long orderId){
        for(OrderBill order: orders){
            if(order.getId() == orderId)
                return true;
        }
        return false;
    }
    
    public String toString(){
        String orders ="";
        for(OrderBill o: this.getOrders()){
            orders += o.getId() + " ";
        }
        String payments = "";
        for(Payment p: this.getPayments()){
            payments += p.getId() + " ";
        }
        String result = "Person" + this.getId() + ": " + this.getName() + " orders(" + orders +")" + "paym(" + payments + ")";
        return result;
    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        if(!(obj instanceof Person))
            return false;
        return ((Person) obj).getId() == this.getId();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (int) (this.id ^ (this.id >>> 32));
        return hash;
    }
}
