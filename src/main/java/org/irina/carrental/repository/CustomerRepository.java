package org.irina.carrental.repository;

import org.irina.carrental.entity.Customer;
import org.springframework.data.repository.CrudRepository;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

    Customer findByNameAndAddress(String name, String address);

}

