/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.DbException;
import database.PersonRepository;
import database.PersonRepositoryFactory;
import domain.OrderBill;
import domain.Person;
import domain.Transaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    
    public void deleteAllPersons(){
        repository.deleteAllPersons();
    }

    public List<Transaction> getSortedTransactionsForPerson(long personId) {
        List<Transaction> transactions = new ArrayList<>();
        Person person = repository.getPerson(personId);
        transactions.addAll(person.getOrders());
        transactions.addAll(person.getPayments());
        Collections.sort(transactions);
        return transactions;
    }

    void closeConnection() {
        repository.closeConnection();
    }
}
