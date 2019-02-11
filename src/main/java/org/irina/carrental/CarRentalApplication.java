package org.irina.carrental;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/* Assumptions:
 * 1. a car becomes available exactly at the time it is scheduled to return
 * 2. payment made in cash at the time of the reservation
 * 3. no additional options are offered, like car insurance or upgrades
 * 4. no special deals to offer for a rental
 * 5. a customer can rent only one car for a time period, rental time overlaps are not allowed
 * 6. only customer who made reservations can drive the car, and only one customer can be on the rental agreement
 * 7. there is always a car available for rent
 * 8. there is no thread-safety concern for competing car reservations as it is a small rental company with only one person able to access the system at a time
 * 9. only unit tests are required, and not integration tests
 */

@SpringBootApplication
public class CarRentalApplication {

	private static final Logger log = LoggerFactory.getLogger(CarRentalApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(CarRentalApplication.class, args);
	}

}

