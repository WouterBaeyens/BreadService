/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import static java.time.Instant.now;
import java.time.LocalDate;
import java.util.Date;
import java.util.Set;
import javafx.util.converter.LocalDateTimeStringConverter;
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
public class PersonTest {
    
    public PersonTest() {
    }
    
    private Person personWithNameJan;
    private Payment positivePaymentWithPastDate;
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        personWithNameJan = new Person("Jan");
        positivePaymentWithPastDate = new Payment(3, LocalDate.now());
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void constructorShouldCreatePersonWithGivenName(){
        String name = "Elbrecht";
        Person person = new Person(name);
        assertEquals(name, person.getName());
    }
    
    @Test
    public void constructorShouldThrowExceptionWhenNameIsNull(){
        String name = null;
        exception.expect(DomainException.class);
        Person person = new Person(name);
    }
    

    @Test
    public void addPaymentShouldAddPaymentToGivenPerson() {
        personWithNameJan.addPayment(positivePaymentWithPastDate);
        assertTrue(personWithNameJan.getPayments().contains(positivePaymentWithPastDate));
    }
    
}
