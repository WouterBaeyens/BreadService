/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

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
public class ServiceRepositoryStubTest {
    
    private ServiceRepositoryStub stub;
    private Person genericPerson;
    private Set<Person> persons;
    private Payment genericPayement_with_past_date;
    private Order genericOrder_with_past_date;
    
    public ServiceRepositoryStubTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        stub = new ServiceRepositoryStub();
        genericPerson = new Person("Jan");
        persons = new HashSet<>();
        persons.add(new Person("Piet"));
        persons.add(new Person("Joris"));
        genericPayement_with_past_date = new Payment(3, new Date(0));
        genericOrder_with_past_date = new Order(5, new Date(0));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class ServiceRepositoryStub.
     */
    @Test
    public void AddPerson_voegt_nieuwe_persoon_toe() {
        stub.addPerson(genericPerson);
        assertTrue(stub.getAllPersons().contains(genericPerson));
    }

    /**
     * Test of addOrder method, of class ServiceRepositoryStub.
     */
    @Test
    public void AddOrder_voegt_nieuwe_order_toe_vooer_elke_meegegeven_persoon_wanneer_alle_personen_geregistreerd_zijn() {
        //registreer elke persoon uit de set
        for(Person person: persons){
            stub.addPerson(person);
        }
        //add de order, en geef de personen mee.
        stub.addOrder(genericOrder_with_past_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        for(Person person:persons){
            assertTrue(stub.getAllOrdersForPerson(person).contains(genericOrder_with_past_date));
        }
    }

    /**
     * Test of addPaymentForPerson method, of class ServiceRepositoryStub.
     */
    @Test
    public void testAddPersonPayment_voegt_payment_toe_aan_de_meegegeven_persoon() {
        stub.addPaymentForPerson(genericPerson, genericPayement_with_past_date);
        assertTrue(genericPerson.getPayments().contains(genericPayement_with_past_date));
    }

   
    
}
