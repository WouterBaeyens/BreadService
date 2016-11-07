/*
 * TODO: REFRESH ORDER!!!!!!!!!! :(
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import domain.DomainException;
import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import domain.Payment;
import domain.Person;
import domain.Transaction;
import service.BreadServiceFacade;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Rule;
import org.junit.rules.ExpectedException;

/**
 *
 * @author Wouter
 */
public class ServletFacadeTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private static Service facade;
    private Person genericPerson;
    private Person genericPerson2;
    private Person genericPerson3;
    private List<Person> persons;
    private List<Long> personIds;
    private Payment validPayement_with_current_date;
    private Payment validPayement_with_past_date;
    private OrderBill validOrder_with_current_date;
    private OrderBill validOrder_with_past_date;
    
    public ServletFacadeTest() {
    }
    
    
    @BeforeClass
    public static void setUpClass() {
        facade = new BreadServiceHyperFacade("test");
        //facade = new BreadServiceFacade("JPA");
        System.out.println("Deleting ALL");
        facade.deleteAll();

    }
    
    @AfterClass
    public static void tearDownClass() {
        facade.closeConnections();
    }
    
    @Before
    public void setUp() {
        genericPerson = new Person("Jan");
        genericPerson2 = new Person("Piet");
        genericPerson3 = new Person("Joris");
        persons = new ArrayList<>();
        persons.add(genericPerson2);
        persons.add(genericPerson3);
        validPayement_with_current_date = new Payment(3, LocalDate.now());
         validPayement_with_past_date = new Payment(5, LocalDate.now().minusWeeks(86));
        validOrder_with_current_date = new OrderBill(5, LocalDate.now());
        validOrder_with_past_date = new OrderBill(10, LocalDate.now().minusWeeks(95));
    }
    
    @After
    public void tearDown() {
        facade.deleteAll();
    }

    /**
     * Test of addPerson method, of class BreadServiceFacade.
     */
    @Test
    public void addPerson_voegt_nieuwe_persoon_toe() {
        System.out.println("Test: addPerson_adds_person");
        facade.addPerson(genericPerson);
        assertTrue(facade.getAllPersons().contains(genericPerson));
    }
    
    @Test
    public void addOrder_should_link_the_given_order_to_all_the_given_persons() {        
        System.out.println("Test: addOrder_should_link_persons");
        Set<Person> personSet = new HashSet<>();
        for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }        
        facade.addOrder(validOrder_with_current_date, personSet);
        System.out.println("Order added");
        facade.getAllOrders();
        //facade.getAllPersons();
