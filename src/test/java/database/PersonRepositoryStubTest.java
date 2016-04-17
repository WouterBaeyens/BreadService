/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Order;
import domain.Payment;
import domain.Person;
import java.time.LocalDateTime;
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
public class PersonRepositoryStubTest {
    
     @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private PersonRepositoryStub stub;
    private Order order1;
    private Payment payment1;
    private Payment payment2;
    private Person person_with_payment1_and_order1;
    private Person person_with_payment1_and_payment2;
    private Person person_with_order1;
     
    public PersonRepositoryStubTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        stub = new PersonRepositoryStub();
        order1 = new Order(15, LocalDateTime.now());
        payment1 = new Payment(12.5, LocalDateTime.now());
        payment2 = new Payment(13.9, LocalDateTime.now());
        person_with_payment1_and_order1 = new Person("Pieter");
        person_with_payment1_and_order1.addOrder(order1);
        person_with_payment1_and_order1.addPayment(payment1);
        person_with_order1 = new Person("Robert");
        person_with_order1.addOrder(order1);
        person_with_payment1_and_payment2 = new Person("July");
        person_with_payment1_and_payment2.addPayment(payment1);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class PersonRepositoryStub.
     */
    @Test
    public void addPerson_adds_given_person_to_stub() {
        stub.addPerson(person_with_order1);
        long id = person_with_order1.getId();
        Person person = stub.getPerson(id);
        
        assertEquals(person_with_order1, person);
    }

    /**
     * Test of updatePerson method, of class PersonRepositoryStub.
     */
    @Test
    public void updatePerson_updates_the_person_with_the_given_id_to_the_given_values() {
        stub.addPerson(person_with_order1);
        long id = person_with_order1.getId();
        
        String newName = "Emma";
        
        stub.updatePerson(id, newName);
        Person person = stub.getPerson(id);
        assertEquals(newName, person.getName());
    }

    /**
     * Test of deletePerson method, of class PersonRepositoryStub.
     */
    @Test
    public void testDeletePerson_removes_person_with_given_id_from_stub() {
        stub.addPerson(person_with_order1);
        long id = person_with_order1.getId();
        
        stub.deletePerson(id);
        assertFalse(stub.getAllPersons().contains(person_with_order1));
    }

    /**
     * Test of getAllPersonsForOrder method, of class PersonRepositoryStub.
     */
    @Test
    public void getAllPersonsForOrder_returns_all_persons_with_the_order_with_given_id() {
        Set<Person> expectedSet = new HashSet<>();
        expectedSet.add(person_with_order1);
        expectedSet.add(person_with_payment1_and_order1);
        stub.addPerson(person_with_order1);
        stub.addPerson(person_with_payment1_and_payment2);
        stub.addPerson(person_with_payment1_and_order1);
        Set<Person> personSet = stub.getAllPersonsForOrder(order1.getId());
        assertEquals(expectedSet, personSet);
    }

    /**
     * Test of getPaymentsForPerson method, of class PersonRepositoryStub.
     */
    @Test
    public void getPaymentsForPerson_returns_all_payments_for_a_given_person() {
        List<Payment> expectedList = new ArrayList<>();
        expectedList.add(payment1);
        expectedList.add(payment2);
        stub.addPerson(person_with_payment1_and_payment2);
        List<Payment> paymentList = stub.getPaymentsForPerson(person_with_payment1_and_payment2.getId());
        assertTrue(paymentList.containsAll(expectedList) && expectedList.containsAll(paymentList));
    }
    
}
