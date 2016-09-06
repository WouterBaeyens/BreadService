/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import domain.DomainException;
import domain.OrderBill;
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
    
    private static BreadServiceFacade facade;
    private Person genericPerson;
    private Person genericPerson2;
    private Person genericPerson3;
    private Set<Person> persons;
    private Set<Long> personIds;
    private Payment validPayement_with_current_date;
    private Payment validPayement_with_current_date2;
    private OrderBill validOrder_with_current_date;
    private OrderBill validOrder_with_current_date2;
    
    public ServletFacadeTest() {
    }
    
    
    @BeforeClass
    public static void setUpClass() {
        facade = new BreadServiceFacade("JPA");
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
        persons = new HashSet<>();
        persons.add(genericPerson2);
        persons.add(genericPerson3);
        validPayement_with_current_date = new Payment(3, LocalDate.now());
         validPayement_with_current_date2 = new Payment(5, LocalDate.now());
        validOrder_with_current_date = new OrderBill(5, LocalDate.now());
        validOrder_with_current_date2 = new OrderBill(10, LocalDate.now());
    }
    
    @After
    public void tearDown() {
        facade.deleteAll();
    }

    /**
     * Test of addPerson method, of class BreadServiceFacade.
     */
    /*@Test
    public void addPerson_voegt_nieuwe_persoon_toe() {
        facade.addPerson(genericPerson);
        assertTrue(facade.getAllPersons().contains(genericPerson));
    }*/
    
    @Test
    public void addOrder_should_link_the_given_order_to_all_the_given_persons() {
        //registreer elke persoon uit de set
        Person genericPerson2 = new Person("Piet");
        Person genericPerson3 = new Person("Joris");
        Set<Person> persons  = new HashSet<>();
        persons.add(genericPerson2);
        persons.add(genericPerson3);
        
        OrderBill  validOrder_with_current_date = new OrderBill(5, LocalDate.now());
        
        System.out.println("**START TEST** AddOrder with persons");
        for(Person person: persons){
            facade.addPerson(person);
        }
        System.out.println("**Step1** persons persisted in db.");
        //add de order, en geef de personen mee.
        
        facade.addOrder(validOrder_with_current_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(validOrder_with_current_date.getId());
        assertTrue(personsWithOrder.containsAll(persons));
        System.out.println("**End TEST** start cleaning");
    }
    
        @Test
    public void getOrder_should_return_the_order_with_the_given_id(){
        for(Person person: persons){
            facade.addPerson(person);
        }
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
            assertEquals(validOrder_with_current_date, facade.getOrder(orderId));
    }

    @Test
    public void addPersonPayment_should_link_the_payment_to_the_given_person() {
        facade.addPerson(genericPerson);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        assertTrue(genericPerson.getPayments().contains(validPayement_with_current_date));
    }

    /**
     * Test of getPersonTotalPayment method, of class BreadServiceFacade.
     */
    @Test
    public void getPersonTotalPayment_should_return_the_total_amount_payed_by_the_given_person() {        
        facade.addPerson(genericPerson);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date2);
        double expectedPayment = validPayement_with_current_date.getAmount() + validPayement_with_current_date2.getAmount();
        double actualPaymentResult = facade.getPersonTotalPayment(genericPerson);
        assertEquals(expectedPayment, actualPaymentResult, 0.000001);

    }

    /**
     * Test of getPersonTotalOrderExpenses method, of class BreadServiceFacade.
     */
    
    @Test
    public void getPersonTotalOrderExpenses_should_return_the_total_cost_for_the_given_person() {
        persons.add(genericPerson);
        for(Person person: persons){
            facade.addPerson(person);
        }
        facade.addOrder(validOrder_with_current_date, persons);
        facade.addOrder(validOrder_with_current_date2, persons);
        double expectedExpenses = validOrder_with_current_date.getCostPerPerson() + validOrder_with_current_date2.getCostPerPerson();
        double actualExpenses = facade.getPersonTotalOrderExpenses(genericPerson);
        assertEquals(expectedExpenses, actualExpenses, 0.000001);
    }
    
    @Test
    public void updateOrder_should_update_the_date_and_cost_for_the_order_with_the_given_id(){
        for(Person person: persons){
            facade.addPerson(person);
        }
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
        LocalDate newDate = LocalDate.now();
        int week = newDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        double newTotalCost = 15.3;
        facade.updateOrder(orderId, newTotalCost, newDate);
        assertEquals(week, validOrder_with_current_date.getWeek());
        assertEquals(newTotalCost, validOrder_with_current_date.getTotalCost(), 0.000001);
    }
    
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
        for(Person person: persons){
            facade.addPerson(person);
        }
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
        facade.deleteOrder(orderId);
        assertFalse(facade.getAllOrders().contains(validOrder_with_current_date));
        //todo: check relations
}
    /*
        @Test
    public void getSortedTransactoinsForPerson_returns_all_payments_and_orders_for_a_given_person(){
        persons.add(genericPerson);
        for(Person person: persons){
            facade.addPerson(person);
        }
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        facade.addPersonPayment(genericPerson, validPayement_with_current_date2);
        facade.addOrder(validOrder_with_current_date, persons);
        facade.addOrder(validOrder_with_current_date2, persons);
        List<Transaction> transactions = facade.getSortedTransactionsForPerson(genericPerson.getId());
        List<Transaction> expectedTransactoins = new ArrayList<>();
        expectedTransactoins.add(validOrder_with_current_date);
        expectedTransactoins.add(validOrder_with_current_date2);
        expectedTransactoins.add(validPayement_with_current_date);
        expectedTransactoins.add(validPayement_with_current_date2);
            assertTrue(transactions.containsAll(expectedTransactoins));
    }*/
   
    
    
}
