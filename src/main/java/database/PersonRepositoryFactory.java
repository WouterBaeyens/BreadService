/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

/**
 *
 * @author Wouter
 */
public class PersonRepositoryFactory {
    
     public static PersonRepository createPersonRepository(String type){
        if(type.toLowerCase().equals("stub")){
            return new PersonRepositoryStub();
        } else if(type.toLowerCase().equals("test")){
            return new APersonTestDb("BreadPU");
        }
        else
            return new PersonRepositoryDb("BreadPU");
    }
}
