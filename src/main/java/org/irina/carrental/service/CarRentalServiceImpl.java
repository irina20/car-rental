package org.irina.carrental.service;

import org.irina.carrental.entity.Car;
import org.irina.carrental.entity.Customer;
import org.irina.carrental.entity.Rental;
import org.irina.carrental.repository.CarRepository;
import org.irina.carrental.repository.CustomerRepository;
import org.irina.carrental.repository.RentalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class CarRentalServiceImpl implements CarRentalService {

    private static final Logger log = LoggerFactory.getLogger(CarRentalServiceImpl.class);

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private RentalRepository rentalRepository;

    @Transactional(readOnly = true)
    public Set<Car> getAvailableCarsForTimePeriod(LocalDateTime startDate, LocalDateTime endDate) {
        // find all rented cars for the specified period
        Set<Rental> rentedCars = rentalRepository.getAllByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate);

        // get ids of the rented cars
        Set<Long> rentedCarIds = getRentedCarIds(rentedCars);

        // get cars of the fleet that have not been rented out for the time period specified
        Set<Car> availableCars = carRepository.findByIdNotIn(rentedCarIds);
        log.info("Available Cars: " + availableCars.toString());
        return availableCars;
    }

    protected Set<Long> getRentedCarIds(Set<Rental> rentedCars) {
        return rentedCars
                    .stream()
                    .map(Rental::getCarId)
                    .collect(Collectors.toSet());
    }


    public Customer validateRentalRequest(Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        // find customer
        Customer existingCustomer = customerRepository.findByNameAndAddress(customer.getName(), customer.getAddress());

        if (existingCustomer == null) { // new customer is allowed to rent a car
            // save new customer
            Customer customerSaved =  customerRepository.save(customer);
            log.info("Valid rental request for new customer: " + customerSaved.toString());
            return customerSaved;
        }

        // find all rented cars for the specified period
        Set<Rental> rentedCarsForTimePeriod = rentalRepository.getAllByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate);

        if (isValidRentalRequestForTimePeriodForExistingCustomer(rentedCarsForTimePeriod, existingCustomer)) {
            return existingCustomer;
        }

        return null;
    }


    // For a known customer, check if the customer already rented a car for the time period that overlaps with the newly desired time period
    protected boolean isValidRentalRequestForTimePeriodForExistingCustomer(Set<Rental> rentedCarsForTimePeriod, Customer customer) {
        boolean isCustomerRentedAlreadyForTheTimePeriod = isCustomerRentedAlreadyForTheTimePeriod(rentedCarsForTimePeriod, customer);

        if (isCustomerRentedAlreadyForTheTimePeriod) {
            log.info("Invalid rental request. Can't rent multiple cars to the same customer for overlapping time periods. Customer: " + customer.toString());
            return false;
        }

        log.info("Valid rental request for customer: " + customer.toString());
        return true;
    }

    protected boolean isCustomerRentedAlreadyForTheTimePeriod(Set<Rental> rentedCarsForTimePeriod, Customer customer) {
        return rentedCarsForTimePeriod
                    .stream()
                    .anyMatch(rental -> rental.getCustomerId().equals(customer.getId()));
    }


    public Rental rentCar(Long carId, Long customerId, LocalDateTime startDate, LocalDateTime endDate){
        Rental rental = new Rental(carId, customerId, startDate, endDate);
        Rental reservedCar = rentalRepository.save(rental);
        log.info("Reserved car: " + reservedCar.toString());
        return reservedCar;
    }
}
