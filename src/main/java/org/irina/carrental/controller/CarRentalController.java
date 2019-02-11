package org.irina.carrental.controller;

import org.irina.carrental.dto.RentalRequest;
import org.irina.carrental.entity.Car;
import org.irina.carrental.entity.Customer;
import org.irina.carrental.entity.Rental;
import org.irina.carrental.service.CarRentalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/car/rentals")
public class CarRentalController {

    @Autowired
    private CarRentalService carRentalService;


    @GetMapping("/available")
    public ResponseEntity<Set<Car>> getAvailableCarsForTimePeriod(@RequestParam Map<String, String> queryMap) {
        Set<Car> availableCarsList = carRentalService.getAvailableCarsForTimePeriod(
                LocalDateTime.parse(queryMap.get("startDate")),
                LocalDateTime.parse(queryMap.get("endDate")));
        return new ResponseEntity<>(availableCarsList, HttpStatus.OK);
    }


    @PostMapping("/reserve")
    public ResponseEntity<Rental> reserveCarForTimePeriod(@RequestBody RentalRequest rentalRequest) {
        Customer customer = new Customer(rentalRequest.getCustomerName(), rentalRequest.getCustomerAddress());

        LocalDateTime startDate = LocalDateTime.parse(rentalRequest.getStartDate());
        LocalDateTime endDate = LocalDateTime.parse(rentalRequest.getEndDate());

        Customer validatedCustomer = carRentalService.validateRentalRequest(customer, startDate, endDate);

        if (validatedCustomer == null) {
            return new ResponseEntity<>(null, HttpStatus.UNAVAILABLE_FOR_LEGAL_REASONS);
        }

        Rental rentedCar = carRentalService.rentCar(rentalRequest.getCarId(), validatedCustomer.getId(), startDate, endDate);
        return new ResponseEntity<>(rentedCar, HttpStatus.CREATED);
    }

}
