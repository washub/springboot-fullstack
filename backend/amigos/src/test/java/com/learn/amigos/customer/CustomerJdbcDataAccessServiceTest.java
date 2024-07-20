package com.learn.amigos.customer;

import com.learn.amigos.AbstractTestContainers;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class CustomerJdbcDataAccessServiceTest extends AbstractTestContainers {
    private CustomerJdbcDataAccessService tester;

    @BeforeEach
    void setUp() {
        tester = new CustomerJdbcDataAccessService(getJdbcTemplate());
    }
    private static Customer getCustomerEnity() {
        return  new Customer(
                faker.name().fullName(),
                faker.number().numberBetween(11,40),
                faker.name().firstName().concat(UUID.randomUUID().toString()+"@gmail.com"));
    }
    @Test
    void selectAllCustomers() {
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMERS", "Select * from customer");
        ReflectionTestUtils.setField(tester, "INSERT_CUSTOMER", "INSERT into customer(name, email, age) values(?,?,?)");

        Customer customer = getCustomerEnity();

        tester.insertCustomer(customer);

        var result = tester.selectAllCustomers();
        assertThat(result).isNotEmpty();
    }



    @Test
    void selectCustomerById() {
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMERS", "Select * from customer");
        ReflectionTestUtils.setField(tester, "INSERT_CUSTOMER", "INSERT into customer(name, email, age) values(?,?,?)");
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMER_BY_ID", "Select * from customer where id=?");

        Customer customer = getCustomerEnity();
        String email = customer.getEmail();

        tester.insertCustomer(customer);
        var allCustomers = tester.selectAllCustomers();
        Long id = allCustomers.stream()
                .filter(c ->c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();

        var result = tester.selectCustomerById(id);

        assertThat(result).isPresent()
                .hasValueSatisfying(c ->{
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    void selectCustomerByIdWhenNotFound(){
        Long id = -1L;
        var result = tester.selectCustomerById(id);
        assertThat(result).isEmpty();
    }


    @Test
    void existCustomerWithEmailId() {
        ReflectionTestUtils.setField(tester, "INSERT_CUSTOMER", "INSERT into customer(name, email, age) values(?,?,?)");
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMER_BY_EMAIL", "Select * from customer where email=?");

        Customer customer = getCustomerEnity();
        String email = customer.getEmail();

        tester.insertCustomer(customer);
        var result = tester.existCustomerWithEmailId(email);
        assertThat(result).isTrue();
    }
    @Test
    void existCustomerWithEmailIdNotFound() {
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMER_BY_EMAIL", "Select * from customer where email=?");
        var result = tester.existCustomerWithEmailId(faker.name()+UUID.randomUUID().toString()+"@gmail.com");
        assertThat(result).isFalse();
    }

    @Test
    void deleteCustomerById() {
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMERS", "Select * from customer");
        ReflectionTestUtils.setField(tester, "INSERT_CUSTOMER", "INSERT into customer(name, email, age) values(?,?,?)");
        ReflectionTestUtils.setField(tester, "SELECT_CUSTOMER_BY_ID", "Select * from customer where id=?");
        ReflectionTestUtils.setField(tester, "DELETE_CUSTOMER_BY_ID", "delete from customer where id=?");

        Customer customer = getCustomerEnity();
        String email = customer.getEmail();

        tester.insertCustomer(customer);
        var allCustomers = tester.selectAllCustomers();
        Long id = allCustomers.stream()
                .filter(c ->c.getEmail().equals(email))
                .findFirst()
                .map(Customer::getId)
                .orElseThrow();

        tester.deleteCustomerById(id);

        var result = tester.selectCustomerById(id);
        assertThat(result).isEmpty();
    }
}