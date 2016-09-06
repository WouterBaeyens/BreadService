/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.Person;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class OrderRepositoryTest {
    

    @Rule
    public final ExpectedException exception = ExpectedException.none();
    
    //beforeClass/afterClass methods need to be static, 
    //the factory of the repository should really only be closed after all the tests have run.
    //so this should happen in the afterClass.
    private static OrderRepository repository;
    private OrderBill genericOrder_with_current_date;
    private OrderBill genericOrder_with_past_date;

    
    public OrderRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        repository = OrderRepositoryFactory.createOrderRepository("JPA");
        repository.deleteAllOrders();
    }
    
    @AfterClass
    public static void tearDownClass() {
        repository.closeConnection();
    }
    
    @Before
    public void setUp() {
        genericOrder_with_current_date = new OrderBill(15, LocalDate.now());
        genericOrder_with_past_date = new OrderBill(0.38, 12,2016);
    }
    
    @After
    public void tearDown() {
        repository.deleteAllOrders();
    }

    /**
     * Test of addOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void addOrder_adds_the_given_order_to_the_stub() {
        //double cost = genericOrder_with_current_date.getTotalCost();
        //LocalDate date = genericOrder_with_current_date.getDate();
        repository.addOrder(genericOrder_with_current_date);

        long id = genericOrder_with_current_date.getId();
        
        OrderBill order = repository.getOrder(id);
        assertEquals(genericOrder_with_current_date, order);
    }

    /**
     * Test of deleteOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void deleteOrder_removes_order_with_given_id_from_stub() {
        repository.addOrder(genericOrder_with_current_date);
        long id = genericOrder_with_current_date.getId();
        
        repository.deleteOrder(id);
        List<OrderBill> orders = repository.getAllOrders();
        assertFalse(orders.contains(genericOrder_with_current_date));
        
    }


    /**
     * Test of updateOrder method, of class OrderRepositoryStub.
     */
    @Test
    public void updateOrder_updates_the_payment_with_the_given_id_to_the_given_values() {
        repository.addOrder(genericOrder_with_current_date);
        long id = genericOrder_with_current_date.getId();
        
        double newCost = 15.4;
        LocalDate newDate = LocalDate.now().minusWeeks(12);
        int week = newDate.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = newDate.get(WeekFields.ISO.weekBasedYear());
        
        repository.updateOrder(id, newCost, newDate);
        OrderBill order = repository.getOrder(id);
        assertEquals(newCost, order.getTotalCost(), 0.00001);
        assertEquals(week, order.getWeek());
        assertEquals(year, order.getYear());
    }
    
    @Test
    public void getOrder_returns_order_with_given_week_and_year(){
        repository.addOrder(genericOrder_with_past_date);
        repository.addOrder(genericOrder_with_current_date);
        int week = genericOrder_with_past_date.getWeek();
        int year = genericOrder_with_past_date.getYear();
        List<OrderBill> orders = repository.getOrders(week, year);
        assertTrue(orders.contains(genericOrder_with_past_date));
        assertFalse(orders.contains(genericOrder_with_current_date));
    }
        /**
     * Test of getAllPersonsForOrder method, of class PersonRepositoryStub.
     */
    /*
    @Test
    public void getAllPersonsForOrder_returns_all_persons_with_the_order_with_given_id() {
        Set<Person> expectedSet = new HashSet<>();
        expectedSet.add(person_with_order1);
        expectedSet.add(person_with_payment1_and_order1);
        repository.addPerson(person_with_order1);
        repository.addPerson(person_with_payment1_and_payment2);
        repository.addPerson(person_with_payment1_and_order1);
        Set<Person> personSet = repository.getAllPersonsForOrder(order1.getId());
        assertEquals(expectedSet, personSet);
    }*/
}
