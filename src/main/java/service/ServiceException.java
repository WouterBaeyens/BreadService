/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package service;

/**
 *
 * @author Wouter
 */
public class ServiceException extends RuntimeException{


    
    public ServiceException(){
        super();
    }
    
    public ServiceException(String message){
        super(message);
    }
    
    public ServiceException(String message, Throwable exception){
        super(message, exception);
    }
    
    public ServiceException(Throwable exception){
        super(exception);
    }
}
