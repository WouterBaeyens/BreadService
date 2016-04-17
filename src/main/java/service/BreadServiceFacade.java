/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.OrderRepository;
import database.PaymentRepository;
import database.PersonRepository;
import domain.Order;
import domain.Payment;
import domain.Person;
import database.ServiceRepositoryFactory;
import database.ServiceRepositoryInterface;
import java.time.LocalDateTime;
import java.util.Date;
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
public class BreadServiceFacade implements Service{

    private OrderService orderService;
    private PersonService personService;
    private PaymentService paymentService;
    
    public BreadServiceFacade(String repositoryType){
        orderService = new OrderService(repositoryType);
        personService = new PersonService(repositoryType);
        paymentService = new PaymentService(repositoryType);
    }
    
    @Deprecated
    public BreadServiceFacade(ServiceRepositoryInterface serviceRepository){
        throw new UnsupportedOperationException("You tried to access the BreadServiceFacade constructor, which should not be used.");
    }
    
    
    
    /*adds a person to keep track of / store */
    public void addPerson(Person person){
        personService.addPerson(person);
        
    }
    
    /*returns the person with the given name*/
    public Person getPerson(long id){
        return personService.getPerson(id);
    }
    
    /*returns all the persons that are stored*/
    public Set<Person> getAllPersons(){
        return personService.getAllPersons();
    }
    
    
    /*adds an order for the given group of people*/
    public void addOrder(Order order, Set<Person> persons){
        orderService.addOrder(order);
        for(Person person:persons){
            person.addOrder(order);
        }
    }
    
    public Order getOrder(long orderId){
        return orderService.getOrder(orderId);
    }
    
    public void updateOrder(long orderId,double newCost, LocalDateTime newDate){
        orderService.updateOrder(orderId, newCost, newDate);
    }
    
    public void deleteOrder(long orderId){
        orderService.deleteOrder(orderId);
    }
    
    public List<Order> getAllOrders(){
        return orderService.getAllOrders();
    }
        
    /*adds a (partial) payment made by a person for his orders*/
    public void addPersonPayment(Person person, Payment payement){
        person.addPayment(payement);
    }
    
    /*returns the total amount this person has payed up until now for his orders*/
    public double getPersonTotalPayment(Person person){
        double totalPayment = 0;
        Set<Payment> payments = person.getPayments();
        for(Payment payment: payments){
            totalPayment += payment.getAmount();
        }
        return totalPayment;
    }
    
    /*returns all the expenses this person has made through his orders*/
    public double getPersonTotalOrderExpenses(Person person){
        double totalExpenses = 0;
        Set<Order> orders = person.getOrders();
        for(Order order: orders){
            totalExpenses += order.getCostPerPerson();
        }
        return totalExpenses;
    }
    
    /*returs the group of people whom have made a certain order*/
    public Set<Person> getPersonsWithOrder(long orderId){
        return personService.getPersonsWithOrder(orderId);
    }
    
}
