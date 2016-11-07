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
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Wouter
 * Notes: 
 *  - persist immediately adds obj. to database, no merge needed
 *  - after clearing manager, objects can't be retrieved from db.
 *  - managed entities are ignored by merge operation
 *  - after adding an order to a week, you can
 *          1. refresh it: the order won't be managed through the week (it will have exactly as the db says, so week->order->null)
 *          2. search the week in the database: the order will be managed through the week
 *  - removing many to many relationships can be done without too much problems if one of the EM's gives a managed entity to the other EM
 * 
 */
public class GeneralTestFile {
    
    private static OrderRepository orderRep;
    private static OrderWeekRepository weekRep;
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
    
    @BeforeClass
    public static void setUpClass() {
        weekRep = OrderWeekRepositoryFactory.createOrderWeekRepository("test");
        orderRep = OrderRepositoryFactory.createOrderRepository("test");
        personRep = PersonRepositoryFactory.createPersonRepository("test");
  
        orderRep.deleteAllOrders();
        personRep.deleteAllPersons();
        weekRep.deleteAllWeeks();
        
    }
    
        @AfterClass
    public static void tearDownClass() {
        weekRep.clearManager();
        orderRep.clearManager();
        personRep.clearManager();
        clearEverything();

        weekRep.closeConnection();
        orderRep.closeConnection();
        personRep.closeConnection();
    }
    
    @Before
    public void setUp() {
        orderRep.clearManager();
        weekRep.clearManager();
        personRep.clearManager();
        
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
        clearEverything();
    }
    
    public static void clearEverything(){
        List<OrderBill> orders = orderRep.getAllOrders();
        for(OrderBill o: orders)
            personRep.removeRelationsToOrder(o);
        orderRep.deleteAllOrders();
        personRep.deleteAllPersons();
        weekRep.deleteAllWeeks();
    }
    //-------------------------WEEK------------------------------    
    @Test
    public void test_add_week_adds_week_to_db(){
        weekRep.addWeek(pastWeek);
        OrderWeek foundWeek = weekRep.getWeek(pastWeek.getOrderWeekPK());
                assertEquals("week added, but not found afterwards", pastWeek, foundWeek);
        List<OrderWeek> weeks = weekRep.getAllWeeks();
                assertEquals("only 1 week added in this class but found " + weeks.size() + "weeks.", 1, weeks.size());
    }
    
    @Test
        public void test_find_detached_week_after_clear(){
            weekRep.addWeek(pastWeek);
        OrderWeek foundWeek = weekRep.getWeek(pastWeek.getOrderWeekPK());
        assertEquals("week added, but not found afterwards", pastWeek, foundWeek);
        weekRep.clearManager();
        foundWeek = weekRep.getWeek(pastWeek.getOrderWeekPK());
        assertEquals("expected to find added week (by Pk) " + pastWeek + "but found week" + foundWeek, pastWeek.getOrderWeekPK(), foundWeek.getOrderWeekPK());
        List<OrderWeek> weeks = weekRep.getAllWeeks();
            assertEquals("only 1 week added in this class but found " + weeks.size() + "weeks after manager clear.", 1, weeks.size());     
        }
    //---------------------ORDER--------------------------------

    
         @Test
    public void test_addOrder_adds_order_to_db(){
        weekRep.addWeek(currentWeek);
        assertTrue("since currentWeek is just added, it should be managed by currentWeek", weekRep.isManaged(currentWeek));
        currentWeek.setOrder(cheapOrder);
        orderRep.addOrder(cheapOrder);
        assertTrue("since currentWeek is just added, it should be managed by currentWeek", weekRep.isManaged(currentWeek));
        //Note: the getWeek is just a means to force the weekRep manager to flush.;
        //weekRep.getWeek(currentWeek.getOrderWeekPK());
        //weekRep.updateWeek(currentWeek);
        
        //this assertion really fluctuates, can't pinpoint the reason
        assertFalse("since the em's don't carry baggage, cheaporder is not managed here (this can flucuate without clearing the em)", weekRep.isManaged(cheapOrder));
        OrderBill foundOrder = orderRep.getOrder(cheapOrder.getOrderPK());
                assertEquals("order added, but not found afterwards", cheapOrder, foundOrder);
        List<OrderBill> orders = orderRep.getAllOrders();
                assertEquals("only 1 order added in this class but found " + orders.size() + "weeks.", 1, orders.size());
    }  


     @Test
    public void test_addOrder_adds_order_to_existing_week(){
        weekRep.addWeek(currentWeek);
        weekRep.clearManager();
        OrderWeek foundWeek = weekRep.getWeek(currentWeek.getOrderWeekPK());
        foundWeek.setOrder(cheapOrder);
        orderRep.addOrder(cheapOrder);
        OrderBill foundOrder = orderRep.getOrder(cheapOrder.getOrderPK());
                assertEquals("order added, but not found afterwards", cheapOrder, foundOrder);
        List<OrderBill> orders = orderRep.getAllOrders();
                assertEquals("only 1 order added in this class but found " + orders.size() + "weeks.", 1, orders.size());
    }  
    
   @Test
    public void test_deleteOrder(){
         weekRep.addWeek(currentWeek);
        currentWeek.setOrder(cheapOrder);
        orderRep.addOrder(cheapOrder);
        
        orderRep.deleteOrder(cheapOrder.getOrderPK());
        assertEquals("order should be deleted (return null), but was still found in Db",null, orderRep.getOrder(currentWeek.getOrderWeekPK()));
        assertEquals("the week still had the supposedly deleted order attached to it",null, weekRep.getWeek(currentWeek.getOrderWeekPK()).getOrderBill());
    }
    
