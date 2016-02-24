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

/**
 *
 * @author Wouter
 */
public class OrderTest {
    
    public OrderTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDate method, of class Order.
     */
    @Test
    public void order_constructor_with_given_amount_and_date_sets_amount_and_date_of_order() {
        double amount = 5;
        LocalDateTime date = LocalDateTime.now();
        Order order = new Order(amount, date);
        assertEquals(amount, order.getCostPerPerson(), 0.00001);
        assertEquals(date, order.getDate());
    }

    
}
