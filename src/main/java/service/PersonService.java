/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DbException;
import database.PersonRepository;
import database.PersonRepositoryFactory;
import domain.Order;
import domain.Person;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public class PersonService {
    
        private PersonRepository repository;

        public PersonService(String repositoryType){
            this.repository = PersonRepositoryFactory.createPersonRepository(repositoryType);
        }
        
            /*adds a person to keep track of / store */
    public void addPerson(Person person){
        repository.addPerson(person);
        
    }
    
    /*returns the person with the given name*/
    public Person getPerson(long id){
        try{
            return repository.getPerson(id);
        } catch (DbException ex){
            throw new ServiceException(ex.getMessage());
        }
    }
    
    /*returns all the persons that are stored*/
    public Set<Person> getAllPersons(){
        return repository.getAllPersons();
    }
        /*returs the group of people whom have made a certain order*/
    public Set<Person> getPersonsWithOrder(long orderId){
        return repository.getAllPersonsForOrder(orderId);
    }

}
