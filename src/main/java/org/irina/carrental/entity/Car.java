package org.irina.carrental.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
public class Car {

    @Id
    @GeneratedValue
    private Long id;

    private String carClass;

    private String carModel;

    private BigDecimal dailyRate;

    public Car() {}

    public Car(String carClass, String carModel, BigDecimal dailyRate) {
        this.carClass = carClass;
        this.carModel = carModel;
        this.dailyRate = dailyRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCarClass() {
        return carClass;
    }

    public void setCarClass(String carClass) {
        this.carClass = carClass;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public BigDecimal getDailyRate() {
        return dailyRate;
    }

    public void setDailyRate(BigDecimal dailyRate) {
        this.dailyRate = dailyRate;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
