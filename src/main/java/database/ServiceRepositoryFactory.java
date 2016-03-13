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
public class ServiceRepositoryFactory {
    
    public static ServiceRepositoryInterface createServiceRepository(String type){
        if(type.toLowerCase().equals("stub")){
            return new ServiceRepositoryStub();
        }
        else
            return new ServiceRepositoryDb();
    }
}
