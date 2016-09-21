/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderWeek;
import domain.OrderWeekPK;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Wouter
 */
public class OrderWeekRepositoryDb implements OrderWeekRepository{
    
    private EntityManagerFactory factory;
    private EntityManager manager;
    
    public OrderWeekRepositoryDb(String namePersistenceUnit){
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }

    @Override
    public void addWeek(OrderWeek week) {
        /*OrderWeek o = getWeek(week.getOrderWeekPK());
        //boolean checkup = isManaged(o);
        if(o != null)
            throw new DbException("[Err. already persisted: old " + o + " | new " + week + "] ");
        */try{
            manager.getTransaction().begin();
            manager.persist(week);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(week + " - " + e.getMessage(), e);
        }   
    }
    
    public OrderWeek getWeek(OrderWeekPK orderWeekPK){
        OrderWeek orderWeek = null;   
        try {
            manager.getTransaction().begin();
            orderWeek = manager.find(OrderWeek.class, orderWeekPK);
            //if(orderWeek == null)
            //    manager.detach(orderWeek);
            manager.getTransaction().commit();
            return orderWeek;
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
            manager.getTransaction().rollback();
            String message = orderWeek == null ? 
                    "[Err. week with id " + orderWeekPK + " not found]":
                    "[Err. getting week " + orderWeek + "]";
            throw new DbException(message  + e.getMessage(),e);
        } 
    }
    
    @Override
    public List<OrderWeek> getAllWeeks(){
        try{
        manager.getTransaction().begin();
        List<OrderWeek> weeks = manager.createNamedQuery("OrderWeek.getAll", OrderWeek.class).getResultList();
        manager.getTransaction().commit();
        return weeks;
        } catch (Exception e){
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. retrieving all weeks] " + e.getMessage(), e);
        }
    }
    
    @Override
    public void updateWeek(OrderWeek orderWeek){
        try{
            manager.getTransaction().begin();
            manager.merge(orderWeek);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("[Err. updating " + orderWeek + "] " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteWeek(OrderWeekPK orderWeekPK) {
        OrderWeek orderWeek = getWeek(orderWeekPK);
        //cascade should handel the leftovers
         try{
            manager.getTransaction().begin();
            manager.remove(orderWeek);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error deleting " + orderWeek + "\n" + e.getMessage(), e);
        }
    }
    
    
    //when it has a link to an order
    //and the order has a link to a person
    //than this method will cause errors because of cascade delete.
    public void deleteAllWeeks(){
        manager.clear();
        List<OrderWeek> weeks = getAllWeeks();
        for(OrderWeek week: weeks){
            deleteWeek(week.getOrderWeekPK());
        }
    }
    
    public void closeConnection(){
        manager.close();
        factory.close();
    }
    
    //Note this is for testing purposes only
    public EntityManager getManager(){
        return manager;
    }
    
    @Override
    public boolean isManaged(Object o){
        return manager.contains(o);
    }
    
    public void refreshOrderWeek(OrderWeek week){
                 try{
            manager.getTransaction().begin();
            manager.refresh(week);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error refreshing " + week + "\n" + e.getMessage(), e);
        }
    }
}