//check of aan elke persoon de meegegeven order gekoppeld is.
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(validOrder_with_current_date.getWeekNr(), validOrder_with_current_date.getYearNr());
        for(Person person : persons){
        assertTrue(personsWithOrder.stream().anyMatch(p->p.getId() == person.getId()));
                }
    }
    
        @Test
    public void getOrder_should_return_the_order_with_the_given_id(){
            System.out.println("getOrder_gets_order");
        Set<Person> personSet = new HashSet<>();
            for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }
        validOrder_with_current_date.setTotalCost(9001);
        OrderWeekPK pk = validOrder_with_current_date.getOrderPK();
        OrderWeek week = facade.getWeek(pk.getWeekNr(), pk.getYearNr());
        facade.addOrder(validOrder_with_current_date, personSet);
        
            //assertEquals(validOrder_with_current_date, facade.getOrder(pk.getWeekNr(), pk.getYearNr()));
    }

    //This instance of payment is not persisted, but rather copied
    @Test
    public void addPersonPayment_should_link_the_payment_to_the_given_person() {
        facade.addPerson(genericPerson);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        assertEquals(facade.getPerson(genericPerson.getId()).getPayments().size(), 1);
        //assertTrue(genericPerson.getPayments().contains(validPayement_with_current_date));
    }

    /**
     * Test of getPersonTotalPayment method, of class BreadServiceFacade.
    */
    @Test
    public void getPersonTotalPayment_should_return_the_total_amount_payed_by_the_given_person() {        
        facade.addPerson(genericPerson);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        facade.addPersonPayment(genericPerson, validPayement_with_past_date);
        double expectedPayment = validPayement_with_current_date.getAmount() + validPayement_with_past_date.getAmount();
        double actualPaymentResult = facade.getPersonTotalPayment(genericPerson);
        assertEquals(expectedPayment, actualPaymentResult, 0.000001);

    }

    /**
     * Test of getPersonTotalOrderExpenses method, of class BreadServiceFacade.
     */
    
    @Test
    public void getPersonTotalOrderExpenses_should_return_the_total_cost_for_the_given_person() {
        facade.addPerson(genericPerson);
        facade.addPerson(genericPerson2);
        facade.addPerson(genericPerson3);
        
        persons.add(genericPerson);
        Set<Long> idList = new HashSet<>();
        for(Person person: persons){
            //facade.addPerson(person);
            idList.add(person.getId());
        }
        
        //The switch is needed, since validOrder is not managed by orderRep,
        //so persons added to the list in the DB are not added to validOrder
        facade.addOrder(idList, validOrder_with_current_date);
        OrderBill addedOrder1 = facade.getOrder(validOrder_with_current_date.getWeekNr(), validOrder_with_current_date.getYearNr());
        assertEquals(idList.size(), addedOrder1.getAuthors().size());
        
        facade.addOrder(idList, validOrder_with_past_date);
        OrderBill addedOrder2 = facade.getOrder(validOrder_with_past_date.getWeekNr(), validOrder_with_past_date.getYearNr());
        assertEquals(idList.size(), addedOrder2.getAuthors().size());
        
        double expectedExpenses = addedOrder1.getCostPerPerson() + addedOrder2.getCostPerPerson();
        double actualExpenses = facade.getPersonTotalOrderExpenses(genericPerson);
        assertEquals(expectedExpenses, actualExpenses, 0.000001);
    }
    
    /*@Test
    public void updateOrder_should_update_the_date_and_cost_for_the_order_with_the_given_id(){
        Set<Person> personSet = new HashSet<>();
        for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }
        facade.addOrder(validOrder_with_current_date, personSet);
        OrderWeekPK pk = validOrder_with_current_date.getOrderPK();
        double newTotalCost = 15.3;
        facade.updateOrder(pk.getWeekNr(), pk.getYearNr(), newTotalCost);
        validOrder_with_current_date = facade.getOrder(pk.getWeekNr(), pk.getYearNr());
        assertEquals(newTotalCost, validOrder_with_current_date.getTotalCost(), 0.000001);
    }*/
    
    @Test
    public void payment_constructor_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        exception.expect(DomainException.class);
        Payment payment = new Payment(amount, LocalDate.now());
    }
    
    //note: only the delete part is tested for now, and the relations will
    //      stay intact until jpa is further configured.
    @Test
    public void deleteOrder_should_remove_the_order_and_all_its_relations(){
        Set<Person> personSet = new HashSet();
        for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }
        facade.addOrder(validOrder_with_current_date, personSet);
        OrderWeekPK pk = validOrder_with_current_date.getOrderPK();
        facade.deleteOrder(pk.getWeekNr(), pk.getYearNr());
        assertFalse(facade.getAllOrders().contains(validOrder_with_current_date));
        for(Person p: facade.getAllPersons()){
            assertFalse("Person has orders: " + p + "but order has no persons: " + validOrder_with_current_date, p.getOrders().contains(validOrder_with_current_date));
        }
}
    
  /*  @Test
    public void add_order_to_week_should_add_order_and_all_its_relations_to_the_week(){
        Set<Person> personSet = new HashSet();      
        for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }
        OrderWeek w = validOrder_with_current_date.getOrderWeek();
        System.out.println("will add week: " + w.getWeekNr() + " " + w.getYearNr());
        facade.addWeek(new OrderWeek(w.getWeekNr(), w.getYearNr()));
        //boolean testing = facade.isWeekInDb(w.getWeekNr(), w.getWeekNr());
        //assertTrue("unable to add week " +w.getWeekNr() + " " + w.getYearNr(), facade.isWeekInDb(w.getWeekNr(), w.getWeekNr()));
        facade.addOrder(validOrder_with_current_date, personSet);
        OrderWeekPK pk = validOrder_with_current_date.getOrderPK();
        
        OrderBill o = facade.getOrder(w.getWeekNr(), w.getYearNr());
        //assertTrue("week " +w.getWeekNr() + " " + w.getYearNr() +  " is not in Db after adding the order", facade.isWeekInDb(w.getWeekNr(), w.getWeekNr()));
        OrderWeek week = facade.getWeek(w.getWeekNr(), w.getYearNr());

        for(Person p: o.getAuthors()){
            assertTrue(p.getOrders().contains(o));
            OrderBill o2 = week.getOrderBill();
            assertTrue(o2.getAuthors().contains(p));
        }
        
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(validOrder_with_current_date.getWeekNr(), validOrder_with_current_date.getYearNr());
        for(Person person : persons){
            assertTrue("person [" + person +  "] has no link to order [" + o + "]", person.getOrders().contains(o));

            assertTrue(personsWithOrder.stream().anyMatch(p->p.getId() == person.getId()));
    }
    }
    /*
        @Test
    public void add_order_without_week_should_add_order_and_all_its_relations_to_a_new_week(){
                for(Person person: persons){
            facade.addPerson(person);
        }
        OrderWeek w = validOrder_with_current_date.getOrderWeek();
        //facade.addWeek(new OrderWeek(w.getWeekNr(), w.getYearNr()));
        facade.addOrder(validOrder_with_current_date, persons);
        OrderWeekPK pk = validOrder_with_current_date.getOrderPK();
        
        OrderBill o = facade.getOrder(w.getWeekNr(), w.getYearNr());
        for(Person p: o.getAuthors()){
            assertTrue(p.getOrders().contains(o));
        }
        
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(validOrder_with_current_date.getWeekNr(), validOrder_with_current_date.getYearNr());
        for(Person person : persons){
            assertTrue("person [" + person +  "] has no link to order [" + o + "]", person.getOrders().contains(o));

            assertTrue(personsWithOrder.stream().anyMatch(p->p.getId() == person.getId()));
    }
    }*/
    
    
    
      /*  @Test
    public void getSortedTransactoinsForPerson_returns_all_payments_and_orders_for_a_given_person(){
        persons.add(genericPerson);
        Set<Person> personSet = new HashSet();
        for(Person person: persons){
            facade.addPerson(person);
            personSet.add(person);
        }
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        facade.addPersonPayment(genericPerson, validPayement_with_past_date);
        
        facade.addOrder(validOrder_with_current_date, personSet);
        facade.addOrder(validOrder_with_past_date, personSet);
        
        List<Transaction> transactions = facade.getSortedTransactionsForPerson(genericPerson.getId());
        //since added orders are not managed by orderRep, they can't be used
        List<Transaction> expectedTransactions = new ArrayList(facade.getAllOrders());
        
        expectedTransactions.add(validPayement_with_current_date);
        expectedTransactions.add(validPayement_with_past_date);
            //assertTrue(transactions.containsAll(expectedTransactions));
        assertEquals(4, transactions.size());
    }*/
   
    
    
}
