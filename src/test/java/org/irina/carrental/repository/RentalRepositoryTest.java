package org.irina.carrental.repository;

import org.irina.carrental.entity.Rental;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RentalRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RentalRepository rentalRepository;

    @Test
    public void testGetAllByStartDateBetweenOrEndDateBetween() {

        // insert

        // rental 1
        Long carId_1 = 1L;
        Long customerId_1 = 1L;
        LocalDateTime startDate_1 = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00);  // 2019/02/02 8:00:00
        LocalDateTime endDate_1 = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);    // 2019/02/03 8:00:00
        Rental rental_1 = new Rental(carId_1, customerId_1, startDate_1, endDate_1);
        entityManager.persist(rental_1);

        // rental 2
        Long carId_2 = 2L;
        Long customerId_2 = 2L;
        LocalDateTime startDate_2 = LocalDateTime.of(2019, Month.FEBRUARY, 6, 8, 00, 00);  // 2019/02/06 8:00:00
        LocalDateTime endDate_2 = LocalDateTime.of(2019, Month.FEBRUARY, 7, 8, 00, 00);    // 2019/02/07 8:00:00
        Rental rental_2 = new Rental(carId_2, customerId_2, startDate_2, endDate_2);
        entityManager.persist(rental_2);

        // rental 3
        Long carId_3 = 3L;
        Long customerId_3 = 3L;
        LocalDateTime startDate_3 = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);  // 2019/02/03 8:00:00
        LocalDateTime endDate_3 = LocalDateTime.of(2019, Month.FEBRUARY, 4, 8, 00, 00);    // 2019/02/04 8:00:00
        Rental rental_3 = new Rental(carId_3, customerId_3, startDate_3, endDate_3);
        entityManager.persist(rental_3);


        // call repo
        LocalDateTime startDate_4 = LocalDateTime.of(2019, Month.FEBRUARY, 1, 8, 00, 00);  // 2019/02/01 8:00:00
        LocalDateTime endDate_4 = LocalDateTime.of(2019, Month.FEBRUARY, 2, 16, 00, 00);    // 2019/02/02 16:00:00
        Set<Rental> rentalsFound = rentalRepository.getAllByStartDateBetweenOrEndDateBetween(startDate_4, endDate_4, startDate_4, endDate_4);

        // test results
        assertEquals(1, rentalsFound.size());
        assertTrue(rentalsFound.contains(rental_1));
    }
}
