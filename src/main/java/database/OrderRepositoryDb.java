/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeek;
import domain.OrderWeekPK;
import domain.Person;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
//import org.hibernate.jpa.HibernatePersistenceProvider
import javax.persistence.Query;
import org.hibernate.Criteria;

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
    
    public OrderRepositoryDb(EntityManagerFactory managerFactory){
        factory = managerFactory;
        manager = factory.createEntityManager();
    }
    
    @Override
    //since OrderBill's only PK is a FK, it can not be correctly persisted on it's own
    public void addOrder(OrderBill order){
            //throw new DbException("For now I'm sticking to the idea that an order cannot exist on it's own and should be persisted by adding it to the orderweek");
            OrderBill o = getOrder(order.getOrderPK());
            if(o != null)
            throw new DbException("[Err. already persisted: old " + o + " | new " + order + "] ");
       
            try {
            manager.getTransaction().begin();
            manager.persist(order);
            manager.getTransaction().commit();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. persisting error (already persisted through week?)]" + e.getMessage(),e);
        }    
    }
    
        @Override
    public OrderBill getOrder(OrderWeekPK orderWeekPK) {
        OrderBill order = null;
        try {
            manager.getTransaction().begin();
            order = manager.find(OrderBill.class, orderWeekPK);
            manager.getTransaction().commit();
            return order;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            String message = order == null ? 
                    "[Err. person with id " + orderWeekPK + " not found]":
                    "[Err. getting person " + order + "]";
            throw new DbException(message + e.getMessage(),e);
        }
    }
    
        //http://stackoverflow.com/questions/8307578/what-is-the-best-way-to-update-the-entity-in-jpa
    //in conclusion: since the entity is persistent it seemed best 
    //to just change the cost with it's setters from here.
    //note: i've come to disagree with the above comments, but will leave them for reference
    @Override
    public OrderBill updateOrder(OrderBill order){
            try {
            manager.getTransaction().begin();
            OrderBill dbOrder = manager.merge(order);
            for(Person p : order.getAuthors())
                manager.merge(p);
            manager.flush();
            manager.getTransaction().commit();
            return dbOrder;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("[Err. updating order " + order+ "] " + e.getMessage(),e);
        }
    }

    @Override
    //note: if week is null, the sysout might cause an error because of manager.contains(null)
    public void deleteOrder(OrderWeekPK orderWeekPK) {
        removeAuthorsFromOrder(orderWeekPK);
        OrderBill order = getOrder(orderWeekPK);
        OrderWeek week = order.getOrderWeek();
        try{
            manager.getTransaction().begin();            
            manager.remove(order);
            manager.flush();
            manager.refresh(week);
            manager.getTransaction().commit();
        } catch (Exception e){
            if (manager.getTransaction().isActive())
            manager.getTransaction().rollback();
            throw new DbException("[Err. deleting " + order + "] " + e.getMessage(), e);
        }
    }
    
    public void refreshOrder(OrderBill order){
        try {
            manager.getTransaction().begin();
            manager.refresh(order);
            manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("[Err. refreshing " + order + " in OrderRep.] " + e.getMessage(),e);
        }
    }
    
    private void removeAuthorsFromOrder(OrderWeekPK orderWeekPK){
        OrderBill order = getOrder(orderWeekPK);
        try{
            manager.getTransaction().begin(); 
            System.out.println("remove o-p relation. order persisted? " + manager.contains(order));
            Object[] p = order.getAuthors().toArray();
            if(p != null && p.length > 0){
                System.out.println("remove o-p relation. person persisted? " + manager.contains(p[0]));
                order.removeAllAuthors();
                // update should be unnecessary since this is all done within a transactrion
                // if update needs to be added, it has to happen outside of this transaction
                // since updateOrder is executed inside a transaction of it's own
                //updateOrder(person); 
            }
            manager.getTransaction().commit();
        }catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("[Err. removing order-person relation for o_PK = " + orderWeekPK +"] " + e.getMessage(), e);
        }
        
    }

    @Override
    public List<OrderBill> getAllOrders() {
        try {
            manager.getTransaction().begin();
            Query query = manager.createNamedQuery("Order.getAll");
            List<OrderBill> orders = new ArrayList<>(query.getResultList());
            manager.getTransaction().commit();
            return orders;
        } catch (NoResultException e){
            return new ArrayList<>();
        }catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("[Err. getting all orders]" + e.getMessage(),e);
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
    public boolean isManaged(Object order){
        return manager.contains(order);
    }
    
    @Override
    public void deleteAllOrders() {
        manager.clear();
            List<OrderBill> orders = getAllOrders();
            System.out.println("deleting all (" + orders.size() + ") orders");
            for(OrderBill o: orders){
                System.out.println(o);
                deleteOrder(o.getOrderPK());
            }
    }
    
        public void removeRelationsToPerson(Person person){
            
        }
    
    public void clearManager(){
        manager.clear();
    }
}
