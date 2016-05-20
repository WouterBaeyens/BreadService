/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Wouter
 */
public interface PaymentRepository {
    
    public void addPayment(Payment payment);
    
    public Payment getPayment(long paymentId);
    
    public void updatePayment(long paymentId, double newAmount, LocalDate newDate);
    
    public void deletePayment(long paymentId);
    
    public void deleteAllPayments();
    
    public List<Payment> getAllPayments();

    public void closeConnection() throws DbException;

}
