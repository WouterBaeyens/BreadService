/*
1.6.24
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.Payment;
import domain.Person;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
public class APersonTestDb implements PersonRepository {

    private EntityManagerFactory factory;
    private EntityManager manager;
    
    public APersonTestDb(String namePersistenceUnit){
        //factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
        factory = Persistence.createEntityManagerFactory(namePersistenceUnit);
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
        }    }
    
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
        }       }

    @Override
    public void updatePerson(Person person) {
     try {
            manager.getTransaction().begin();
            manager.merge(person);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e) {
            if(manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. updating person " + person+ "] " + e.getMessage(),e);
        }        }

    @Override
    public void deletePerson(long id) {
        Person person = getPerson(id);  
        if(person.getOrders().size() > 0)
            throw new DbException("[Err. remove all the orders from person " + person + " before deleting]");
        try{
            manager.getTransaction().begin();
            manager.remove(person);
            manager.flush();
            manager.getTransaction().commit();
        } catch (Exception e){
            if (manager.getTransaction().isActive())
                manager.getTransaction().rollback();
            throw new DbException("[Err. deleting " + person + "] " + e.getMessage(), e);
        }
    }


    @Override
    public void deleteAllPersons() {
        System.out.println("deleting all persons");    
        for(Person person: getAllPersons()){
                long id = person.getId();
                deletePerson(id);
            }    
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
        public void removeRelationsToOrder(OrderBill order){
        List<Long> personIdList = order.getAuthors().stream().map(o->o.getId()).collect(Collectors.toList());
        for(Long pk: personIdList){
            Person person = getPerson(pk);
            person.removeOrder(order);
        }
    }

    @Override
    public List<Payment> getPaymentsForPerson(long personId) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void testFlush() {
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();    }

    @Override
    public boolean isManaged(Person person) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void closeConnection() throws DbException {
        try{
        manager.close();
        factory.close();
        } catch (Exception e){
            throw new DbException(e.getMessage(), e);
        }     }

    @Override
    public void refreshPerson(Person p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearManager() {
        manager.getTransaction().begin();
        manager.flush();
        manager.getTransaction().commit();
        manager.clear();
    }
    
}
