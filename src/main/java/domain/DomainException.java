/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

/**
 *
 * @author Wouter
 */
public class DomainException extends RuntimeException{


    
    public DomainException(){
        super();
    }
    
    public DomainException(String message){
        super(message);
    }
    
    public DomainException(String message, Throwable exception){
        super(message, exception);
    }
    
    public DomainException(Throwable exception){
        super(exception);
    }
}
