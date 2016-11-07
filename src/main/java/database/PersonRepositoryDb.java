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

    PersonRepositoryDb(EntityManagerFactory factory) {
        this.factory = factory;
        manager = factory.createEntityManager();
    }

    @Override
    public void addPerson(Person person) {
        Person p = getPerson(person.getId());
        if(p!=null)
            throw new DbException("[Err. already persisted: old " + p + " | new " + person + "] ");
        try{
            manager.getTransaction().begin();
            manager.persist(person);

            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("[Err. adding" + person + "]" + e.getMessage(), e);
        }
    }

    @Override
    public Person getPerson(long id) {
        Person person = null;
        try {
            person = manager.find(Person.class, id);
            return person;
        } catch (Exception e) {
            String message = person == null ? 
                    "[Err. person with id " + id + " not found]":
                    "[Err. getting person " + person + "]";
            throw new DbException(message  + e.getMessage(),e);
        }    }

    @Override
    public void updatePerson(Person person) {
            try {
            manager.getTransaction().begin();
            manager.merge(person);
            //manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. updating person " + person+ "] " + e.getMessage(),e);
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
        removeOrdersFromPerson(id);
        Person person = getPerson(id);    
        try{
            manager.getTransaction().begin();
            manager.remove(person);
            manager.getTransaction().commit();
        } catch (Exception e){
            if (manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. deleting " + person + "] " + e.getMessage(), e);
        }
            /*
               System.out.println("starting delete transaction");
            //Query q = manager.createNamedQuery("Person.FetchOrders").setParameter("id", id);
            //Person p = (Person) q.getSingleResult();

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
            System.out.println("Merging person " + person);
            manager.getTransaction().begin();
            manager.merge(person);
        } catch (Exception e1){
            throw new DbException("Error merging person" + person + "\n" + e1.getMessage(), e1);
        }
        try {
            System.out.println("Removing person " + person);
            manager.remove(person);
            


            manager.flush();
            
            manager.getTransaction().commit();
        } catch (Exception e){
            manager.getTransaction().rollback();
            throw new DbException("error deleting " + person + "\n" + e.getMessage(), e);
        }    
            System.out.println("PERSON REMOVAL SUCCESS" + person);*/
    }
    
    private void removeOrdersFromPerson(long id){
        Person person = getPerson(id);
        try{
            manager.getTransaction().begin();
            manager.refresh(person);
            List<OrderBill> orders = new ArrayList(person.getOrders());
            if(orders != null && orders.size() > 0){
                  //System.out.println("remove o-p relation. person persisted? " + manager.contains(orders[0]));
                person.removeAllOrders();
                for(OrderBill o: orders)
                    manager.merge(o);
                // update should be unnecessary since this is all done within a transactrion
                // if update needs to be added, it has to happen outside of this transaction
                //updatePerson(person); 
            }
             manager.getTransaction().commit();
        }catch (Exception e){
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. removing person-order relation for p_id = " + id + "] " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteAllPersons(){
        manager.clear();
        System.out.println("deleting all persons");    
        for(Person person: getAllPersons()){
                long id = person.getId();
                deletePerson(id);
            }
            /*
            for(Person p: getAllPersons()){
                 List<OrderBill> ordersToBeUnlinked = new ArrayList<>();
                ordersToBeUnlinked.addAll(p.getOrders());
                System.out.println("person to be deleted" + p);
                for (OrderBill order : ordersToBeUnlinked) {                
                    p.removeOrder(order);
                    System.out.println(order + "deleted");
            }    
            }
             try {
            manager.getTransaction().begin();
            manager.createNativeQuery("truncate table Order_Person").executeUpdate();
             manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException("ERROR truncating Order_Person: " + e.getMessage(), e);
        }
            try {
            manager.getTransaction().begin();
            manager.createQuery("delete from Person").executeUpdate();
             manager.getTransaction().commit();
        } catch (Exception e) {
            manager.getTransaction().rollback();
            throw new DbException(e.getMessage(), e);
        }*/
    }

    @Override
    public Set<Person> getAllPersons() {
        try {
            //manager.getTransaction().begin();
            Query query = manager.createNamedQuery("Person.getAll");
            //List<Person> persons = new ArrayList<>(query.getResultList());
            Set<Person> persons = new HashSet<>(query.getResultList());            
            //manager.getTransaction().commit();
            return persons;
        } catch (NoResultException e){
            return new HashSet<>();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. getting all persons]" + e.getMessage(),e);
        }    
    }

    @Override
    //I don't think this belongs in the repositoryDb anymore
    public List<Payment> getPaymentsForPerson(long personId) {
        Person person = getPerson(personId);
        try {          
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
    
    public boolean isManaged(Person person){
        return manager.contains(person);
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
    
    public void clearManager(){
        manager.clear();
    }
    
        public void refreshPerson(Person p){
        try {
            manager.getTransaction().begin();
            manager.refresh(p);
            manager.getTransaction().commit();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. refreshing " + p + " in PersonRep.] " + e.getMessage(),e);
        }
    }

    @Override
    public void removeRelationsToOrder(OrderBill order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
