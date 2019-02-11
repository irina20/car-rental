package org.irina.carrental.dto;

import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
public class RentalRequest {

    @Id
    @GeneratedValue
    private Long id;

    private Long carId;

    private String customerName;

    private String customerAddress;

    private String startDate;

    private String endDate;

    public RentalRequest() {}

    public RentalRequest(Long carId, String customerName, String customerAddress, String startDate, String endDate) {
        this.carId = carId;
        this.customerName = customerName;
        this.customerAddress = customerAddress;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

}
