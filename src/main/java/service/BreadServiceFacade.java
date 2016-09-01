/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.OrderRepository;
import database.PaymentRepository;
import database.PersonRepository;
import domain.OrderBill;
import domain.Payment;
import domain.Person;
import database.ServiceRepositoryInterface;
import domain.Transaction;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javafx.util.converter.LocalDateTimeStringConverter;

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
        
        deleteAll();
        fillDatabaseWithFakeData();
    }
    
    @Override
    public void deleteAll(){
        personService.deleteAllPersons();
        orderService.deleteAllOrders();
        paymentService.deleteAllPayments();
    }
    
    private void fillDatabaseWithFakeData(){
        OrderBill order1 = new OrderBill(13.15, LocalDate.of(2016, Month.MARCH, 3));
        OrderBill order2 = new OrderBill(15.12, LocalDate.of(2016, Month.MARCH, 15));
        Payment payment1 = new Payment(12, LocalDate.of(2016, Month.FEBRUARY, 5));
        Payment payment2 = new Payment(5, LocalDate.of(2016, Month.MARCH, 8));
        Payment payment3 = new Payment(20, LocalDate.now());
        
        Person person1 = new Person("Joris Houteven");
        Person person2 = new Person("Minyan");
        Person person3 = new Person("Alexander");
        Person person4 = new Person("Charlotte Luckx");
        Person person5 = new Person("Rani");
        Person person6 = new Person("Wouter");
        
        Set<Person> persons = new HashSet<>();
        persons.add(person1);
        persons.add(person2);
        persons.add(person3);
        persons.add(person4);
        persons.add(person5);
        persons.add(person6);

        addPerson(person1);
        addPerson(person2);
        addPerson(person3);
        addPerson(person4);
        addPerson(person5);
        addPerson(person6);
        
        addOrder(order1, persons);
        
        persons.remove(person4);
        persons.remove(person2);
        
        addOrder(order2, persons);
        
        addPersonPayment(person1, payment1);
        addPersonPayment(person1, payment2);
        addPersonPayment(person5, payment3);  
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
    
    public Person getPerson(String id){
        return getPerson(Long.parseLong(id));
    }
    
    /*returns all the persons that are stored*/
    public Set<Person> getAllPersons(){
        return personService.getAllPersons();
    }
    
    
    /*adds an order for the given group of people
     *Todo: remove this function in the long run when all uses are refractored to use the method below 
    */
    public void addOrder(OrderBill order, Set<Person> persons){
        orderService.addOrder(order);
        for(Person person:persons){
            person.addOrder(order);
        }
    }
    
        /*adds an order for the given group of people*/
    public void addOrder(Set<Long> personIds, OrderBill order){
        orderService.addOrder(order);
        for(Long id:personIds){
            getPerson(id).addOrder(order);
        }
    }
    
    public OrderBill getOrder(long orderId){
        return orderService.getOrder(orderId);
    }
    
    public void updateOrder(long orderId,double newCost, LocalDate newDate){
        orderService.updateOrder(orderId, newCost, newDate);
    }
    
    public void deleteOrder(long orderId){
        orderService.deleteOrder(orderId);
    }
    
    public List<OrderBill> getAllOrders(){
        return orderService.getAllOrders();
    }
        
    /*adds a (partial) payment made by a person for his orders*/
    public void addPersonPayment(Person person, Payment payement){
        //paymentService.addPayment(payement);
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
        Set<OrderBill> orders = person.getOrders();
        for(OrderBill order: orders){
            totalExpenses += order.getCostPerPerson();
        }
        return totalExpenses;
    }
    
    /*returs the group of people whom have made a certain order*/
    public Set<Person> getPersonsWithOrder(long orderId){
        return orderService.getPersonsWithOrder(orderId);
    }

    @Override
    public List<Transaction> getSortedTransactionsForPerson(long personId) {
        return personService.getSortedTransactionsForPerson(personId);
    }

    @Override
    public void closeConnections() {
        orderService.closeConnection();
        paymentService.closeConnection();
        personService.closeConnection();
    }
    
}
