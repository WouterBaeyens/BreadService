/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Order;
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
public class OrderRepositoryStubTest {
    
    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    private OrderRepositoryStub stub;
    private Order genericOrder_with_current_date;
    private Order genericOrder_with_past_date;

    
    public OrderRepositoryStubTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        stub = new OrderRepositoryStub();
        genericOrder_with_current_date = new Order(15, LocalDateTime.now());
        genericOrder_with_past_date = new Order(0.38, LocalDateTime.MIN);
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of addOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void addOrder_adds_the_given_order_to_the_stub() {
        double cost = genericOrder_with_current_date.getTotalCost();
        LocalDateTime date = genericOrder_with_current_date.getDate();
        stub.addOrder(genericOrder_with_current_date);

        long id = genericOrder_with_current_date.getId();
        
        Order order = stub.getOrder(id);
        assertEquals(genericOrder_with_current_date, order);
    }

    /**
     * Test of deleteOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void deleteOrder_removes_order_with_given_id_from_stub() {
        stub.addOrder(genericOrder_with_current_date);
        long id = genericOrder_with_current_date.getId();
        
        stub.deleteOrder(id);
        assertFalse(stub.getAllOrders().contains(genericOrder_with_current_date));
        
    }


    /**
     * Test of updateOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void updateOrder_updates_the_payment_with_the_given_id_to_the_given_values() {
        stub.addOrder(genericOrder_with_current_date);
        long id = genericOrder_with_current_date.getId();
        
        double newCost = 15.4;
        LocalDateTime newDate = LocalDateTime.MAX;
        
        stub.updateOrder(id, newCost, newDate);
        Order order = stub.getOrder(id);
        assertEquals(newCost, order.getTotalCost(), 0.00001);
        assertEquals(newDate, order.getDate());
    }
}
