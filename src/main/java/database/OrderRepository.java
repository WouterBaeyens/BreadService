/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeekPK;
import domain.Person;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface OrderRepository {
 
    void addOrder(OrderBill order);
    
    /*Deletes the order and all it's refernces to the persons it is assigned to*/
    void deleteOrder(OrderWeekPK orderWeekPK);
    
    void deleteAllOrders();
    
    //I've changed the structure so that each week is an entity that can have an orther attached to it.
    //void updateOrder(long orderId, double newCost, LocalDate newDate);
    //returning a managed instance of order as suggested by
    //http://stackoverflow.com/questions/19746240/jpa-merge-unmanaged-entity
    OrderBill updateOrder(OrderBill order);
    
    void refreshOrder(OrderBill order);
    
    /*Returns the order based on it's id*/
    OrderBill getOrder(OrderWeekPK orderWeek);
    
    /*Returns a list with all the existing orders.*/
    List<OrderBill> getAllOrders();
    
        public void removeRelationsToPerson(Person person);
    
    public void closeConnection() throws DbException;
    
    public boolean isManaged(Object order);
    
    public void clearManager();
}
