/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.Payment;
import domain.Person;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author Wouter
 */
class PersonRepositoryDb implements PersonRepository {
    private EntityManagerFactory factory;
    private EntityManager manager;
    
    public PersonRepositoryDb(String namePersistenceUnit){
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }

    @Override
    public void addPerson(Person person) {
        try{
            manager.getTransaction().begin();
            manager.persist(person);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public Person getPerson(long id) {
        try {
            manager.getTransaction().begin();
            Person person = manager.find(Person.class, id);
            manager.flush();
            manager.getTransaction().commit();
            return person;
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(),e);

        }    }

    @Override
    public void updatePerson(long id, String newName) {
         try{
            Person person = getPerson(id);
            person.setName(newName);
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    //removing a person will be seen as invalidating the bills, since it ajusts the cost for the other persons retroactively
    //later on, either a substitute for bill has to be found, or persons should only be archived to avoid this problem
    //since this is not the owner class of the relationship person-order
    //the order references have to be updated manually.
    public void deletePerson(long id) {
            try{
            Person person = getPerson(id);
            manager.getTransaction().begin();
            manager.remove(person);  
            //To avoid removing items from the list over which i am iterating
            List<OrderBill> ordersToBeRemoved = new ArrayList<>();
            ordersToBeRemoved.addAll(person.getOrders());
            for (OrderBill order : ordersToBeRemoved) {
                person.removeOrder(order);
            }         
            /*List<Payment> paymentsToBeRemoved = new ArrayList<>();
            paymentsToBeRemoved.addAll(person.getPayments());
            for(Payment payment: paymentsToBeRemoved){
                person.removePayment(payment);
            }*/
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }    
    }
    
    @Override
    public void deleteAllPersons(){
                try {
            for(Person person: getAllPersons()){
                deletePerson(person.getId());
            }
        } catch (Exception e) {
            throw new DbException(e.getMessage(), e);
        }
    }

    @Override
    public Set<Person> getAllPersons() {
        try {
            Query query = manager.createNamedQuery("Person.getAll");
            return new HashSet<>(query.getResultList());
        } catch (NoResultException e){
            return new HashSet<>();
        } catch (Exception e) {
            throw new DbException(e.getMessage(),e);
        }    
    }

    @Override
    public List<Payment> getPaymentsForPerson(long personId) {
        try {
            Person person = getPerson(personId);
            List<Payment> paymentList = new ArrayList<>();
            paymentList.addAll(person.getPayments());
            return paymentList;
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
