/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

/**
 *
 * @author Wouter
 */
@NamedQueries({
    @NamedQuery(name="Person.getAll", query="select p from Person p"),
}) 
@Entity(name="Person")
public class Person {
    @Id
    @Column(name="PERSON_ID")
    @GeneratedValue
    private long id;

    //The meaning of CascadeType.ALL is that the persistence will propagate (cascade) all EntityManager operations (PERSIST, REMOVE, REFRESH, MERGE, DETACH) to the relating entities.
    //orphanRemoval normally also deletes the payment if it is no longer referenced by the source.
    //@OneToMany(mappedBy="author", orphanRemoval=true, cascade={CascadeType.ALL})
    @OneToMany(mappedBy="author", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE, CascadeType.MERGE, CascadeType.PERSIST, CascadeType.REFRESH})
    private Set<Payment> payments;
    
    @ManyToMany(mappedBy="authors", fetch=FetchType.LAZY, cascade={CascadeType.REMOVE, CascadeType.MERGE})
    private Set<OrderBill> orders;

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
    
    public boolean isRegisteredToOrder(long orderId){
        for(OrderBill order: orders){
            if(order.getId() == orderId)
                return true;
        }
        return false;
    }
}
