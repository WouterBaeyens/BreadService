/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package domain;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Wouter
 */
@Embeddable
public class OrderWeekPK implements Serializable{
    @Column(name="yearNr")
    private int yearNr;
    @Column(name="weekNr")
    private int weekNr;
    
    public OrderWeekPK(){}
    
    public OrderWeekPK(int weekNr, int yearNr){
        setWeekNr(weekNr);
        setYearNr(yearNr);
    }
    
    
    @Override
    public boolean equals(Object obj){
        if(obj instanceof OrderWeekPK){
            OrderWeekPK weekPK = (OrderWeekPK) obj;
            if(weekPK.getWeekNr() == this.getWeekNr() && weekPK.getYearNr() == this.getYearNr())
                return true;
                }
        return false;
    }
    
    @Override
    public int hashCode(){
        return yearNr * 100 + weekNr;
    }
    
    public int getYearNr(){
        return yearNr;
    }
    
    public void setYearNr(int yearNr){
        this.yearNr = yearNr;
    }
    
    public int getWeekNr(){
        return weekNr;
    }
    
    public void setWeekNr(int weekNr){
        this.weekNr = weekNr;
    }
    
    public String toString(){
        return "key: (" + getWeekNr() + " " + getYearNr() + ")";
    }
}
