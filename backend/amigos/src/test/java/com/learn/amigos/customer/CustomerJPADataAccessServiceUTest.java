package com.learn.amigos.customer;

import com.learn.amigos.exceptions.DuplicateResourceException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CustomerJPADataAccessServiceUTest {
    private CustomerJPADataAccessService tester;
    @Mock
    private CustomerRepository repository;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
        tester = new CustomerJPADataAccessService(repository);
    }
    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void selectAllCustomers() {
        tester.selectAllCustomers();

        verify(repository).findAll();
    }

    @Test
    void selectCustomerById() {
        tester.selectCustomerById(1L);
        verify(repository).findById(1L);
    }

    @Test
    void insertCustomer() {
        Customer customer = new Customer("name",1,"email");
        tester.insertCustomer(customer);
        verify(repository).findByEmail(customer.getEmail());
        verify(repository).save(customer);
    }
    @Test
    void insertCustomerWhenEmailAlreadyTaken() {
        Customer customer = new Customer("name",1,"email");
        Mockito.when(repository.findByEmail("email")).thenReturn(Optional.of(customer));
        assertThrows(DuplicateResourceException.class, ()-> tester.insertCustomer(customer));
        verify(repository, times(0)).save(customer);
    }

    @Test
    void existCustomerWithEmailId() {
        tester.existCustomerWithEmailId("email");
        verify(repository).findByEmail("email");
    }

    @Test
    void deleteCustomerById() {
        tester.deleteCustomerById(1L);
        verify(repository).deleteById(1L);
    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("name",1,"email");
        tester.updateCustomer(1L, customer);
        verify(repository).save(customer);
    }
}