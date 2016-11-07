/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Wouter
 */
public class GeneralRepositoryFactory {

    static private EntityManagerFactory factory = Persistence.createEntityManagerFactory("BreadPU");

    
    public static OrderRepository createOrderRepository(String type) {
        if (type.toLowerCase().equals("stub")) {
            return new OrderRepositoryStub();
        } else if(type.toLowerCase().equals("combi_jpa")) {
            return new OrderRepositoryDb(factory);
        } else {
            return new OrderRepositoryDb("BreadPU");
        }
    }

    public static OrderWeekRepository createOrderWeekRepository(String type) {
        if (type.toLowerCase().equals("stub")) {
            //return new OrderWeekRepositoryStub();
            return null;
        } else if(type.toLowerCase().equals("combi_jpa")) {
            return new OrderWeekRepositoryDb(factory);   
        } else {
            return new OrderWeekRepositoryDb("BreadPU");
        }
    }

    public static PersonRepository createPersonRepository(String type) {
        if (type.toLowerCase().equals("stub")) {
            return new PersonRepositoryStub();
        } else if(type.toLowerCase().equals("combi_jpa")) {
                        return new PersonRepositoryDb(factory);
        } else {
            return new PersonRepositoryDb("BreadPU");
        }
    }

    public static PaymentRepository createPaymentRepository(String type) {
        if (type.toLowerCase().equals("stub")) {
            return new PaymentRepositoryStub();
           
        } else if(type.toLowerCase().equals("combi_jpa")) {
            return new PaymentRepositoryDb(factory);
        }
        else {
            return new PaymentRepositoryDb("BreadPU");
        }
    }

}
