/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 *
 * @author Wouter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@Entity
@Table(name = "ORDERWEEK")
@NamedQueries({
    @NamedQuery(name="OrderWeek.getAll", query="select w from OrderWeek w"),
}) 
public class OrderWeek {

    @Transient
    private static final DateTimeFormatter euFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");

    @OneToOne(mappedBy="orderWeek", cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    private OrderBill orderBill;

    @EmbeddedId
    private OrderWeekPK orderWeekPK;

    public OrderWeek(int weekNr, int yearNr) {
        setOrderWeekPK(new OrderWeekPK(weekNr, yearNr));
    }

    public OrderWeek() {
    }

    public OrderWeek(OrderBill orderBill, int weekNr, int yearNr) {
        this(weekNr, yearNr);
        setOrder(orderBill);
    }
    
    public OrderWeek(LocalDate date){
        this(date.get(WeekFields.ISO.weekOfWeekBasedYear()), date.get(WeekFields.ISO.weekBasedYear()));
    }
    
    public void setOrderWeekPK(OrderWeekPK orderWeekPK){
        this.orderWeekPK = orderWeekPK;
    }

    public OrderWeekPK getOrderWeekPK(){
        return orderWeekPK;
    }
    
    public int getWeekNr() {
        return orderWeekPK.getWeekNr();
    }

    public int getYearNr() {
        return orderWeekPK.getYearNr();
    }
    
    public int getId(){
        return orderWeekPK.hashCode();
    }

    public OrderBill getOrderBill() {
        return orderBill;
    }

    public void setOrder(OrderBill order) {
        this.orderBill = order;
        if(order != null){
        if (order.getOrderWeek() == null) {
            order.setOrderWeek(this);
            order.setOrderPK(orderWeekPK);
        } else if (order.getOrderWeek() != this) {
            throw new DomainException("Err: order already linked to another week: (" + order + " | current: " + order.getOrderWeek() + "|new: "+ this + ")");
        }}
    }
        
    public void removeOrderBill() {
        OrderBill o = orderBill;
        orderBill = null;
        if (o != null && o.getOrderWeek() != null) {
            o.removeOrderWeek();
        }     
    }

    private static LocalDate getLocalDate(int yearNr, int weekNr, int dayNr) {
        LocalDate date = LocalDate.now().withYear(yearNr)
                .with(WeekFields.ISO.weekOfYear(), weekNr)
                .with(WeekFields.ISO.dayOfWeek(), dayNr);
        return date;
    }
    
   @JsonIgnore
    public LocalDate getDate(){
        return getLocalDate(getYearNr(), getWeekNr(), 1);
        //return date;
    }
    
    public String getFormattedDate() {
        return "Week " + getWeekNr() + ": "
                + getLocalDate(getYearNr(), getWeekNr(), 1).format(euFormatter) + " - "
                + getLocalDate(getYearNr(), getWeekNr(), 7).format(euFormatter);

        //return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    public String toString() {
        return "Week " + getWeekNr() + ": "
                + getLocalDate(getYearNr(), getWeekNr(), 1).format(euFormatter) + " - "
                + getLocalDate(getYearNr(), getWeekNr(), 7).format(euFormatter);

    }
}
