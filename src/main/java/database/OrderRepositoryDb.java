/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Wouter
 */
public class OrderRepositoryDb implements OrderRepository{
    private EntityManagerFactory factory;
    private EntityManager manager;
    
    public OrderRepositoryDb(String namePersistenceUnit){
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }
    
    @Override
    public void addOrder(OrderBill order) {
        try{
            manager.getTransaction().begin();
            manager.persist(order);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteOrder(long orderId) {
        try{
            OrderBill order = getOrder(orderId);
            manager.getTransaction().begin();
            manager.remove(order);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }
    }
    

    //http://stackoverflow.com/questions/8307578/what-is-the-best-way-to-update-the-entity-in-jpa
    //in conclusion: since the entity is persistent it seemed best 
    //to just change the cost with it's setters from here.
    @Override
    public void updateOrder(long orderId, double newCost, LocalDate newDate) {
         try{
            OrderBill order = getOrder(orderId);
            order.setDate(newDate);
            order.setTotalCost(newCost);
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public OrderBill getOrder(long orderId) {
        try {
            manager.getTransaction().begin();
            OrderBill order = manager.find(OrderBill.class, orderId);
            manager.flush();
            manager.getTransaction().commit();
            return order;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(),e);

        }
    }

    @Override
    public List<OrderBill> getAllOrders() {
        try {
            Query query = manager.createNamedQuery("Order.getAll");
            return query.getResultList();
        } catch (NoResultException e){
            return new ArrayList<>();
        }catch (Exception e) {
            throw new DbException(e.getMessage(),e);
        }
    }

    @Override
    public void closeConnection() throws DbException {
        try{
        manager.close();
        factory.close();
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public void deleteAllOrders() {
        try {
            manager.getTransaction().begin();
            //manager.createNativeQuery("truncate table OrderBill").executeUpdate();
            //delete seemed safer
            manager.createQuery("delete from OrderBill").executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e);
        }
        
    }
    
    
    
}
