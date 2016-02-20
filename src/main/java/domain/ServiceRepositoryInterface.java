/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.Date;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface ServiceRepositoryInterface {
    
    /*register a new person (=add a new person to the repository)*/
    public void addPerson(Person person);

    /*Returns all the persons that are registered (=currently stored on the repository)*/
    public Set<Person> getAllPersons();
    
    /*Returns all the persons linked to the given order*/
    public Set<Person> getAllPersonsForOrder(long orderId);
    


    
    /*Links the order to all the participating persons
     (also adds the persons (addPerson(person)) if they aren't added yet*/
    public void addOrder(Order order, Set<Person> persons);
    
    /*Returns the order based on it's id*/
    public Order getOrder(long orderId);
    
    /*Deletes the order and all it's refernces to the persons it is assigned to*/
    public void deleteOrder(long orderId);
    
    
    public void updateOrder(long orderId, Date newDate, double newCostPerPerson);
    
    /*Returns all the orders that have been set so far*/
    public Set<Order> getAllOrders();
    
    /*Returns all the orders linked to that person*/
    public Set<Order> getAllOrdersForPerson(Person person);
    
    
    
    
    /*Registers the payment to cover the expenses the person has made through orders*/
    public void addPaymentForPerson(Person person, Payment payment);
    
    public Set<Payment> getAllPayments();
    
    /*Returns all the payments for a person*/
    public Set<Payment> getPaymentsForPerson(Person person);
        
}