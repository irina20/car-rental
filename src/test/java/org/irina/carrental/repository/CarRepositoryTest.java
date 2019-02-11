package org.irina.carrental.repository;

import org.irina.carrental.entity.Car;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CarRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CarRepository carRepository;

    @Test
    public void testFindByIdNot() {
        Set<Long> carIdsToBeExcluded = new HashSet<>();

        // insert
        Car car_1 = new Car("Economy", "Corolla", new BigDecimal(24.99));
        entityManager.persist(car_1);

        Car car_2 = new Car("Compact", "Nissan", new BigDecimal(34.99));
        entityManager.persist(car_2);
        carIdsToBeExcluded.add(car_2.getId());

        Car car_3 = new Car("Luxury", "Lexus", new BigDecimal(49.99));
        entityManager.persist(car_3);
        carIdsToBeExcluded.add(car_3.getId());

        // call repo
        Set<Car> carsFound = carRepository.findByIdNotIn(carIdsToBeExcluded);

        // test results
        assertEquals(1, carsFound.size());
        assertTrue(carsFound.contains(car_1));
        assertFalse(carsFound.contains(car_2));
    }
}
