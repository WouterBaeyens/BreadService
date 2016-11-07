/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.GeneralRepositoryFactory;
import database.PaymentRepository;
import database.PaymentRepositoryFactory;
import domain.Payment;

/**
 *
 * @author Wouter
 */
public class PaymentService {
    
        private PaymentRepository repository;
        
        public PaymentService(String repositoryType){
            this.repository = PaymentRepositoryFactory.createPaymentRepository(repositoryType);
            //this.repository = GeneralRepositoryFactory.createPaymentRepository(repositoryType);
        }
        
        public void addPayment(Payment payment){
            repository.addPayment(payment);
        }
        
        public void deleteAllPayments(){
            repository.deleteAllPayments();
        }

    void closeConnection() {
        repository.closeConnection();
    }

}
