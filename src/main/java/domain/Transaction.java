/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.time.LocalDateTime;

/**
 *
 * @author Wouter
 */
public interface Transaction extends Comparable<Transaction>{
    
    
    
    /*
    public double getTransactionValue();
    */
    public LocalDateTime getDate();    
    
    
}
