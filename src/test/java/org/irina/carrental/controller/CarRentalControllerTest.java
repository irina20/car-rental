package org.irina.carrental.controller;

import org.irina.carrental.entity.Car;
import org.irina.carrental.entity.Customer;
import org.irina.carrental.service.CarRentalService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@WebMvcTest(CarRentalController.class)
public class CarRentalControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private CarRentalController carRentalController;

    @Mock
    private CarRentalService carRentalService;


    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(carRentalController).build();
    }


    @Test
    public void testGetAvailableCarsForTimePeriod() throws Exception {
        Car car = new Car("Economy", "Corolla", new BigDecimal(24.99));

        LocalDateTime startDate = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00); //2019/02/02 8:00:00
        LocalDateTime endDate = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);   //2019/02/03 8:00:00

        Set carSet = new HashSet<>();
        carSet.add(car);

        given(carRentalService
                .getAvailableCarsForTimePeriod(startDate, endDate))
                .willReturn(carSet);

        mvc.perform(get("/car/rentals/available")
                .param("startDate", startDate.toString())
                .param("endDate", endDate.toString()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..carModel").value("Corolla"));

        // verify that carRentalService.rentCar() was never called in this case
        then(carRentalService)
                .should()
                .getAvailableCarsForTimePeriod(startDate, endDate);
    }

    @Test
    public void testCannotReserveCarForExistingCustomerForTimePeriod() throws Exception {
        Customer customer = getCustomer();
        customer.setId(1L);

        Car car = new Car("Economy", "Corolla", new BigDecimal(24.99));
        car.setId(2L);

        LocalDateTime startDate = LocalDateTime.of(2019, Month.FEBRUARY, 2, 8, 00, 00); //2019/02/02 8:00:00
        LocalDateTime endDate = LocalDateTime.of(2019, Month.FEBRUARY, 3, 8, 00, 00);   //2019/02/03 8:00:00

        given(carRentalService
                .validateRentalRequest(customer, startDate, endDate))
                .willReturn(customer);

        mvc.perform(post("/car/rentals/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .content(getRequestContent(customer.getName(), customer.getAddress(), car)))
                .andExpect(status().isUnavailableForLegalReasons());

        // verify that carRentalService.rentCar() was never called in this case
        then(carRentalService)
                .should(never())
                .rentCar(car.getId(), customer.getId(), startDate, endDate);
    }

    private Customer getCustomer() {
        String customerName = "Customer Name";
        String customerAddress = "Customer Address";
        Customer customer = new Customer(customerName, customerAddress);
        return customer;
    }

    private String getRequestContent(String customerName, String customerAddress, Car car) {
        StringBuilder buf = new StringBuilder();
        buf.append("{\"carId\":" + car.getId() + ",");
        buf.append("\"customerName\":\"" + customerName + "\",");
        buf.append("\"customerAddress\":\"" + customerAddress + "\",");
        buf.append("\"startDate\":\"2019-02-02T08:00\",");
        buf.append("\"endDate\":\"2019-02-03T08:00\"}");
        return buf.toString();
    }

}
