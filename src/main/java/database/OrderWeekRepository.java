/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import java.util.List;
import javax.persistence.EntityManager;

/**
 *
 * @author Wouter
 */
//Note: watch out for persisting the order twice!
public interface OrderWeekRepository {
        
    public void addWeek(OrderWeek week);
    
    public OrderWeek getWeek(OrderWeekPK orderWeekPK);
    
    public List<OrderWeek> getAllWeeks();
    
    public void updateWeek(OrderWeek orderWeek);
    
    public void deleteWeek(OrderWeekPK orderWeek);
    
    public void deleteAllWeeks();
    
    public void closeConnection();
    //I think the cascade should be able to add an order
    //public void addOrder(OrderBill order);
    
    //I think the cascade should be able to handle the remove
    //public void removeOrder(long OrderId);

    //Mainly for testing purposes
    public boolean isManaged(Object o);
    
    public boolean isWeekInDb(OrderWeekPK pk);
    
    public void refreshOrderWeek(OrderWeek week);
    
    public void clearManager();
    
    //TESTING ONLY
    public EntityManager getManager();
}
