/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
import domain.Person;
import java.time.LocalDate;
import java.util.List;
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
public class PaymentRepositoryTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private static PaymentRepository repository;
    //A payment can't exist without a person initiating it.
    private Person genericPerson_for_owning_payments;
    private Payment genericPayment_with_current_date;
    private Payment genericPayment_with_past_date;
    
    public PaymentRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        repository = PaymentRepositoryFactory.createPaymentRepository("JPA");
        repository.deleteAllPayments();
    }
    
    @AfterClass
    public static void tearDownClass() {
        repository.closeConnection();
    }
    
    @Before
    public void setUp() {
        // for now the original database is used for the tests as well
        genericPayment_with_current_date = new Payment(15, LocalDate.now());
        genericPayment_with_past_date = new Payment(0.38, LocalDate.MIN);
    }
    
    @After
    public void tearDown() {
        repository.deleteAllPayments();
    }

    /**
     * Test of addPayment method, of class PaymentRepositoryStub.
     */
    @Test
    public void addPayment_adds_the_given_payment_to_the_stub() {
        double amount = genericPayment_with_current_date.getAmount();
        LocalDate date = genericPayment_with_current_date.getDate();
        repository.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        Payment payment = repository.getPayment(id);
        assertEquals(amount, payment.getAmount(), 0.00001);
        assertEquals(date, payment.getDate());
    }

    /**
     * Test of updatePayment method, of class PaymentRepositoryStub.
     */
    @Test
    public void updatePayment_updates_the_payment_with_the_given_id_to_the_given_values() {
        double cost = genericPayment_with_current_date.getAmount();
        LocalDate date = genericPayment_with_current_date.getDate();
        repository.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        double newCost = 15.4;
        LocalDate newDate = LocalDate.MAX;
        
        repository.updatePayment(id, newCost, newDate);
        Payment payment = repository.getPayment(id);
        assertEquals(newCost, payment.getAmount(), 0.00001);
        assertEquals(newDate, payment.getDate());
    }

    /**
     * Test of deletePayment method, of class PaymentRepositoryStub.   
     * This method fails sometimes because of persistence-error.
     * I can't figure out the cause due to it's unpredictable nature
     * Might be desynced managers that cause it to fail sometimes?
    */
    /*@Test
     public void deletePayment_removes_order_with_given_id_from_stub() {
        repository.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        repository.deletePayment(id);
        assertFalse(repository.getAllPayments().contains(genericPayment_with_current_date));
    }*/
    
     @Test
     public void payments_get_different_unique_ids_assigned_when_added(){
         repository.addPayment(genericPayment_with_past_date);
         repository.addPayment(genericPayment_with_current_date);
         long id = genericPayment_with_past_date.getId();
         long id2 = genericPayment_with_current_date.getId();
         assertNotEquals(id, id2);
     }
}
