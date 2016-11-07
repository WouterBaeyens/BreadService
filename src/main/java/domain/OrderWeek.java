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
import java.util.ArrayList;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.MapsId;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.PrimaryKeyJoinColumns;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;

/**
 *
 * @author Wouter
 */
@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="id")
@Entity
@Table(name = "ORDERWEEK")
@NamedQueries({
    @NamedQuery(name="OrderWeek.truncate", query="delete from OrderWeek"),
    @NamedQuery(name="OrderWeek.getAll", query="select w from OrderWeek w"),
}) 
public class OrderWeek {

    @Transient
    private static final DateTimeFormatter euFormatter = DateTimeFormatter.ofPattern("dd/MM/yy");
    
    @Transient 
    private static final DateTimeFormatter euFormatter2 = DateTimeFormatter.ofPattern("dd.MM.YYYY");
    
    //@OneToOne(mappedBy="orderWeek", cascade = {CascadeType.REFRESH, CascadeType.REMOVE}, orphanRemoval = true)
    //@OneToOne(mappedBy="orderWeek")
    @OneToOne
        @PrimaryKeyJoinColumns(value = {
        @PrimaryKeyJoinColumn(name="weekNrFk", referencedColumnName = "weekNr"),
        @PrimaryKeyJoinColumn(name="yearNrFk", referencedColumnName = "yearNr")})
    private OrderBill orderBill;

    @EmbeddedId
    private OrderWeekPK orderWeekPK;

    public OrderWeek(int weekNr, int yearNr) {
        LocalDate date = getLocalDate(yearNr, weekNr, 3);
        /*if(!(weekNr == date.get(WeekFields.ISO.weekOfWeekBasedYear()))){
            String expected = "inputted week" + weekNr;
            String actual = "" + date.get(WeekFields.ISO.weekOfWeekBasedYear());
            throw new DomainException("REVISE ORDERWEEK CREATION METHOD: expected: " + expected + " actual: " + actual);
        }*/
        
        int streamlinedWeekNr = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int streamlinedYearNr = date.get(WeekFields.ISO.weekBasedYear());
        setOrderWeekPK(new OrderWeekPK(streamlinedWeekNr, streamlinedYearNr));
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
    
    public static OrderWeek createCurrentWeek(){
        LocalDate date = LocalDate.now();
        int week = date.get(WeekFields.ISO.weekOfWeekBasedYear());
        int year = date.get(WeekFields.ISO.weekBasedYear());
        return new OrderWeek(week, year);
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
        
    @JsonIgnore
    public static ArrayList<OrderWeek> getAllAcademicYearDates(){
        LocalDate date = LocalDate.now();
        int year = date.get(WeekFields.ISO.weekBasedYear());

        LocalDate firstDayOfSchool = date.withMonth(9).withDayOfMonth(1);
        if(date.isBefore(firstDayOfSchool)){
            firstDayOfSchool = firstDayOfSchool.minusYears(1);
        }
        ArrayList<OrderWeek> weeks = new ArrayList();
        for(int i = 0;i<52;i++){
            weeks.add(new OrderWeek(firstDayOfSchool.plusWeeks(i)));
        }
        return weeks;
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
       /* return "Week " + getWeekNr() + ": "
                + getLocalDate(getYearNr(), getWeekNr(), 1).format(euFormatter) + " - "
                + getLocalDate(getYearNr(), getWeekNr(), 7).format(euFormatter);
*/
       return getLocalDate(getYearNr(), getWeekNr(), 1).format(euFormatter2) + " - "
                + getLocalDate(getYearNr(), getWeekNr(), 7).format(euFormatter2);
    }

    public String toString() {
        return "Week " + getWeekNr() + ": "
                + getLocalDate(getYearNr(), getWeekNr(), 1).format(euFormatter) + " - "
                + getLocalDate(getYearNr(), getWeekNr(), 7).format(euFormatter);

    }
    
    @Override
    public boolean equals(Object obj){
        if(obj == null)
            return false;
        if(!(obj instanceof OrderWeek))
            return false;
        return ((OrderWeek) obj).getOrderWeekPK().equals(this.getOrderWeekPK());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 29 * hash + Objects.hashCode(this.orderWeekPK);
        return hash;
    }
    
}
