/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

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
public class ServletFacade {

    private ServiceRepositoryInterface serviceRepository;
    
    public ServletFacade(String repositoryType){
        serviceRepository = ServiceRepositoryFactory.createServiceRepository(repositoryType);
    }
    
    /*adds a person to keep track of / store */
    public void addPerson(Person person){
        serviceRepository.addPerson(person);
    }
    
    /*returns all the persons that are stored*/
    public Set<Person> getAllPersons(){
        return serviceRepository.getAllPersons();
    }
    
    /*adds an order for the given group of people*/
    public void addOrder(Order order, Set<Person> persons){
        serviceRepository.addOrder(order, persons);
    }
        
    /*adds a (partial) payment made by a person for his orders*/
    public void addPersonPayment(Person person, Payment payement){
        serviceRepository.addPaymentForPerson(person, payement);
    }
    
    /*returns the total amount this person has payed up until now for his orders*/
    public double getPersonTotalPayment(Person person){
        double totalPayment = 0;
        Set<Payment> payments = serviceRepository.getPaymentsForPerson(person);
        for(Payment payment: payments){
            totalPayment += payment.getAmount();
        }
        return totalPayment;
    }
    
    /*returns all the expenses this person has made through his orders*/
    public double getPersonTotalOrderExpenses(Person person){
        double totalExpenses = 0;
        Set<Order> orders = serviceRepository.getAllOrdersForPerson(person);
        for(Order order: orders){
            totalExpenses += order.getCostPerPerson();
        }
        return totalExpenses;
    }
    
    /*returs the group of people whom have made a certain order*/
    public Set<Person> getPersonsWithOrder(Order order){
        return serviceRepository.getAllPersonsForOrder(order);
    }
    
}
