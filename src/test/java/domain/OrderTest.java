/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDateTime;
import java.util.Date;
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
public class OrderTest {
    
    public OrderTest() {
    }
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private Order validOrderWithCurrentDate;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        validOrderWithCurrentDate = new Order(3, LocalDateTime.now());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDate method, of class Order.
     */
    @Test
    public void order_constructor_should_create_new_order_with_given_date_and_amount() {
        double amount = 5;
        LocalDateTime date = LocalDateTime.now();
        Order order = new Order(amount, date);
        assertEquals(amount, order.getCostPerPerson(), 0.00001);
        assertEquals(date, order.getDate());
    }
    
    @Test
    public void order_constructor_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        exception.expect(DomainException.class);
        Order order = new Order(amount, LocalDateTime.now());
    }
    
    public void setAmount_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        validOrderWithCurrentDate.setCostPerPerson(amount);
        exception.expect(DomainException.class);
        
    }

    
}
