/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class Person {
    
    private long id;
    private String name = "undefined";
    private Set<Payment> payments;
    private Set<Order> orders;
    
    public Person(){
        this("undefined");
    };
    
    public Person(String name){
        setName(name);
        payments = new HashSet<Payment>();
        orders = new HashSet<Order>();
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
            saldo += payment.getAmount();
        }
        return saldo;
    }
    
    private void setSaldo(){
        
    }
    
    public void addOrder(Order order){
        orders.add(order);
    }
    
    public void removeOrder(Order order){
        orders.remove(order);
    }
    
    public Set<Order> getOrders(){
        return orders;
    }
    
    public void addPayment(Payment payment){
        payments.add(payment);
    }
    
    public void removePayment(Payment payment){
        
    }
    
    public Set<Payment> getPayments(){
        return payments;
    }
    
    public boolean isRegisteredToOrder(long orderId){
        for(Order order: orders){
            if(order.getId() == orderId)
                return true;
        }
        return false;
    }
}
