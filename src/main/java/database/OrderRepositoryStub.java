/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import domain.Order;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 *
 * @author Wouter
 */
public class OrderRepositoryStub implements OrderRepository{

    //ensures the incrementing code is atomic
    //so objects created at same time -> not the same id
    private static AtomicLong nextId = new AtomicLong();
    private Map<Long, Order> orders;
    
    public OrderRepositoryStub(){
        orders = new HashMap<Long, Order>();
        
    }
    
    //in this stub an object can be added twice, since the id and the
    //key of the map are not directly linked
    //ie: when order is added a second time, the id of order will be changed
    //    and it will be assigned to a second key (which has the updated id value)
    @Override
    public void addOrder(Order order) {
        order.setId(nextId.incrementAndGet());
        if(orders.containsKey(order.getId()))
            throw new DbException("order with this id (" + order.getId() + ") already exists");
        orders.put(order.getId(), order);
    }

    @Override
    public void deleteOrder(long orderId) {
        if(!orders.containsKey(orderId))
            throw new DbException("no order with  this id (" + orderId + ") was found.");
        orders.remove(orderId);
    }

    @Override
    public Order getOrder(long orderId) {
        if(!orders.containsKey(orderId))
            throw new DbException("no order with  this id (" + orderId + ") was found.");
        return orders.get(orderId);
    }
    
        @Override
    public void updateOrder(long orderId, double newCost, LocalDateTime newDate) {
        Order order = getOrder(orderId);
        order.setTotalCost(newCost);
        order.setDate(newDate);
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orderList = new ArrayList<>();
        orderList.addAll(orders.values());
        return orderList;
    }
    
}
