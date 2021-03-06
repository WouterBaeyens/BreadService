/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.Payment;
import domain.Person;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface PersonRepository {
    
    
    /*register a new person (=add a new person to the repository)*/
    public void addPerson(Person person);

    /*Returns the person with the given email*/
    public Person getPerson(long id);
    
    public void updatePerson(Person person);
    
    public void deletePerson(long id);
    
    public void deleteAllPersons();
    
    /*Returns all the persons that are registered (=currently stored on the repository)*/
    public Set<Person> getAllPersons();
    
    /*Returns all the payments for a person*/
    public List<Payment> getPaymentsForPerson(long personId);
 
    public void removeRelationsToOrder(OrderBill order);

    
    public void testFlush();
    
    public boolean isManaged(Person person);
    
    public void closeConnection() throws DbException;
    
    public void refreshPerson(Person p);
    
    public void clearManager();
        
}
