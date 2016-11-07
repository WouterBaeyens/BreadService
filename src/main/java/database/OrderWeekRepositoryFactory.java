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
public class OrderWeekRepositoryFactory {
    
        
        public static OrderWeekRepository createOrderWeekRepository(String type){
        if(type.toLowerCase().equals("stub")){
            //return new OrderWeekRepositoryStub();
            return null;
        } else if(type.toLowerCase().equals("test")){
            return new AWeekTestDb("BreadPU");
        }
        else
            return new OrderWeekRepositoryDb("BreadPU");
    }
}
