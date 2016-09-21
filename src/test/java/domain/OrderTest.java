/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
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
    
    /* http://stackoverflow.com/questions/156503/how-do-you-assert-that-a-certain-exception-is-thrown-in-junit-4-tests?rq=1
    This can be preferred over @Test(expected=IndexOutOfBoundsException.class) 
    because the test will fail if Exception is thrown before foo.doStuff()
    */
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private OrderBill validOrderWithCurrentDate;
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        validOrderWithCurrentDate = new OrderBill(3, LocalDate.now());
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of getDate method, of class OrderBill.
     */
    @Test
    public void Order_should_create_new_order_with_given_date_and_amount() {
        double amount = 5;
        LocalDate date = LocalDate.now();
        int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        OrderBill order = new OrderBill(amount, date);
        assertEquals(amount, order.getTotalCost(), 0.00001);
        assertEquals(week, order.getWeekNr());
    }
    
    @Test
    public void order_constructor_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        exception.expect(DomainException.class);
        OrderBill order = new OrderBill(amount, LocalDate.now());
    }
    
    public void setAmount_with_negative_given_amount_should_throw_exception(){
        double amount = -3;
        validOrderWithCurrentDate.setTotalCost(amount);
        exception.expect(DomainException.class);
        
    }

    
}
