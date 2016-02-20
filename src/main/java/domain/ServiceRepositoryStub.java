/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class ServiceRepositoryStub implements ServiceRepositoryInterface{

    private List<PersonOrderRelation> personOrderRelations;
    private Set<Person> persons;
    
    public ServiceRepositoryStub(){
        persons = new HashSet<>();
        personOrderRelations = new ArrayList<>();
    }
    
    @Override
    public void addPerson(Person person) {
        persons.add(person);
    }

    @Override
    public Set<Person> getAllPersons(){
        return persons;
    }

    @Override
    public Set<Person> getAllPersonsForOrder(Order order) {
        Set<Person> allFoundPersons = new HashSet<>();
        for(PersonOrderRelation personOrderRelation : personOrderRelations){
            if(personOrderRelation.getOrder().equals(order)){
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
