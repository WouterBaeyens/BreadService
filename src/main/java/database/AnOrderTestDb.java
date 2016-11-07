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
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Wouter
 */
public class AnOrderTestDb implements OrderRepository{

    private EntityManagerFactory factory;
    private EntityManager manager;
    
        public AnOrderTestDb(String namePersistenceUnit){
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }
    
    @Override
    public void addOrder(OrderBill order) {
            OrderBill o = getOrder(order.getOrderPK());
            if(o != null)
            throw new DbException("[Err. adding order already persisted: old " + o + " | new " + order + "] ");
       
            try {
            manager.getTransaction().begin();
            manager.persist(order);
            manager.getTransaction().commit();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. persisting error (already persisted through week?)]" + e.getMessage(),e);
        }     }

    @Override
    public void deleteOrder(OrderWeekPK orderWeekPK) {
        OrderBill order = getOrder(orderWeekPK);
        if(order.getAuthors().size()>0)
            throw new DbException("[Err. remove all the persons from order " + order + " before deleting]");

        OrderWeek week = order.getOrderWeek();
        week.setOrder(null);
        //boolean test = isManaged(week);
        try{
            manager.getTransaction().begin();            
            manager.remove(order);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            if (manager.getTransaction().isActive())
            manager.getTransaction().rollback();
            throw new DbException("[Err. deleting " + order + "] " + e.getMessage(), e);
        }    }

    @Override
    public void deleteAllOrders() {
            List<OrderBill> orders = getAllOrders();
            System.out.println("deleting all (" + orders.size() + ") orders");
            for(OrderBill o: orders){
                System.out.println(o);
                deleteOrder(o.getOrderPK());
            }
    }

    @Override
    public OrderBill updateOrder(OrderBill order) {
            try {
            manager.getTransaction().begin();
                OrderBill dbOrder = manager.merge(order);
            manager.flush();
            manager.getTransaction().commit();
            return dbOrder;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("[Err. updating order " + order+ "] " + e.getMessage(),e);
        }    }

    @Override
    public void refreshOrder(OrderBill order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
        }    }

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
        }    }

    @Override
    public void closeConnection() throws DbException {
        try{
        manager.close();
        factory.close();
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    public void removeRelationsToPerson(Person person){
        List<OrderWeekPK> orderPkList = person.getOrders().stream().map(p->p.getOrderPK()).collect(Collectors.toList());
        for(OrderWeekPK pk: orderPkList){
            OrderBill order = getOrder(pk);
            order.removeAuthor(person);
        }
    }
    
    @Override
    public boolean isManaged(Object order) {
        return manager.contains(order);
    }
    
    public void clearManager(){
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();
        manager.clear();
    }
    
}
