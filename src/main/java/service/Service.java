/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import domain.OrderBill;
import domain.Payment;
import domain.Person;
import domain.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * This domain is used to keep track of 
 * all the (bread-)orders and the (partial) payments for these orders 
 * that are made for the different people participating.
 * 
 * The cost for an order is divided equally between each person.
 * 
 * For now the given dates on the orders and payments are not taken into account.
 * @author Wouter
 */
public interface Service {

    
    
    /*adds a person to keep track of / store */
    public void addPerson(Person person);
    
    /*returns all the persons that are stored*/
    public Set<Person> getAllPersons();
    
    /*returns the person with the given name*/
    public Person getPerson(long id);
    public Person getPerson(String id);
    
    /*adds an order for the given group of people*/
    public void addOrder(OrderBill order, Set<Person> persons);
    
    /*Switching the order is hacky - reason:
        - because of erasure just switching isn't possible
        - because person references order and not the other way around
            this can't be refractored to the OrderBill constructor as of now*/
    public void addOrder(Set<Long> personIds, OrderBill order);
    
    public OrderBill getOrder(long orderId);
    
    public void updateOrder(long orderId,double newCost, LocalDate newDate);
    
    public void deleteOrder(long orderId);
        
    /*returns all the orders that are stored*/
    public List<OrderBill> getAllOrders();
    
    /*adds a (partial) payment made by a person for his orders*/
    public void addPersonPayment(Person person, Payment payement);
    
    /*returns the total amount this person has payed up until now for his orders*/
    public double getPersonTotalPayment(Person person);
    
    /*returns all the expenses this person has made through his orders*/
    public double getPersonTotalOrderExpenses(Person person);
    
    /*returs the group of people whom have made a certain order*/
    public Set<Person> getPersonsWithOrder(long orderId);
    
    public List<Transaction> getSortedTransactionsForPerson(long personId);
    
    public void deleteAll();
    
    public void closeConnections();
}
