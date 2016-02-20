/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

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
        genericPayement_with_past_date = new Payment(3, new Date(0));
         genericPayement_with_past_date2 = new Payment(5, new Date(0));
        genericOrder_with_past_date = new Order(5, new Date(0));
        genericOrder_with_past_date2 = new Order(10, new Date(0));
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPerson method, of class ServletFacade.
     */
    @Test
    public void AddPerson_voegt_nieuwe_persoon_toe() {
        facade.addPerson(genericPerson);
        assertTrue(facade.getAllPersons().contains(genericPerson));
    }

    /**
     * Test of addOrder method, of class ServletFacade.
     */
    @Test
    public void AddOrder_voegt_nieuwe_order_toe_vooer_elke_meegegeven_persoon_wanneer_alle_personen_geregistreerd_zijn() {
        //registreer elke persoon uit de set
        for(Person person: persons){
            facade.addPerson(person);
        }
        //add de order, en geef de personen mee.
        facade.addOrder(genericOrder_with_past_date, persons);
        //check of aan elke persoon de meegegeven order gekoppeld is.
        Set<Person> personsWithOrder = facade.getPersonsWithOrder(genericOrder_with_past_date);
        assertTrue(personsWithOrder.containsAll(persons));
    }

    /**
     * Test of addPersonPayment method, of class ServletFacade.
     */
    @Test
    public void testAddPersonPayment_voegt_payment_toe_aan_de_meegegeven_persoon() {
        facade.addPersonPayment(genericPerson, genericPayement_with_past_date);
        assertTrue(genericPerson.getPayments().contains(genericPayement_with_past_date));
    }

    /**
     * Test of getPersonTotalPayment method, of class ServletFacade.
     */
    @Test
    public void testGetPersonTotalPayment_berekend_totale_bedrag_betaald_door_meegegeven_persoon() {        
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
    public void testGetPersonTotalOrderExpenses_berekend_totale_kost_voor_meegegeven_persoon() {
        persons.add(genericPerson);
        facade.addOrder(genericOrder_with_past_date, persons);
        facade.addOrder(genericOrder_with_past_date2, persons);
        double expectedExpenses = genericOrder_with_past_date.getCostPerPerson() + genericOrder_with_past_date2.getCostPerPerson();
        double actualExpenses = facade.getPersonTotalOrderExpenses(genericPerson);
        assertEquals(expectedExpenses, actualExpenses, 0.000001);
    }
    
}
