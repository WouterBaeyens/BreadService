/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
import java.time.LocalDateTime;
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
public class PaymentRepositoryStubTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private PaymentRepositoryStub stub;
    private Payment genericPayment_with_current_date;
    private Payment genericPayment_with_past_date;
    
    public PaymentRepositoryStubTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        stub = new PaymentRepositoryStub();
        genericPayment_with_current_date = new Payment(15, LocalDateTime.now());
        genericPayment_with_past_date = new Payment(0.38, LocalDateTime.MIN);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addPayment method, of class PaymentRepositoryStub.
     */
    @Test
    public void addPayment_adds_the_given_payment_to_the_stub() {
        double amount = genericPayment_with_current_date.getAmount();
        LocalDateTime date = genericPayment_with_current_date.getDate();
        stub.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        Payment payment = stub.getPayment(id);
        assertEquals(amount, payment.getAmount(), 0.00001);
        assertEquals(date, payment.getDate());
    }

    /**
     * Test of updatePayment method, of class PaymentRepositoryStub.
     */
    @Test
    public void updatePayment_updates_the_payment_with_the_given_id_to_the_given_values() {
        double cost = genericPayment_with_current_date.getAmount();
        LocalDateTime date = genericPayment_with_current_date.getDate();
        stub.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        double newCost = 15.4;
        LocalDateTime newDate = LocalDateTime.MAX;
        
        stub.updatePayment(id, newCost, newDate);
        Payment payment = stub.getPayment(id);
        assertEquals(newCost, payment.getAmount(), 0.00001);
        assertEquals(newDate, payment.getDate());
    }

    /**
     * Test of deletePayment method, of class PaymentRepositoryStub.
     */
    @Test
     public void deletePayment_removes_order_with_given_id_from_stub() {
        stub.addPayment(genericPayment_with_current_date);
        long id = genericPayment_with_current_date.getId();
        
        stub.deletePayment(id);
        assertFalse(stub.getAllPayments().contains(genericPayment_with_current_date));
    }
    
}
