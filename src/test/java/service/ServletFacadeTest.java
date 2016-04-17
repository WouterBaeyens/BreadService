/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import domain.DomainException;
import domain.Order;
import domain.Payment;
import domain.Person;
import service.BreadServiceFacade;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
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
    
    private BreadServiceFacade facade;
    private Person genericPerson;
    private Set<Person> persons;
    private Payment validPayement_with_current_date;
    private Payment validPayement_with_current_date2;
    private Order validOrder_with_current_date;
    private Order validOrder_with_current_date2;
    
    public ServletFacadeTest() {
    }
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        facade = new BreadServiceFacade("stub");
        genericPerson = new Person("Jan");
        persons = new HashSet<>();
        persons.add(new Person("Piet"));
        persons.add(new Person("Joris"));
        validPayement_with_current_date = new Payment(3, LocalDateTime.now());
         validPayement_with_current_date2 = new Payment(5, LocalDateTime.now());
        validOrder_with_current_date = new Order(LocalDateTime.now(), 5);
        validOrder_with_current_date2 = new Order(LocalDateTime.now(), 10);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class BreadServiceFacade.
     */
    @Test
    public void addPerson_voegt_nieuwe_persoon_toe() {
        facade.addPerson(genericPerson);
        assertTrue(facade.getAllPersons().contains(genericPerson));
    }
    
    @Test
    public void addOrder_should_link_the_given_order_to_all_the_given_persons() {
        //registreer elke persoon uit de set
        for(Person person: persons){
            facade.addPerson(person);
        }
        //add de order, en geef de personen mee.
        facade.addOrder(validOrder_with_current_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(validOrder_with_current_date.getId());
        assertTrue(personsWithOrder.containsAll(persons));
    }
    
        @Test
    public void getOrder_should_return_the_order_with_the_given_id(){
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
            assertEquals(validOrder_with_current_date, facade.getOrder(orderId));
    }

    @Test
    public void addPersonPayment_should_link_the_payment_to_the_given_person() {
        facade.addPersonPayment(genericPerson, validPayement_with_current_date);
        assertTrue(genericPerson.getPayments().contains(validPayement_with_current_date));
    }

    /**
     * Test of getPersonTotalPayment method, of class BreadServiceFacade.
     */
    @Test
    public void getPersonTotalPayment_should_return_the_total_amount_payed_by_the_given_person() {        
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
        facade.addOrder(validOrder_with_current_date, persons);
        facade.addOrder(validOrder_with_current_date2, persons);
        double expectedExpenses = validOrder_with_current_date.getCostPerPerson() + validOrder_with_current_date2.getCostPerPerson();
        double actualExpenses = facade.getPersonTotalOrderExpenses(genericPerson);
        assertEquals(expectedExpenses, actualExpenses, 0.000001);
    }
    
    @Test
    public void updateOrder_should_update_the_date_and_cost_for_the_order_with_the_given_id(){
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
        LocalDateTime newDate = LocalDateTime.now();
        double newTotalCost = 15.3;
        facade.updateOrder(orderId, newTotalCost, newDate);
        assertEquals(newDate, validOrder_with_current_date.getDate());
        assertEquals(newTotalCost, validOrder_with_current_date.getTotalCost(), 0.000001);
    }
    
    @Test
    public void payment_constructor_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        exception.expect(DomainException.class);
        Payment payment = new Payment(amount, LocalDateTime.now());
    }
    
    //note: only the delete part is tested for now, and the relations will
    //      stay intact until jpa is further configured.
    @Test
    public void deleteOrder_should_remove_the_order_and_all_its_relations(){
        facade.addOrder(validOrder_with_current_date, persons);
        long orderId = validOrder_with_current_date.getId();
        facade.deleteOrder(orderId);
        assertFalse(facade.getAllOrders().contains(validOrder_with_current_date));
        
}
   
    
    
}
