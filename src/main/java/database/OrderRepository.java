/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
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
    void deleteOrder(long orderId);
    
    void deleteAllOrders();
    
    void updateOrder(long orderId, double newCost, LocalDate newDate);
    
    /*Returns the order based on it's id*/
    OrderBill getOrder(long orderId);
    
    List<OrderBill> getOrders(int week, int year);
    
    /*Returns a list with all the existing orders.*/
    List<OrderBill> getAllOrders();
    
    public void closeConnection() throws DbException;
    
    public void testFlush();
    
}
