/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
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
class PaymentRepositoryDb implements PaymentRepository {

    private EntityManagerFactory factory;
    private EntityManager manager;
    
    public PaymentRepositoryDb(String namePersistenceUnit){
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }
    @Override
    public void addPayment(Payment payment) {
        System.out.println("WARNING: payment added manually, this can cause a persitence error if linked to a person when it is managed through cascade!");
        try{
            manager.getTransaction().begin();
            manager.persist(payment);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    public Payment getPayment(long paymentId) {
        try {
            manager.getTransaction().begin();
            Payment payment = manager.find(Payment.class, paymentId);
            manager.flush();
            manager.getTransaction().commit();
            return payment;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(),e);

        }    }

    @Override
    public void updatePayment(long paymentId, double newAmount, LocalDate newDate) {
         try{
            Payment payment = getPayment(paymentId);
            payment.setDate(newDate);
            payment.setAmount(newAmount);
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    public void deletePayment(long paymentId) {
        try{
            Payment payment = getPayment(paymentId);
            manager.getTransaction().begin();
            manager.remove(payment);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    public List<Payment> getAllPayments() {
        try {
            Query query = manager.createNamedQuery("Payment.getAll");
            return query.getResultList();
        } catch (NoResultException e){
            return new ArrayList<>();
        } catch (Exception e) {
            throw new DbException(e.getMessage(),e);
        }    
    }
    
        @Override
    public void deleteAllPayments() {
        try {
            manager.getTransaction().begin();
            manager.createQuery("delete from Payment").executeUpdate();
            manager.getTransaction().commit();
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e);
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
}
