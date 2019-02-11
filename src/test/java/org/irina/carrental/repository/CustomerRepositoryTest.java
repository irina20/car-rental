package org.irina.carrental.repository;

import org.irina.carrental.entity.Customer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@DataJpaTest
public class CustomerRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    public void testFindByNameAndAddress() {

        // insert
        String customerName = "Customer Name";
        String customerAddress = "Customer Address";
        Customer customer = new Customer(customerName, customerAddress);
        entityManager.persist(customer);

        String customerName_2 = "Customer Name2";
        String customerAddress_2 = "Customer Address2";
        Customer customer_2 = new Customer(customerName_2, customerAddress_2);
        entityManager.persist(customer_2);

        // call repo
        Customer customerFound = customerRepository.findByNameAndAddress(customer.getName(), customer.getAddress());

        // test results
        assertEquals(customerName, customerFound.getName());
        assertEquals(customerAddress, customerFound.getAddress());
    }
}
