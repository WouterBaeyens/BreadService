/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DbException;
import database.GeneralRepositoryFactory;
import database.OrderRepository;
import database.OrderRepositoryFactory;
import domain.OrderBill;
import domain.OrderWeekPK;
import domain.Person;
import java.time.LocalDate;
import java.time.temporal.WeekFields;
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
            //this.repository = GeneralRepositoryFactory.createOrderRepository(repostioryType);
        }
        
    public void addOrder(OrderBill order){
        repository.addOrder(order);
    }
    
    public OrderBill getOrder(int week, int year){
        return repository.getOrder(new OrderWeekPK(week, year));
    }
    
    public OrderBill getCurrentOrder(){
        LocalDate date = LocalDate.now();
        int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = date.get(WeekFields.ISO.weekBasedYear());
        return getOrder(week, year);
    }
    
    public void updateOrder(int week, int year,double newCost){
        OrderBill order = getOrder(week, year);
        order.setTotalCost(newCost);
        //update probably not necessary, since order is managed and cost is no FK/PK
        repository.updateOrder(order);
    }
    
    public OrderBill updateOrder(OrderBill order){
        return repository.updateOrder(order);
    }
    
    public void refreshOrder(OrderBill order){
        repository.refreshOrder(order);
    }
    
    public void deleteOrder(int week, int year){
        repository.deleteOrder(new OrderWeekPK(week, year));
    }
    
    public Set<Person> getPersonsWithOrder(int week, int year){
        OrderBill order = repository.getOrder(new OrderWeekPK(week, year));
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
    
    public void removeRelationsToPerson(Person p){
        repository.removeRelationsToPerson(p);
    }
        
    public void clearManager(){
        repository.clearManager();
    }
}
