/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;

/**
 *
 * @author Wouter
 */
public class AWeekTestDb implements OrderWeekRepository {

    private EntityManagerFactory factory;
    private EntityManager manager;

    public AWeekTestDb(String namePersistenceUnit) {
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }

    @Override
    public void addWeek(OrderWeek week) {
        OrderWeek o = getWeek(week.getOrderWeekPK());
            if(o != null)
            throw new DbException("[Err. adding week: already persisted: old " + o + " | new " + week + "] ");
       
        try {
            manager.getTransaction().begin();
            manager.persist(week);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("Err. adding week: " + week + " - " + e.getMessage(), e);
        }
    }

    @Override
    public OrderWeek getWeek(OrderWeekPK orderWeekPK) {
        OrderWeek orderWeek = null;   
        try {
            manager.getTransaction().begin();
            orderWeek = manager.find(OrderWeek.class, orderWeekPK);
            manager.getTransaction().commit();
            return orderWeek;
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
            manager.getTransaction().rollback();
            String message = orderWeek == null ? 
                    "[Err. week with id " + orderWeekPK + " not found]":
                    "[Err. getting week " + orderWeek + "]";
            throw new DbException(message  + e.getMessage(),e);
        }     }

    @Override
    public List<OrderWeek> getAllWeeks() {
        try{
        manager.getTransaction().begin();
        List<OrderWeek> weeks = manager.createNamedQuery("OrderWeek.getAll", OrderWeek.class).getResultList();
        manager.getTransaction().commit();
        return weeks;
         } catch (NoResultException e){
            return new ArrayList<>();
        } catch (Exception e){
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. retrieving all weeks] " + e.getMessage(), e);
        } 
    }

    @Override
    public void updateWeek(OrderWeek orderWeek) {
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
        //cascade should handle the leftovers
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
    
    @Override
    public void deleteAllWeeks() {
        List<OrderWeek> weeks = getAllWeeks();
        for(OrderWeek week: weeks){
            deleteWeek(week.getOrderWeekPK());
        }
    }

    @Override
    public void closeConnection() {
        manager.close();
        factory.close();
    }

    @Override
    public boolean isManaged(Object o) {
        return manager.contains(o);
    }

    @Override
    public boolean isWeekInDb(OrderWeekPK pk) {
        boolean foundIt = manager.find(OrderWeek.class, pk) != null;
        return foundIt;    
    }

    @Override
    public void refreshOrderWeek(OrderWeek week) {
                 try{
            manager.getTransaction().begin();
            manager.refresh(week);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error refreshing " + week + "\n" + e.getMessage(), e);
        }    }

    @Override
    public void clearManager() {
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();
        manager.clear();
        
    }

    /*    @Override
    public EntityManager getManager() {
               try{
            manager.getTransaction().begin();
            //manager.refresh(week);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
    }
               return null;
    }
    */
    
    public void removeRelationsToPerson(long personId){
        
    }
    
    @Override
    public EntityManager getManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
