/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import domain.Payment;
import domain.Person;
import domain.Transaction;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Wouter
 */
public class BreadServiceHyperFacade implements Service{

    private OrderWeekService orderWeekService;
    private OrderService orderService;
    private PersonService personService;
    private PaymentService paymentService;
    
    public BreadServiceHyperFacade(String repositoryType){
        personService = new PersonService(repositoryType);
        orderService = new OrderService(repositoryType);
        orderWeekService = new OrderWeekService(repositoryType);
        paymentService = new PaymentService(repositoryType);
        
    }
    
    @Override
    public void addPerson(Person person) {
        personService.addPerson(person);
    }

    @Override
    public Set<Person> getAllPersons() {
        return personService.getAllPersons();
    }

    @Override
    public Person getPerson(long id) {
        return personService.getPerson(id);
    }

    @Override
    public Person getPerson(String id) {
        return getPerson(Long.parseLong(id));
    }

    @Override
    public void addOrder(OrderBill order, Set<Person> persons) {
          Set<Long> personIdList = persons.stream().map(o->o.getId()).collect(Collectors.toSet());
          addOrder(personIdList, order);
    }

    @Override
    public void addOrder(Set<Long> personIds, OrderBill order) {
        OrderWeekPK pk = order.getOrderPK();
        if(pk == null)
             throw new ServiceException("Err. adding order (" + order + ") can't add order without pk");
        OrderBill oldOrder = orderService.getOrder(pk.getWeekNr(), pk.getYearNr());
        if(oldOrder != null)
            throw new ServiceException("Err. adding order (" + order + ") an order (" + oldOrder + ") already exists");
        order.removeOrderWeek();
        OrderWeek week = orderWeekService.getWeek(pk.getWeekNr(), pk.getYearNr());
        if(!orderWeekService.isWeekInDb(pk.getWeekNr(), pk.getYearNr()))
            orderWeekService.addWeek(week);
        week.setOrder(order);
        orderService.addOrder(order);
        for(long id: personIds)
            order.addAuthor(personService.getPerson(id));
    }

    @Override
    public OrderBill getOrder(int week, int year) {
        return orderService.getOrder(week, year);
    }

    @Override
    public OrderBill getCurrentOrder() {
        return orderService.getCurrentOrder();
    }

    @Override
    public void updateOrder(int week, int year, double newCost) {
        orderService.updateOrder(week, year, newCost);
    }

    @Override
    public void deleteOrder(int week, int year) {
        OrderBill o = orderService.getOrder(week, year);
        personService.removeRelationsToOrder(o);
        orderService.deleteOrder(week, year);
    }

    @Override
    public List<OrderBill> getAllOrders() {
        return orderService.getAllOrders();
    }

    @Override
    public void addWeek(OrderWeek week) {
        orderWeekService.addWeek(week);
    }

    @Override
    public OrderWeek getWeek(int week, int year) {
        return orderWeekService.getWeek(week, year);
    }

    public boolean isWeekInDb(int week, int year){
        return orderWeekService.isWeekInDb(week, year);
    }
    
    @Override
    public OrderWeek getCurrentWeek() {
         LocalDate date = LocalDate.now();
        int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = date.get(WeekFields.ISO.weekBasedYear());
        return getWeek(week, year);    }

    @Override
    public List<OrderWeek> getAllAcademicYearDates() {
            return orderWeekService.getAllAcademicYearDates();
    }

    @Override
    public void addPersonPayment(Person person, Payment payement) {
        person.addPayment(payement);
        personService.updatePerson(person);
    }

    @Override
    public double getPersonTotalPayment(Person person) {
        double totalPayment = 0;
        Set<Payment> payments = person.getPayments();
        for(Payment payment: payments){
            totalPayment += payment.getAmount();
        }
        return totalPayment;
    }

    @Override
    public double getPersonTotalOrderExpenses(Person person) {
        double totalExpenses = 0;
        Set<OrderBill> orders = person.getOrders();
        for(OrderBill order: orders){
            totalExpenses += order.getCostPerPerson();
        }
        return totalExpenses;
    }

    @Override
    public Set<Person> getPersonsWithOrder(int week, int year) {
        return orderService.getPersonsWithOrder(week, year);
    }

    @Override
    public List<Transaction> getSortedTransactionsForPerson(long personId) {
        return personService.getSortedTransactionsForPerson(personId);
    }

    @Override
    public void deleteAll() {
        List<OrderBill> orders = getAllOrders();
        for(OrderBill o: orders)
            personService.removeRelationsToOrder(o);
        
        orderService.deleteAllOrders();
        personService.deleteAllPersons();
        orderWeekService.deleteAllWeeks();
    }

    @Override
    public void closeConnections() {
        orderService.closeConnection();
        paymentService.closeConnection();
        personService.closeConnection();    }
    
    private void clear(){
        orderWeekService.clearManager();
        orderService.clearManager();
        personService.clearManager();
    }
}
