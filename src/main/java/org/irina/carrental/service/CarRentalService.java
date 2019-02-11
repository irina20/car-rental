package org.irina.carrental.service;

import org.irina.carrental.entity.Car;
import org.irina.carrental.entity.Customer;
import org.irina.carrental.entity.Rental;

import java.time.LocalDateTime;
import java.util.Set;

public interface CarRentalService {
    Set<Car> getAvailableCarsForTimePeriod(LocalDateTime startDate, LocalDateTime endDate);
    Customer validateRentalRequest(Customer customer, LocalDateTime startDate, LocalDateTime endDate);
    Rental rentCar(Long carId, Long customerId, LocalDateTime startDate, LocalDateTime endDate);
}
