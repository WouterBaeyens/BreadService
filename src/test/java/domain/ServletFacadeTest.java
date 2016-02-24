/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

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

/**
 *
 * @author Wouter
 */
public class ServletFacadeTest {
    
    private ServletFacade facade;
    private Person genericPerson;
    private Set<Person> persons;
    private Payment genericPayement_with_past_date;
    private Payment genericPayement_with_past_date2;
    private Order genericOrder_with_past_date;
    private Order genericOrder_with_past_date2;
    
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
        facade = new ServletFacade("stub");
        genericPerson = new Person("Jan");
        persons = new HashSet<>();
        persons.add(new Person("Piet"));
        persons.add(new Person("Joris"));
        genericPayement_with_past_date = new Payment(3, LocalDateTime.now());
         genericPayement_with_past_date2 = new Payment(5, LocalDateTime.now());
        genericOrder_with_past_date = new Order(5, LocalDateTime.now());
        genericOrder_with_past_date2 = new Order(10, LocalDateTime.now());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class ServletFacade.
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
        facade.addOrder(genericOrder_with_past_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(genericOrder_with_past_date.getId());
        assertTrue(personsWithOrder.containsAll(persons));
    }
    
        @Test
    public void getOrder_should_return_the_order_with_the_given_id(){
        facade.addOrder(genericOrder_with_past_date, persons);
        long orderId = genericOrder_with_past_date.getId();
            assertEquals(genericOrder_with_past_date, facade.getOrder(orderId));
    }

    @Test
    public void addPersonPayment_should_link_the_payment_to_the_given_person() {
        facade.addPersonPayment(genericPerson, genericPayement_with_past_date);
        assertTrue(genericPerson.getPayments().contains(genericPayement_with_past_date));
    }

    /**
     * Test of getPersonTotalPayment method, of class ServletFacade.
     */
    @Test
    public void getPersonTotalPayment_should_return_the_total_amount_payed_by_the_given_person() {        
        facade.addPersonPayment(genericPerson, genericPayement_with_past_date);
        facade.addPersonPayment(genericPerson, genericPayement_with_past_date2);
        double expectedPayment = genericPayement_with_past_date.getAmount() + genericPayement_with_past_date2.getAmount();
        double actualPaymentResult = facade.getPersonTotalPayment(genericPerson);
        assertEquals(expectedPayment, actualPaymentResult, 0.000001);

    }

    /**
     * Test of getPersonTotalOrderExpenses method, of class ServletFacade.
     */
    @Test
    public void getPersonTotalOrderExpenses_should_return_the_total_cost_for_the_given_person() {
        persons.add(genericPerson);
        facade.addOrder(genericOrder_with_past_date, persons);
        facade.addOrder(genericOrder_with_past_date2, persons);
        double expectedExpenses = genericOrder_with_past_date.getCostPerPerson() + genericOrder_with_past_date2.getCostPerPerson();
        double actualExpenses = facade.getPersonTotalOrderExpenses(genericPerson);
        assertEquals(expectedExpenses, actualExpenses, 0.000001);
    }
    
    @Test
    public void updateOrder_should_update_the_date_and_cost_for_the_order_with_the_given_id(){
        facade.addOrder(genericOrder_with_past_date, persons);
        long orderId = genericOrder_with_past_date.getId();
        LocalDateTime newDate = LocalDateTime.now();
        double newCostPerPerson = 15.3;
        facade.updateOrder(orderId, newDate, newCostPerPerson);
        assertEquals(newDate, genericOrder_with_past_date.getDate());
        assertEquals(newCostPerPerson, genericOrder_with_past_date.getCostPerPerson(), 0.000001);
    }
    
    @Test
    public void deleteOrder_should_remove_the_order_and_all_its_relations(){
        facade.addOrder(genericOrder_with_past_date, persons);
        long orderId = genericOrder_with_past_date.getId();
        facade.deleteOrder(orderId);
        assertNull(facade.getOrder(orderId));
        
}
   
    
    
}
