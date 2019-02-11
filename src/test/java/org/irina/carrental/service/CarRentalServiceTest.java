package org.irina.carrental.service;

import org.irina.carrental.entity.Customer;
import org.irina.carrental.entity.Rental;
import org.irina.carrental.repository.CarRepository;
import org.irina.carrental.repository.CustomerRepository;
import org.irina.carrental.repository.RentalRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.*;

@SpringBootTest
public class CarRentalServiceTest {

    @InjectMocks
    private CarRentalServiceImpl carRentalService;  // to test functionality that is not exposed via interface

    @Mock
    private CarRepository carRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CustomerRepository customerRepository;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
    }


    private Customer getCustomer(Long customerId) {
        String customerName = "Customer Name";
        String customerAddress = "Customer Address";
        Customer customer = new Customer(customerName, customerAddress);
        customer.setId(customerId);
        return customer;
    }


    private Rental getRentalForCustomerForTimePeriod(Long carId, Customer customer, LocalDateTime startDate, LocalDateTime endDate) {
        return new Rental(carId, customer.getId(), startDate, endDate);
    }


    @Test
    public void testGetAvailableCarsForTimePeriod() {
        LocalDateTime startDate = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);

        // call service
        carRentalService.getAvailableCarsForTimePeriod(startDate, endDate);

        // verify repos were called
        Set<Long> rentedCarIds = new HashSet<>();
        then(rentalRepository)
                .should()
                .getAllByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate);
        then(carRepository)
                .should()
                .findByIdNotIn(rentedCarIds);
    }


    @Test
    public void testGetRentedCarIds() {

        // setup

        // customers
        Customer customer_1 = getCustomer(1L);
        Customer customer_2 = getCustomer(2L);

        // rented cars
        Set<Rental> rentedCars = new HashSet<>();

        // rental 1
        Long carId_1 = 3L;
        LocalDateTime startDateRented_1 = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00);
        LocalDateTime endDateRented_1 = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);
        Rental rentalForCustomer_1 = getRentalForCustomerForTimePeriod(carId_1, customer_1, startDateRented_1, endDateRented_1);
        rentedCars.add(rentalForCustomer_1);

        // rental 2
        Long carId_2 = 4L;
        LocalDateTime startDateRented_2 = LocalDateTime.of(2019, Month.FEBRUARY, 6, 8, 00, 00);
        LocalDateTime endDateRented_2 = LocalDateTime.of(2019, Month.FEBRUARY, 7, 8, 00, 00);
        Rental rentalForCustomer_2 = getRentalForCustomerForTimePeriod(carId_2, customer_2, startDateRented_2, endDateRented_2);
        rentedCars.add(rentalForCustomer_2);

        // call service
        Set<Long> rentedCarsIds = carRentalService.getRentedCarIds(rentedCars);

        // test results
        assertEquals(2, rentedCarsIds.size());
        assertTrue(rentedCarsIds.contains(rentalForCustomer_1.getCarId()));
        assertTrue(rentedCarsIds.contains(rentalForCustomer_2.getCarId()));
    }


    @Test
    public void testValidateRentalRequestForNewCustomer() {
        // setup

        Customer customer_1 = getCustomer(1L);

        LocalDateTime startDate = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);

        given(customerRepository
                .findByNameAndAddress(customer_1.getName(), customer_1.getAddress()))
                .willReturn(null);

        given(customerRepository
                .save(customer_1))
                .willReturn(customer_1);

        // call service
        carRentalService.validateRentalRequest(customer_1, startDate, endDate);

        // verify repositories were called
        then(customerRepository)
                .should()
                .findByNameAndAddress(customer_1.getName(), customer_1.getAddress());
        then(customerRepository)
                .should()
                .save(customer_1);
    }


    @Test
    public void testValidateRentalRequestForExistingCustomer() {
        // setup

        Customer customer_1 = getCustomer(1L);

        LocalDateTime startDate = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00);
        LocalDateTime endDate = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);

        given(customerRepository
                .findByNameAndAddress(customer_1.getName(), customer_1.getAddress()))
                .willReturn(customer_1);

        // call service
        carRentalService.validateRentalRequest(customer_1, startDate, endDate);

        // verify repositories were called
        then(customerRepository)
                .should()
                .findByNameAndAddress(customer_1.getName(), customer_1.getAddress());
        then(customerRepository)
                .should(never())
                .save(customer_1);
        then(rentalRepository)
                .should()
                .getAllByStartDateBetweenOrEndDateBetween(startDate, endDate, startDate, endDate);
    }


    @Test
    public void testCustomerDidNotRentedAlreadyForTheTimePeriod() {
        // setup

        // customers
        Customer customer_1 = getCustomer(1L);
        Customer customer_2 = getCustomer(2L);

        // rented

        LocalDateTime startDateRented = LocalDateTime.of(2019, Month.MARCH, 10, 8, 00, 00);  // 2019/03/10 8:00:00
        LocalDateTime endDateRented = LocalDateTime.of(2019, Month.MARCH, 11, 8, 00, 00);    // 2019/03/11 8:00:00
        Rental rentalForCustomer_2 = getRentalForCustomerForTimePeriod(4L, customer_2, startDateRented, endDateRented);

        // rented cars
        Set<Rental> rentedCarsForTimePeriod = new HashSet<>();
        rentedCarsForTimePeriod.add(rentalForCustomer_2);


        // call service
        boolean isCustomerRentedAlreadyForTheTimePeriod = carRentalService.isCustomerRentedAlreadyForTheTimePeriod(rentedCarsForTimePeriod, customer_1);

        // test results
        assertFalse(isCustomerRentedAlreadyForTheTimePeriod);
    }


    @Test
    public void testCustomerRentedAlreadyForTheTimePeriod() {
        // setup

        // customers
        Customer customer_1 = getCustomer(1L);

        // rented

        LocalDateTime startDateRented = LocalDateTime.of(2019, Month.FEBRUARY, 6, 8, 00, 00);  // 2019/02/06 8:00:00
        LocalDateTime endDateRented = LocalDateTime.of(2019, Month.FEBRUARY, 7, 8, 00, 00);    // 2019/02/07 8:00:00
        Rental rentalForCustomer_1 = getRentalForCustomerForTimePeriod(4L, customer_1, startDateRented, endDateRented);

        // rented cars
        Set<Rental> rentedCarsForTimePeriod = new HashSet<>();
        rentedCarsForTimePeriod.add(rentalForCustomer_1);


        // call service
        boolean isCustomerRentedAlreadyForTheTimePeriod = carRentalService.isCustomerRentedAlreadyForTheTimePeriod(rentedCarsForTimePeriod, customer_1);

        // test results
        assertTrue(isCustomerRentedAlreadyForTheTimePeriod);
    }


    @Test
    public void testDisallowRentalRequestForExistingCustomer() {
        // setup

        // customer
        Customer customer_1 = getCustomer(1L);

        // rented
        LocalDateTime startDateRented = LocalDateTime.of(2019, Month.FEBRUARY, 6, 8, 00, 00);  // 2019/02/06 8:00:00
        LocalDateTime endDateRented = LocalDateTime.of(2019, Month.FEBRUARY, 7, 8, 00, 00);    // 2019/02/07 8:00:00
        Rental rentalForCustomer_1 = getRentalForCustomerForTimePeriod(4L, customer_1, startDateRented, endDateRented);

        // rented cars
        Set<Rental> rentedCarsForTimePeriod = new HashSet<>();
        rentedCarsForTimePeriod.add(rentalForCustomer_1);

        // call service
        boolean isValid = carRentalService.isValidRentalRequestForTimePeriodForExistingCustomer(rentedCarsForTimePeriod, customer_1);

        // test results
        assertFalse(isValid);
    }


    @Test
    public void testAllowRentalRequestForExistingCustomer() {
        // setup

        // customer
        Customer customer_1 = getCustomer(1L);
        Customer customer_2 = getCustomer(2L);

        // rented
        LocalDateTime startDateRented = LocalDateTime.of(2019, Month.MARCH, 10, 8, 00, 00);  // 2019/03/10 8:00:00
        LocalDateTime endDateRented = LocalDateTime.of(2019, Month.MARCH, 11, 8, 00, 00);    // 2019/03/11 8:00:00
        Rental rentalForCustomer_2 = getRentalForCustomerForTimePeriod(4L, customer_2, startDateRented, endDateRented);

        // rented cars
        Set<Rental> rentedCarsForTimePeriod = new HashSet<>();
        rentedCarsForTimePeriod.add(rentalForCustomer_2);

        // call service
        boolean isValid = carRentalService.isValidRentalRequestForTimePeriodForExistingCustomer(rentedCarsForTimePeriod, customer_1);

        // test results
        assertTrue(isValid);
    }

}
