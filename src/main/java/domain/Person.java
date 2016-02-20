/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class Person {
    
    private String name;
    private Set<Payment> payments;
    
    public Person(String name){
        setName(name);
        payments = new HashSet<Payment>();
    }

    private void setName(String name) {
        this.name = name;
    }
    
    
    
    public void addPayment(Payment payment){
        payments.add(payment);
    }
    
    public Set<Payment> getPayments(){
        return payments;
    }
}
