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
        //factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        manager = factory.createEntityManager();
    }

    @Override
    public void addPerson(Person person) {
        Person p = getPerson(person.getId());
        if(p!=null)
            throw new DbException("person already persisted: " + p);
        try{
            manager.getTransaction().begin();
            manager.persist(person);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("Error adding" + p + "\n" + e.getMessage(), e);
        }
    }

    @Override
    public Person getPerson(long id) {
        try {
            Person person = manager.find(Person.class, id);
            return person;
        } catch (Exception e) {
            throw new DbException(e.getMessage(),e);

        }    }

    @Override
    public void updatePerson(long id, String newName) {
        Person person = getPerson(id); 
        try{
            person.setName(newName);
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }    }

    @Override
    //removing a person will be seen as invalidating the bills, since it ajusts the cost for the other persons retroactively
    //later on, either a substitute for bill has to be found, or persons should only be archived to avoid this problem
    //since this is not the owner class of the relationship person-order
    //the order references have to be updated manually.
    
    //note: if you flush, 
            //testDeletePerson() (in PersonRepositoryTest)  throwns an exception
            //It seems to be a problem with synchronisation, 
            //but i would like to here the exact reason for this if anyone knows.
    public void deletePerson(long id) {
        Person person = getPerson(id);    
        
            
               System.out.println("starting delete transaction");
            /*Query q = manager.createNamedQuery("Person.FetchOrders").setParameter("id", id);
            Person p = (Person) q.getSingleResult();*/

            //To avoid removing items from the list over which i am iterating
            List<OrderBill> ordersToBeUnlinked = new ArrayList<>();
            ordersToBeUnlinked.addAll(person.getOrders());
            
            System.out.println("person to be deleted" + person);
            for (OrderBill order : ordersToBeUnlinked) {
                
                person.removeOrder(order);
                System.out.println(order + "deleted");
            }         
            
            System.out.println(person + " ready for removal");

        try{       
            manager.getTransaction().begin();
            manager.merge(person);
            manager.remove(person);
            


            manager.flush();
            
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error deleting " + person + "\n" + e.getMessage(), e);
        }    
    }
    
    @Override
    public void deleteAllPersons(){
            for(Person person: getAllPersons()){
                long id = person.getId();
                deletePerson(id);
            }
    }

    @Override
    public Set<Person> getAllPersons() {
        try {
            Query query = manager.createNamedQuery("Person.getAll");
            Set<Person> persons = new HashSet<>(query.getResultList());
            //Set<Person> persons = new HashSet<>();            
            return persons;
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
    
    public void testFlush(){
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();
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
