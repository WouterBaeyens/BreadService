/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

import database.PaymentRepository;
import database.PaymentRepositoryFactory;

/**
 *
 * @author Wouter
 */
public class PaymentService {
    
        private PaymentRepository repository;
        
        public PaymentService(String repositoryType){
            PaymentRepositoryFactory.createPaymentRepository(repositoryType);
        }

}
