package org.irina.carrental.repository;

import org.irina.carrental.entity.Rental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public interface RentalRepository extends CrudRepository<Rental, Long> {
    Set<Rental> getAllByStartDateBetweenOrEndDateBetween(LocalDateTime startDateForStart, LocalDateTime endDateForStart, LocalDateTime startDateForEnd, LocalDateTime endDateForEnd);
}

