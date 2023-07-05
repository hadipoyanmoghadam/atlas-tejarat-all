package dpi.atlas.model.tj.entity;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 1, 2008
 * Time: 12:59:57 PM
 * To change this template use File | Settings | File Templates.
 */
public class Wage implements Serializable {

    String wageType;

    public String toString() {
//        return super.toString();    //To change body of overridden methods use File | Settings | File Templates.
        return ("wageType=" + this.wageType + "-- " + "wagePercent=" + this.wagePercent + "-- " + "wageFee=" + this.wageFee + "-");
    }

    Float wagePercent;
    Long wageFee;

    public Wage(String wageType, Float wagePercent, Long wageFee) {
        this.wageType = wageType;
        this.wagePercent = wagePercent;
        this.wageFee = wageFee;
    }


    public String getWageType() {
        return wageType;
    }

    public void setWageType(String wageType) {
        this.wageType = wageType;
    }

    public Float getWagePercent() {
        return wagePercent;
    }

    public void setWagePercent(Float wagePercent) {
        this.wagePercent = wagePercent;
    }

    public Long getWageFee() {
        return wageFee;
    }

    public void setWageFee(Long wageFee) {
        this.wageFee = wageFee;
    }


}
