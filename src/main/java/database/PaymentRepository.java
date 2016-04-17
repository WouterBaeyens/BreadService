/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface PaymentRepository {
    
    public void addPayment(Payment payment);
    
    public Payment getPayment(long paymentId);
    
    public void updatePayment(long paymentId, double newAmount, LocalDateTime newDate);
    
    public void deletePayment(long paymentId);
    
    public List<Payment> getAllPayments();
}
