/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ui;

import domain.Person;
import service.BreadServiceFacade;
import service.Service;

/**
 *
 * @author Wouter
 */
public class TestService {
    public static void main(String[] args){
        Person person = new Person("Jan Sterckx");
        Service service = new BreadServiceFacade("JPA");
        service.addPerson(person);
        System.out.println(service.getAllPersons());
        service.closeConnections();
    }
}
