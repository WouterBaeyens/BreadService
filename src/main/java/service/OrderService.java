/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DbException;
import database.OrderRepository;
import database.OrderRepositoryFactory;
import domain.OrderBill;
import domain.Person;
import java.time.LocalDate;
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
        
    public void addOrder(OrderBill order){
        repository.addOrder(order);
    }
    
    public OrderBill getOrder(long orderId){
        try{
            return repository.getOrder(orderId);
        } catch (DbException ex){
            throw new ServiceException(ex.getMessage());
        }
    }
    
    public void updateOrder(long orderId,double newCost, LocalDate newDate){
        repository.updateOrder(orderId, newCost, newDate);
    }
    
    public void deleteOrder(long orderId){
        repository.deleteOrder(orderId);
    }
    
    public Set<Person> getPersonsWithOrder(long orderId){
        OrderBill order = repository.getOrder(orderId);
        return order.getAuthors();
    }
    
    List<OrderBill> getAllOrders() {
        return repository.getAllOrders();
    }
    
    public void deleteAllOrders(){
        repository.deleteAllOrders();
    }

    public void closeConnection() {
        repository.closeConnection();
    }
        
}
