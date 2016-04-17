/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Order;
import domain.Payment;
import domain.Person;
import domain.PersonOrderRelation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class ServiceRepositoryStub implements ServiceRepositoryInterface{

    private List<PersonOrderRelation> personOrderRelations;
    //no seperate order-set exists, since orders can't exist without the people making them.
    private Set<Person> persons;
    
    public ServiceRepositoryStub(){
        persons = new HashSet<Person>();
        Person p1 = new Person("Pieter Laermans");
        p1.addPayment(new Payment(13.12, LocalDateTime.MAX));
        p1.addPayment(new Payment(15.13, LocalDateTime.MAX));
        persons.add(p1);
        persons.add(new Person("Charlotte Luckx"));
        
        personOrderRelations = new ArrayList<>();
    }
    
    @Override
    public void addPerson(Person person) {
        persons.add(person);
    }
    
    @Override
    public Person getPerson(long id) {
        for(Person person: persons){
            if(person.getId() == id)
                return person;
        }
        return null;
    }

    @Override
    public Set<Person> getAllPersons(){
        return persons;
    }

    @Override
    public Set<Person> getAllPersonsForOrder(long orderId) {
        Set<Person> allFoundPersons = new HashSet<>();
        for(PersonOrderRelation personOrderRelation : personOrderRelations){
            if(personOrderRelation.getOrder().getId() == orderId){
                allFoundPersons.add(personOrderRelation.getPerson());
            }
        }
        return allFoundPersons;
    }
    
    
    
    
    @Override
    public void addOrder(Order order, Set<Person> personGroup) {
            for(Person person: personGroup){
                addPerson(person);
                personOrderRelations.add(new PersonOrderRelation(person, order));
            }
        }  
    
    @Override
    public Order getOrder(long orderId){
        for(PersonOrderRelation personOrderRelation : personOrderRelations){
            if(personOrderRelation.getOrder().getId() == orderId){
                return personOrderRelation.getOrder();
            }
        }
        return null;
    }
    
    @Override
    public void deleteOrder(long orderId){
        for (Iterator<PersonOrderRelation> iterator = personOrderRelations.iterator(); iterator.hasNext();) {
            PersonOrderRelation personOrderRelation = iterator.next();
            if (personOrderRelation.getOrder().getId() == orderId) {
            iterator.remove();
            }
        }
    }
    
    @Override
    public void updateOrder(long orderId, LocalDateTime newDate, double newCostPerPerson){
         Order order = getOrder(orderId);
         order.setCostPerPerson(newCostPerPerson);
         order.setDate(newDate);   
    }
    
    @Override
    public Set<Order> getAllOrders() {
        Set<Order> resultSet = new HashSet<>();
        for(PersonOrderRelation personOrderRelation : personOrderRelations){
            resultSet.add(personOrderRelation.getOrder());
        }
        return resultSet;
    }
    
        @Override
    public Set<Order> getAllOrdersForPerson(Person person) {
        Set<Order> orders = new HashSet<>();
        for(PersonOrderRelation personWithOrder : personOrderRelations){
            if(personWithOrder.getPerson().equals(person)){
                orders.add(personWithOrder.getOrder());
            }
        }
        return orders;
    }
    
    
    
    
    
    @Override
    public void addPaymentForPerson(Person person, Payment payement) {
        person.addPayment(payement);
    }
    
    @Override
    public Set<Payment> getAllPayments() {
        Set<Payment> payments = new HashSet<>();
        for(Person person: persons){
            payments.addAll(person.getPayments());
        }
        return payments;
    }

    @Override
    public Set<Payment> getPaymentsForPerson(Person person) {
        return person.getPayments();
    }
    
}
