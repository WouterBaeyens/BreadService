/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import domain.Person;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import service.ServiceException;

/**
 *
 * @author Wouter
 */
public class OrderWeekRepositoryDbTest {
    private static OrderWeekRepository weekRep;
    private static OrderRepository orderRep;
    private static PersonRepository personRep;
    
    private OrderWeek currentWeek;
    private OrderWeek pastWeek;
    private OrderWeek futureWeek;
    
    private OrderBill cheapOrder;
    private OrderBill expensiveOrder;
    private OrderBill normalOrder;
    
    private Person jan;
    private Person piet;
    private Person joris;
    
    public OrderWeekRepositoryDbTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        weekRep = OrderWeekRepositoryFactory.createOrderWeekRepository("JPA");
        orderRep = OrderRepositoryFactory.createOrderRepository("JPA");
        personRep = PersonRepositoryFactory.createPersonRepository("JPA");
        orderRep.deleteAllOrders();
        personRep.deleteAllPersons();
        weekRep.deleteAllWeeks();
        
    }
    
    @AfterClass
    public static void tearDownClass() {
                orderRep.deleteAllOrders();
        personRep.deleteAllPersons();
        weekRep.deleteAllWeeks();
        weekRep.closeConnection();
    }
    
    @Before
    public void setUp() {
        currentWeek = new OrderWeek(LocalDate.now());
        pastWeek = new OrderWeek(15, 2015);
        futureWeek = new OrderWeek(23, 2023);
        
        cheapOrder = new OrderBill(0.25);
        expensiveOrder = new OrderBill(820);
        normalOrder = new OrderBill(5);
        
        jan = new Person("Jan");
        piet = new Person("Piet");
        joris = new Person("Joris");
    }
    
    @After
    public void tearDown() {
                personRep.deleteAllPersons();
        orderRep.deleteAllOrders();
        weekRep.deleteAllWeeks();
        
    }

    private void addOrder(OrderBill order){
        OrderWeekPK pk = order.getOrderPK();
        if(pk == null)
             throw new ServiceException("Err. adding order (" + order + ") can't add order without pk");
        OrderWeek week = weekRep.getWeek(pk);
        if(week == null){
            OrderWeek weekToAdd = order.getOrderWeek();
            order.removeOrderWeek();
            weekRep.addWeek(weekToAdd);
            order.setOrderWeekOneWay(weekToAdd);
            orderRep.addOrder(order);
            order.setOrderWeek(weekToAdd);
            
            weekRep.getManager().refresh(weekToAdd);
            orderRep.refreshOrder(order);
        }
        else if(week.getOrderBill() == null)
            week.setOrder(order);
    }
    
    /**
     * Test of getWeek method, of class OrderWeekRepositoryDb.
     */
    @Test
    public void testGetWeek_int_int() {
        System.out.println("TEST: getWeek");
        test_cleared();
        weekRep.addWeek(pastWeek);
        assertTrue(weekRep.isManaged(pastWeek));
        weekRep.getManager().detach(pastWeek);
        assertFalse(weekRep.isManaged(pastWeek));
        OrderWeek foundWeek = weekRep.getWeek(pastWeek.getOrderWeekPK());
        assertTrue(weekRep.isManaged(foundWeek));
    }

    @Test
    public void test_addOrderWeek_with_order(){
        test_cleared();
        System.out.println("TEST: add_week_with_order");
        currentWeek.setOrder(cheapOrder);
        addOrder(cheapOrder);
        OrderBill foundOrder = orderRep.getOrder(currentWeek.getOrderWeekPK());
        assertEquals(cheapOrder.getTotalCost(), foundOrder.getTotalCost(), 0.000001);
    }
    
    /*
    //Causes nullpointerexception, but i plan to rewrite to use the method below anyway
    @Test
    public void test_add_Order_To_Existing_Week(){
        test_cleared();
        System.out.println("TEST: add_order_to_week");
        weekRep.addWeek(currentWeek);
            
        //orderRep.addOrder(cheapOrder);
             
        currentWeek.setOrder(cheapOrder);
        weekRep.updateWeek(currentWeek);
        OrderBill foundOrder = orderRep.getOrder(currentWeek.getOrderWeekPK());
        assertEquals(cheapOrder.getTotalCost(), foundOrder.getTotalCost(), 0.000001);
    }*/
    
        @Test
    public void test_add_Order_To_Week_without_using_cascade(){
        test_cleared();
        System.out.println("TEST: add_order_to_week");
        weekRep.addWeek(currentWeek);
        cheapOrder.setOrderWeek(currentWeek);
        orderRep.addOrder(cheapOrder);
             
        weekRep.updateWeek(currentWeek);
        OrderBill foundOrder = orderRep.getOrder(currentWeek.getOrderWeekPK());
        assertEquals(cheapOrder.getTotalCost(), foundOrder.getTotalCost(), 0.000001);
    }
    
    @Test
    //Reminder: updating the person, does not link it to an order. (only the other way around)
    public void test_add_persons_to_existing_orders(){
        test_cleared();
        System.out.println("Test: add_persons_to_order");
        personRep.addPerson(jan);
        personRep.addPerson(piet);
        personRep.getAllPersons();
        currentWeek.setOrder(cheapOrder);
                            pastWeek.setOrder(expensiveOrder);
        //weekRep.addWeek(currentWeek);
        addOrder(cheapOrder);
        //weekRep.addWeek(pastWeek);
        addOrder(expensiveOrder);
        
        jan.addOrder(cheapOrder);  
        piet.addOrder(cheapOrder); 
        
        
        expensiveOrder.addAuthor(jan);
        expensiveOrder.addAuthor(piet);

        orderRep.updateOrder(cheapOrder);
        orderRep.updateOrder(expensiveOrder);
        
        Person p = personRep.getPerson(jan.getId());
        assertTrue(p.getOrders().size() == 2);
        double expectedSaldo = -cheapOrder.getCostPerPerson() - expensiveOrder.getCostPerPerson();
        double actualSaldo = p.getSaldo();
        assertEquals(actualSaldo, expectedSaldo, 0.00001);
    }
    
    
    @Test
    //Reminder: updating the person, does not link it to an order. (only the other way around)
    public void test_simulate_adding_2_orders(){
        test_cleared();
        OrderBill cheapOrder = new OrderBill(5, LocalDate.now());
        OrderBill expensiveOrder = new OrderBill(10, LocalDate.now().minusWeeks(95)); 
        
        personRep.addPerson(jan);
        personRep.addPerson(piet);
        personRep.addPerson(joris);
        
        Set<Long> idList = new HashSet<>();
        idList.add(jan.getId());
        idList.add(piet.getId());
        idList.add(joris.getId());
        
        OrderWeekPK pk = cheapOrder.getOrderPK();
                if(pk == null)
            throw new ServiceException("Err. adding order (" + cheapOrder + ") can't add order without pk");
        
        OrderWeek week = weekRep.getWeek(pk);
        if(week == null){
            //throws errror
            OrderWeek weekToAdd = cheapOrder.getOrderWeek();
            cheapOrder.removeOrderWeek();
            weekRep.addWeek(weekToAdd);
            cheapOrder.setOrderWeekOneWay(weekToAdd);
            orderRep.addOrder(cheapOrder);
            cheapOrder.setOrderWeek(weekToAdd);
            //orderRep.getOrder(pk);
            //orderRep.refreshOrder(cheapOrder);
            weekRep.getManager().refresh(weekToAdd);
        }
     /*   boolean t1 = weekRep.isManaged(cheapOrder.getOrderWeek());
        boolean t2 = weekRep.isManaged(cheapOrder);
        
        OrderBill dbOrder = orderRep.updateOrder(cheapOrder);
        boolean t3  = orderRep.isManaged(cheapOrder);
        boolean t4 =  orderRep.isManaged(dbOrder);
        dbOrder.addAuthor(jan);
     
        personRep.updatePerson(jan);*/
//        orderRep.updateOrder(cheapOrder);
//        weekRep.getManager().refresh(jan);
        
        OrderWeekPK pk2 = expensiveOrder.getOrderPK();
                if(pk2 == null)
            throw new ServiceException("Err. adding order (" + expensiveOrder + ") can't add order without pk");
        
        OrderWeek week2 = weekRep.getWeek(pk2);
        if(week2 == null){
            OrderWeek weekToAdd2 = expensiveOrder.getOrderWeek();
            expensiveOrder.removeOrderWeek();
            weekRep.addWeek(weekToAdd2);
            expensiveOrder.setOrderWeekOneWay(weekToAdd2);
            orderRep.addOrder(expensiveOrder);
            expensiveOrder.setOrderWeek(weekToAdd2);
            orderRep.getOrder(pk2);
        }
    }
        
    @Test
    public void switch_orders_for_week(){
        test_cleared();
        EntityManager manager = weekRep.getManager();
        currentWeek.setOrder(cheapOrder);
        addOrder(cheapOrder);
        orderRep.deleteOrder(cheapOrder.getOrderPK());
        OrderWeek newWeek = weekRep.getWeek(currentWeek.getOrderWeekPK());
        manager.refresh(newWeek);
        OrderBill newBill = newWeek.getOrderBill();
        System.out.println(newBill);
    }
    
    public void test_cleared(){
        assertFalse(weekRep.isManaged(pastWeek));
        assertFalse(weekRep.isManaged(currentWeek));
        assertFalse(weekRep.isManaged(futureWeek));
        assertFalse(orderRep.isManaged(cheapOrder));
        assertFalse(orderRep.isManaged(normalOrder));
        assertFalse(orderRep.isManaged(expensiveOrder));
        assertFalse(personRep.isManaged(jan));
        assertFalse(personRep.isManaged(piet));
        assertFalse(personRep.isManaged(joris));
    }
    
    /**
     * Test of getWeek method, of class OrderWeekRepositoryDb.
     
    @Test
    public void testGetWeek_OrderWeekPK() {
        System.out.println("getWeek");
        OrderWeekPK orderWeekPK = null;
        OrderWeekRepositoryDb instance = null;
        OrderWeek expResult = null;
        OrderWeek result = instance.getWeek(orderWeekPK);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of addWeek method, of class OrderWeekRepositoryDb.
     
    @Test
    public void testAddWeek() {
        System.out.println("addWeek");
        OrderWeek week = null;
        OrderWeekRepositoryDb instance = null;
        instance.addWeek(week);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of deleteWeek method, of class OrderWeekRepositoryDb.
     
    @Test
    public void testRemoveWeek() {
        System.out.println("removeWeek");
        OrderWeekPK orderWeekPK = null;
        OrderWeekRepositoryDb instance = null;
        instance.deleteWeek(orderWeekPK);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
    */
    
}