    //-------------PERSON
    
    @Test
    public void test_addPerson_adds_person_to_db(){
                personRep.addPerson(jan);
        Person foundPerson = personRep.getPerson(jan.getId());
                assertEquals("person added, but not found afterwards", jan, foundPerson);
        Set<Person> persons = personRep.getAllPersons();
                assertEquals("1 person added in this class but found " + persons.size() + "persons.", 1, persons.size());
    }

    /*@Test
    public void test_add_order_to_existing_persons_and_week(){
        personRep.addPerson(jan);
        personRep.addPerson(piet);
        weekRep.addWeek(currentWeek);
        personRep.clearManager();
        weekRep.clearManager();
        
        OrderWeek foundWeek = weekRep.getWeek(currentWeek.getOrderWeekPK());
        foundWeek.setOrder(cheapOrder);
        orderRep.addOrder(cheapOrder);
        assertTrue("this order has just been added and should thus be managed", orderRep.isManaged(cheapOrder));
        cheapOrder.addAuthor(personRep.getPerson(jan.getId()));
        cheapOrder.addAuthor(personRep.getPerson(piet.getId()));
        orderRep.updateOrder(cheapOrder);
        orderRep.clearManager();
        cheapOrder = orderRep.getOrder(cheapOrder.getOrderPK());
        assertEquals("2 persons should be added to the author, but another number was found", 2, orderRep.getOrder(cheapOrder.getOrderPK()).getAuthors().size());
    }
    
      */  @Test
    public void test_delete_added_orders(){
        //Add a week + reset
        personRep.addPerson(jan);
        personRep.addPerson(piet);
        weekRep.addWeek(currentWeek);
        personRep.clearManager();
        weekRep.clearManager();
        
        //Add Order with 2 persons attached + reset
        OrderWeek foundWeek = weekRep.getWeek(currentWeek.getOrderWeekPK());
        foundWeek.setOrder(cheapOrder);
        orderRep.addOrder(cheapOrder);
        assertTrue("this order has just been added and should thus be managed", orderRep.isManaged(cheapOrder));
        cheapOrder.addAuthor(personRep.getPerson(jan.getId()));
        cheapOrder.addAuthor(personRep.getPerson(piet.getId()));
        orderRep.clearManager();
        weekRep.clearManager();
        personRep.clearManager();
        
        //The actual test of removing the relations and deleting the order afterwards
        cheapOrder = orderRep.getOrder(cheapOrder.getOrderPK());
        Person p1 = personRep.getPerson(jan.getId());  
        Person p2 = personRep.getPerson(piet.getId());
        assertEquals("2 persons should be added to the author, but another number was found", 2, orderRep.getOrder(cheapOrder.getOrderPK()).getAuthors().size());      
        assertTrue("the order should have a relation to the person", cheapOrder.getAuthors().contains(p1));
        //both work
        cheapOrder.removeAuthor(p1);
        cheapOrder.removeAuthor(p2);
        //cheapOrder.removeAllAuthors();
        
        //p1.removeOrder(cheapOrder);
        //p2.removeOrder(cheapOrder);
        orderRep.deleteOrder(cheapOrder.getOrderPK());
        Person p = personRep.getPerson(jan.getId());
            assertFalse("there's still an order linked to jan", p.getOrders().size() > 0);
    }
    
    @Test
    public void test_delete_2_added_orders(){
        //Add a week + reset
        personRep.addPerson(jan);
        personRep.addPerson(piet);
        weekRep.addWeek(currentWeek);
        weekRep.addWeek(pastWeek);
        personRep.clearManager();
        weekRep.clearManager();
        
        //Add Order with 2 persons attached + reset
        OrderWeek week1 = weekRep.getWeek(currentWeek.getOrderWeekPK());
        OrderWeek week2 = weekRep.getWeek(pastWeek.getOrderWeekPK());
        week1.setOrder(cheapOrder);
        week2.setOrder(expensiveOrder);
        orderRep.addOrder(cheapOrder);
        orderRep.addOrder(expensiveOrder);
        assertTrue("this order has just been added and should thus be managed", orderRep.isManaged(cheapOrder));
        assertTrue("this order has just been added and should thus be managed", orderRep.isManaged(expensiveOrder));
        cheapOrder.addAuthor(personRep.getPerson(jan.getId()));
        cheapOrder.addAuthor(personRep.getPerson(piet.getId()));
        expensiveOrder.addAuthor(personRep.getPerson(jan.getId()));
        expensiveOrder.addAuthor(personRep.getPerson(piet.getId()));
        orderRep.clearManager();
        weekRep.clearManager();
        personRep.clearManager();
        
        //The actual test of removing the relations and deleting the order afterwards
        OrderBill order1 = orderRep.getOrder(cheapOrder.getOrderPK());
        OrderBill order2 = orderRep.getOrder(expensiveOrder.getOrderPK());        
        Person p1 = personRep.getPerson(jan.getId());  
        Person p2 = personRep.getPerson(piet.getId());
        assertEquals("2 persons should be added to the author, but another number was found", 2, orderRep.getOrder(order1.getOrderPK()).getAuthors().size());      
        assertTrue("the order should have a relation to the person", order1.getAuthors().contains(p1));

        personRep.removeRelationsToOrder(order2);
        personRep.removeRelationsToOrder(order1);
        //orderRep.removeRelationsToPerson(p1);
        //orderRep.removeRelationsToPerson(p2);


        orderRep.deleteOrder(order1.getOrderPK());
        orderRep.deleteOrder(order2.getOrderPK());
        Person p = personRep.getPerson(jan.getId());
            assertFalse("there's still an order linked to jan", p.getOrders().size() > 0);
    }
    
}