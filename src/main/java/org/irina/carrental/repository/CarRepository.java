package org.irina.carrental.repository;

import org.irina.carrental.entity.Car;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Set;

public interface CarRepository extends CrudRepository<Car, Long> {
    List<Car> findAll();
    Set<Car> findByIdNotIn(Set<Long> rentedCarIds);

}

