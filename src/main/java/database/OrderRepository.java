/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface OrderRepository {
 
    void addOrder(Order order);
    
    /*Deletes the order and all it's refernces to the persons it is assigned to*/
    void deleteOrder(long orderId);
    
    void updateOrder(long orderId, double newCost, LocalDateTime newDate);
    
    /*Returns the order based on it's id*/
    Order getOrder(long orderId);
    
    /*Returns a list with all the existing orders.*/
    List<Order> getAllOrders();
    
}
