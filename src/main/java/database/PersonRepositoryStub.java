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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Wouter
 */
public class PersonRepositoryStub implements PersonRepository{
    
    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    private static AtomicLong nextId = new AtomicLong();
    private Map<Long, Person> persons;

    public PersonRepositoryStub(){
        persons = new HashMap<Long, Person>();
    }
    
    @Override
    public void addPerson(Person person) {
        person.setId(nextId.incrementAndGet());
        if(persons.containsKey(person.getId()))
            throw new DbException("A person with this id ("+ person.getId() + ") already exists.");
        persons.put(person.getId(), person);
    }

    @Override
    public Person getPerson(long id) {
        if(!persons.containsKey(id))
           throw new DbException("No person with this id ("+ id + ") was found.");
       return persons.get(id);
    }
    
        @Override
    public void updatePerson(Person person) {
        persons.put(person.getId(), person);
    }

    @Override
    public void deletePerson(long id) {
        if(!persons.containsKey(id))
           throw new DbException("No person with this id ("+ id + ") was found.");
        persons.remove(id);
    }
    
    @Override
    public void deleteAllPersons(){
        persons.clear();
    }

    @Override
    public Set<Person> getAllPersons() {
        Set<Person> personList = new HashSet<>();
        personList.addAll(persons.values());
        return personList;
    }

    /*@Override
    public Set<Person> getAllPersonsForOrder(long orderId) {
        Set<Person> registeredPersons = new HashSet<>();
        for(Person person: persons.values()){
            if(person.isRegisteredToOrder(orderId))
                registeredPersons.add(person);
        }
        return registeredPersons;
    }*/

    @Override
    public List<Payment> getPaymentsForPerson(long personId) {
        List<Payment> paymentList = new ArrayList<>();
        paymentList.addAll(persons.get(personId).getPayments());
        return paymentList;
    }
    
    public void testFlush(){}
    
@Override
    public void closeConnection() throws DbException {
        System.out.println("imitates a real database and acts like it's really closing a connection");
    }


    @Override
    public boolean isManaged(Person person) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refreshPerson(Person p) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeRelationsToOrder(OrderBill order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
