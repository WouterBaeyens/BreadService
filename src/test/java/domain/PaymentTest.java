/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDate;
import java.util.Date;
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
public class PaymentTest {
    
    private Payment positivePaymentWithCurrentDate;
    
    public PaymentTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        positivePaymentWithCurrentDate = new Payment(3, LocalDate.now());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDate method, of class Order.
     */
    @Test
    public void Payment_should_create_new_payment_with_given_date_and_amount() {
        double amount = 5;
        LocalDate date = LocalDate.now();
        Payment payment = new Payment(amount, date);
        assertEquals(amount, payment.getAmount(), 0.00001);
        assertEquals(date, payment.getDate());
    }
    
    /**
     * Test of setAmount method, of class Payment.
     */
    @Test
    public void testSetAmountShouldUpdateTheAmountForPositiveGivenAmount() {
        double amount = 9.5;
        positivePaymentWithCurrentDate.setAmount(amount);
        double actualAmount = positivePaymentWithCurrentDate.getAmount();
        assertEquals(amount, actualAmount, 0.0000001);
    }
    
}
