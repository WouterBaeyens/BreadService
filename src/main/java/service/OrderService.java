/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DbException;
import database.OrderRepository;
import database.OrderRepositoryFactory;
import domain.Order;
import domain.Person;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class OrderService {
    
        private OrderRepository repository;
        
        public OrderService(String repostioryType){
            this.repository = OrderRepositoryFactory.createOrderRepository(repostioryType);
        }
        
    public void addOrder(Order order){
        repository.addOrder(order);
    }
    
    public Order getOrder(long orderId){
        try{
            return repository.getOrder(orderId);
        } catch (DbException ex){
            throw new ServiceException(ex.getMessage());
        }
    }
    
    public void updateOrder(long orderId,double newCost, LocalDateTime newDate){
        repository.updateOrder(orderId, newCost, newDate);
    }
    
    public void deleteOrder(long orderId){
        repository.deleteOrder(orderId);
    }
    
    List<Order> getAllOrders() {
        return repository.getAllOrders();
    }
        
}
