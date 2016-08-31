/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.Payment;
import domain.Person;
import java.time.LocalDate;
import java.util.ArrayList;
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
public class PersonRepositoryTest {
    
     @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private static PersonRepository repository;
    private static OrderRepository orderRep;
    private static PaymentRepository paymentRep;
    private OrderBill order1;
    private OrderBill order2;
    private Payment payment1;
    private Payment payment2;
    private Payment payment3;
    private Person person_with_payment1_and_order1;
    private Person person_with_payment2_and_payment3;
    private Person person_with_order1;
     
    public PersonRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        repository = PersonRepositoryFactory.createPersonRepository("JPA");
        orderRep = OrderRepositoryFactory.createOrderRepository("JPA");
        paymentRep = PaymentRepositoryFactory.createPaymentRepository("JPA");
        
        repository.deleteAllPersons();
        orderRep.deleteAllOrders();
        paymentRep.deleteAllPayments();


    }
    
    @AfterClass
    public static void tearDownClass() {
        repository.closeConnection();
        paymentRep.closeConnection();
        orderRep.closeConnection();
    }
    
    //orders will not be linked to persons in the setup
    //this is to avoid inconsistencies, 
    //since persons should be added before orders are linked to them
    //Adding persons is one of the things that are tested, 
    //thus it should not be done in setUp, since it is not presumed to work as expected.
    @Before
    public void setUp() {
        order1 = new OrderBill(15, LocalDate.now());
        order2 = new OrderBill(13, LocalDate.now());
        orderRep.addOrder(order1);
        orderRep.addOrder(order2);
        
        //orderRep = OrderRepositoryFactory.createOrderRepository("JPA");
        //orderRep.addOrder(order1);
        
        payment1 = new Payment(12.5, LocalDate.now());
        payment2 = new Payment(13.9, LocalDate.now());
        payment3 = new Payment(5.63, LocalDate.now());
        //paymentRep.addPayment(payment1);
        //paymentRep.addPayment(payment2);
        //paymentRep.addPayment(payment3);
        
        person_with_order1 = new Person("Robert");
        //person_with_order1.addOrder(order1);   
        
        person_with_payment1_and_order1 = new Person("Pieter");
        //person_with_payment1_and_order1.addOrder(order1);
        //person_with_payment1_and_order1.addPayment(payment1);
        
        person_with_payment2_and_payment3 = new Person("July");
        //person_with_payment2_and_payment3.addPayment(payment2);
        //person_with_payment2_and_payment3.addPayment(payment3);
        
    }
    
    @After
    public void tearDown() {
        repository.deleteAllPersons();
        orderRep.deleteAllOrders();
        paymentRep.deleteAllPayments();
    }
   
    @Test
    public void addPerson_adds_given_person_to_stub() {
        repository.addPerson(person_with_payment2_and_payment3);
        person_with_payment2_and_payment3.addPayment(payment2);
        person_with_payment2_and_payment3.addPayment(payment3);
        long id = person_with_payment2_and_payment3.getId();
        Person person = repository.getPerson(id);
        assertEquals(person_with_payment2_and_payment3, person);
    }

    @Test
    public void updatePerson_updates_the_person_with_the_given_id_to_the_given_values() {
        repository.addPerson(person_with_order1);
        long id = person_with_order1.getId();
        
        String newName = "Emma";
        
        repository.updatePerson(id, newName);
        Person person = repository.getPerson(id);
        assertEquals(newName, person.getName());
    }

    @Test
    public void testDeletePerson_removes_person_with_given_id_from_stub() {
        repository.addPerson(person_with_order1);
        repository.addPerson(person_with_payment1_and_order1);
       person_with_order1.addOrder(order1);
       repository.deletePerson(person_with_order1.getId());
        assertFalse(repository.getAllPersons().contains(person_with_order1));
    }

    @Test
    public void getPaymentsForPerson_returns_all_payments_for_a_given_person() {
        List<Payment> expectedList = new ArrayList<>();
        expectedList.add(payment2);
        expectedList.add(payment3);
        repository.addPerson(person_with_payment2_and_payment3);
        person_with_payment2_and_payment3.addPayment(payment2);
        person_with_payment2_and_payment3.addPayment(payment3);
        List<Payment> paymentList = repository.getPaymentsForPerson(person_with_payment2_and_payment3.getId());
        assertTrue(paymentList.containsAll(expectedList) && expectedList.containsAll(paymentList));
    }
}
