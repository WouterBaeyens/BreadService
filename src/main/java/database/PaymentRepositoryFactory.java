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
public class PaymentRepositoryFactory {
 
     public static PaymentRepository createPaymentRepository(String type){
        if(type.toLowerCase().equals("stub")){
            return new PaymentRepositoryStub();
        }
        else
            return new PaymentRepositoryDb("BreadPU");
    }
}
