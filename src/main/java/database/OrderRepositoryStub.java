/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.OrderBill;
import domain.OrderWeekPK;
import domain.Person;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

/**
 *
 * @author Wouter
 */
public class OrderRepositoryStub implements OrderRepository{

    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    private Map<OrderWeekPK, OrderBill> orders;
    
    public OrderRepositoryStub(){
        orders = new HashMap<OrderWeekPK, OrderBill>();
        
    }
    
    public void testFlush(){
    }
    
    //in this stub an object can be added twice, since the id and the
    //key of the map are not directly linked
    //ie: when order is added a second time, the id of order will be changed
    //    and it will be assigned to a second key (which has the updated id value)
    @Override
    public void addOrder(OrderBill order) {
    }

    @Override
    public void deleteOrder(OrderWeekPK orderId) {
        if(!orders.containsKey(orderId))
            throw new DbException("no order with  this id (" + orderId + ") was found.");
        orders.remove(orderId);
    }

    @Override
    public OrderBill getOrder(OrderWeekPK orderId) {
        if(!orders.containsKey(orderId))
            throw new DbException("no order with  this id (" + orderId + ") was found.");
        return orders.get(orderId);
    }
    
    /*@Override
    public List<OrderBill> getOrders(int week, int year){
        List<OrderBill> foundOrders = orders.values().stream().filter(o -> o.getWeekNr() == week && o.getYearNr() == year).collect(Collectors.toList());
        return foundOrders;
    }*/
    
    /*
    public void updateOrder(long orderId, double newCost) {
        OrderBill order = getOrder(orderId);
        order.setTotalCost(newCost);
        //order.setDate(newDate);
    }*/
    
    @Override
    public List<OrderBill> getAllOrders() {
        List<OrderBill> orderList = new ArrayList<>();
        orderList.addAll(orders.values());
        return orderList;
    }

    @Override
    public void closeConnection() throws DbException {
        System.out.println("imitates a real database and acts like it's really closing a connection");
    }

    @Override
    public void deleteAllOrders() {
        orders.clear();
    }

    @Override
    public OrderBill updateOrder(OrderBill order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void refreshOrder(OrderBill order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean isManaged(Object order) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void clearManager() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeRelationsToPerson(Person person) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
