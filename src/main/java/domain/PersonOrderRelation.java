/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Wouter
 */
public class PersonOrderRelation {
    private Person person;
    private Order order;
    
    public PersonOrderRelation(Person person, Order order){
        setPerson(person);
        setOrder(order);
    }
    

    private void setPerson(Person person) {
        this.person = person;
    }
    
    public Person getPerson(){
        return person;
    }

    
    private void setOrder(Order order) {
        this.order = order;
    }
    
    public Order getOrder(){
        return order;
    }
    
    
    
}
