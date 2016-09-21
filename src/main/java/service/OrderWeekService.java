/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.OrderWeekRepository;
import database.OrderWeekRepositoryFactory;
import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import java.util.List;

/**
 *
 * @author Wouter
 */
public class OrderWeekService {
    
    private OrderWeekRepository repository;
    
    public OrderWeekService(String repositoryType){
        this.repository = OrderWeekRepositoryFactory.createOrderWeekRepository(repositoryType);
    }
    
    public void addWeek(OrderWeek orderWeek){
        repository.addWeek(orderWeek);
    }
    
    public OrderWeek getWeek(int week, int year){
        return repository.getWeek(new OrderWeekPK(week, year));
    }
    
    public List<OrderWeek> getAllWeeks(){
        return repository.getAllWeeks();
    }
    
    public void updateWeek(OrderWeek orderweek){
        repository.updateWeek(orderweek);
    }
    
    public void deleteWeek(int week, int year){
        repository.deleteWeek(new OrderWeekPK(week, year));
    }
    
    public void deleteAllWeeks(){
        repository.deleteAllWeeks();
    }
    
    public void addOrder(OrderBill order){
        OrderWeekPK pk = order.getOrderPK();
        //Week: (not init)
        if(pk == null)
            throw new ServiceException("Err. adding order (" + order + ") can't add order without pk");
        
        OrderWeek week = repository.getWeek(pk);
        
        //Week: (init, not persisted); Order: (not persisted)
        if(week == null)
            addWeek(order.getOrderWeek());   
        //Week: (init, persisted); Order: (not persisted)
        else if(week.getOrderBill() == null){
            week.setOrder(order);
            updateWeek(week);
        }
        //Week: (init, persisted with other order)
        else
            throw new ServiceException("Err. adding order (" + order + ") another order already exists for that week " + "(" + week.getOrderBill() + ")");
    }
    
    public void refreshWeek(OrderWeek week){
        repository.refreshOrderWeek(week);
    }
    
}
