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
public class OrderRepositoryFactory {
    
        public static OrderRepository createOrderRepository(String type){
        if(type.toLowerCase().equals("stub")){
            return new OrderRepositoryStub();
        }
        else
            return new OrderRepositoryDb();
    }
}
