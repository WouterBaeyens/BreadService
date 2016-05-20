/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import database.ServiceRepositoryStub;
import domain.OrderBill;
import domain.Payment;
import domain.Person;
import java.time.LocalDate;
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

/**
 *
 * @author Wouter
 */
public class ServiceRepositoryTest {
    
    private ServiceRepositoryStub repository;
    private Person genericPerson;
    private Set<Person> persons;
    private Payment genericPayement_with_current_date;
    private OrderBill genericOrder_with_current_date;
    
    public ServiceRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        repository = new ServiceRepositoryStub();
        genericPerson = new Person("Jan");
        persons = new HashSet<>();
        persons.add(new Person("Piet"));
        persons.add(new Person("Joris"));
        genericPayement_with_current_date = new Payment(3, LocalDate.now());
        genericOrder_with_current_date = new OrderBill(5,LocalDate.now());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class ServiceRepositoryStub.
     */
    @Test
    public void addPerson_voegt_nieuwe_persoon_toe() {
        repository.addPerson(genericPerson);
        assertTrue(repository.getAllPersons().contains(genericPerson));
    }

    /**
     * Test of addOrder method, of class ServiceRepositoryStub.
     */
    @Test
    public void AddOrder_voegt_nieuwe_order_toe_voor_elke_meegegeven_persoon() {
        //registreer elke persoon uit de set
        for(Person person: persons){
            repository.addPerson(person);
        }
        //add de order, en geef de personen mee.
        repository.addOrder(genericOrder_with_current_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        for(Person person:persons){
            assertTrue(repository.getAllOrdersForPerson(person).contains(genericOrder_with_current_date));
        }
    }

    /**
     * Test of addPaymentForPerson method, of class ServiceRepositoryStub.
     */
    @Test
    public void testAddPersonPayment_voegt_payment_toe_aan_de_meegegeven_persoon() {
        repository.addPaymentForPerson(genericPerson, genericPayement_with_current_date);
        assertTrue(genericPerson.getPayments().contains(genericPayement_with_current_date));
    }

   
    
}
