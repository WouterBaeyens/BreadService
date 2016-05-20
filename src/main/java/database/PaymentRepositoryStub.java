/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Payment;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Wouter
 */
public class PaymentRepositoryStub implements PaymentRepository{

    private static AtomicLong nextId = new AtomicLong();
    Map<Long, Payment> payments;

    PaymentRepositoryStub() {
        payments = new HashMap<>();
    }
    
    @Override
    public void addPayment(Payment payment) {
        payment.setId(nextId.incrementAndGet());
        if(payments.containsKey(payment.getId()))
            throw new DbException("payment with this id (" + payment.getId() + ") already exists.");
        payments.put(payment.getId(), payment);
    }

    @Override
    public Payment getPayment(long paymentId) {
            if(!payments.containsKey(paymentId))
            throw new DbException("payment with this id (" + paymentId + ") was not found.");
        return payments.get(paymentId);
    }

    @Override
    public void updatePayment(long paymentId, double newAmount, LocalDate newDate) {
        Payment payment = getPayment(paymentId);
        payment.setDate(newDate);
        payment.setAmount(newAmount);
    }

    @Override
    public void deletePayment(long paymentId) {
        if(!payments.containsKey(paymentId))
            throw new DbException("payment with this id (" + paymentId + ") was not found.");
        payments.remove(paymentId);
    }

    @Override
    public List<Payment> getAllPayments() {
        List<Payment> paymentList = new ArrayList<>();
        paymentList.addAll(payments.values());
        return paymentList;
    }
    
    @Override
    public void closeConnection() throws DbException {
        System.out.println("imitates a real database and acts like it's really closing a connection");
    }

    @Override
    public void deleteAllPayments() {
        payments.clear();
    }
}
