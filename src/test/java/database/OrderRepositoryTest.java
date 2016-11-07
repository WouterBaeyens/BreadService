/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeek;
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
    private static OrderRepository orderRep;
    private static OrderWeekRepository weekRep;
    private OrderBill cheap_order;
    private OrderBill expensive_order;
    private OrderWeek week_recent;
    private OrderWeek week_past;
    
    public OrderRepositoryTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        orderRep = OrderRepositoryFactory.createOrderRepository("combi_jpa");
        //repository = OrderRepositoryFactory.createOrderRepository("JPA");
        orderRep.deleteAllOrders();
        
        weekRep = OrderWeekRepositoryFactory.createOrderWeekRepository("combi_jpa");
        //weekRep = OrderWeekRepositoryFactory.createOrderWeekRepository("JPA");
        weekRep.deleteAllWeeks();
    }
    
    @AfterClass
    public static void tearDownClass() {
        orderRep.closeConnection();
    }
    
    @Before
    public void setUp() {
        cheap_order = new OrderBill(0.38);
        expensive_order = new OrderBill(8000);
        week_recent = new OrderWeek(LocalDate.now());
        week_past = new OrderWeek(LocalDate.now().minusWeeks(16));
    }
    
    @After
    public void tearDown() {
        orderRep.deleteAllOrders();
        weekRep.deleteAllWeeks();
    }

    /**
     * Test of addOrder method, of class OrderRepositoryStub.
     * For now orders are added through weeks, while a correct implementation for this method is not available yet
     */
    /*@Test
    public void addOrder_adds_the_given_order_to_the_stub() {
        //double cost = genericOrder_with_current_date.getTotalCost();
        //LocalDate date = genericOrder_with_current_date.getDate();
        repository.addOrder(genericOrder_with_current_date);

        long id = genericOrder_with_current_date.getId();
        
        OrderBill order = repository.getOrder(id);
        assertEquals(genericOrder_with_current_date, order);
    }*/

    /**
     * Test of deleteOrder method, of class OrderRepositoryStub.
     */
    /*@Test
    public void deleteOrder_removes_order_with_given_id_from_stub() {
        //repository.addOrder(genericOrder_with_current_date);
        weekRep.addWeek(week_recent);
        week_recent.setOrder(cheap_order);
        weekRep.updateWeek(week_recent);

        OrderBill managed_cheap_order = orderRep.updateOrder(cheap_order);
        List<OrderBill> orders = orderRep.getAllOrders();
        assertTrue(orders.contains(managed_cheap_order));
        
        orderRep.deleteOrder(cheap_order.getOrderPK());
        
        orders = orderRep.getAllOrders();
        assertFalse(orders.contains(managed_cheap_order));
        
    }


    /**
     * Test of updateOrder method, of class OrderRepositoryStub.
     */
    /*
    @Test
    public void updateOrder_updates_the_payment_with_the_given_id_to_the_given_values() {
        repository.addOrder(cheap_order);
        long id = cheap_order.getId();
        
        double newCost = 15.4;
        
        repository.updateOrder(id, newCost);
        OrderBill order = repository.getOrder(id);
        assertEquals(newCost, order.getTotalCost(), 0.00001);
        //assertEquals(week, order.getWeekNr());
        //assertEquals(year, order.getYearNr());
    }
    
    @Test
    public void getOrder_returns_order_with_given_week_and_year(){
        repository.addOrder(expensive_order);
        repository.addOrder(cheap_order);
        int week = expensive_order.getWeekNr();
        int year = expensive_order.getYearNr();
        List<OrderBill> orders = repository.getOrders(week, year);
        assertTrue(orders.contains(expensive_order));
        assertFalse(orders.contains(cheap_order));
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
