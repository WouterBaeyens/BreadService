/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
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
    
    @Override
    public void addOrder(OrderBill order) {
        OrderBill o = getOrder(order.getId());
        if(o != null)
            throw new DbException("order already persisted: " + o);
        try{
            manager.getTransaction().begin();
            manager.persist(order);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(order + " - " + e.getMessage(), e);
        }
    }

    public void testFlush(){
        System.out.println("testing flush");
        manager.getTransaction().begin();
            manager.flush();
            manager.getTransaction().commit();
         System.out.println("flush finished");
    }
    @Override
    public void deleteOrder(long orderId) {
        
            OrderBill order = getOrder(orderId);            
            //removes all existing links of this order with it's authors
            Set<Person> ps = order.getAuthors();
            order.removeAllAuthors();   
            manager.getTransaction().begin();
            manager.flush();
            manager.getTransaction().commit();
        try{
            manager.getTransaction().begin();
            manager.remove(order);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error deleting " + order + "\n" + e.getMessage(), e);
        }
    }
    

    //http://stackoverflow.com/questions/8307578/what-is-the-best-way-to-update-the-entity-in-jpa
    //in conclusion: since the entity is persistent it seemed best 
    //to just change the cost with it's setters from here.
    @Override
    public void updateOrder(long orderId, double newCost, LocalDate newDate) {
          OrderBill order = getOrder(orderId); 
        try{          
            order.setDate(newDate);
            order.setTotalCost(newCost);
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public OrderBill getOrder(long orderId) {
        try {
            OrderBill order = manager.find(OrderBill.class, orderId);
            return order;
        } catch (Exception e) {
            throw new DbException(e.getMessage(),e);

        }
    }

    @Override
    public List<OrderBill> getOrders(int week, int year){
        //Query query = sessionFactory.getCurrentSession().createQuery("from OrderBill o where o.week like :email");
        //List<User> userList = query.setParameter("email", "john%").list();
        Query query = manager.createNamedQuery("Order.findOrders", OrderBill.class);
        query.setParameter("w", week);
        query.setParameter("y", year);
        List<OrderBill> foundOrders = query.getResultList();        
        if(foundOrders == null)
            foundOrders = new ArrayList();
        return foundOrders;
    }
    

    
    @Override
    public List<OrderBill> getAllOrders() {
        try {
            Query query = manager.createNamedQuery("Order.getAll");
            List<OrderBill> orders = new ArrayList<>(query.getResultList());
            return orders;
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
            //manager.getTransaction().begin();
            //manager.createNativeQuery("truncate table OrderBill").executeUpdate();
            //delete seemed safer
            //manager.createQuery("delete from OrderBill").executeUpdate();
            //manager.getTransaction().commit();
            for(OrderBill o: getAllOrders())
                deleteOrder(o.getId());

        
    }
    
    
    
}
