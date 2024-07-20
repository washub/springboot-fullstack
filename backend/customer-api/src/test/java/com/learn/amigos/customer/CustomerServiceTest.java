package com.learn.amigos.customer;

import com.learn.amigos.exceptions.DuplicateResourceException;
import com.learn.amigos.exceptions.RequestValidationException;
import com.learn.amigos.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @InjectMocks
    private CustomerService tester;
    @Mock
    private CustomerDao dao;


    @Test
    void getAllCustomers() {
        tester.getAllCustomers();

        verify(dao).selectAllCustomers();
    }

    @Test
    void getCustomer() {
        Customer customer = new Customer("name",1,"email");

        when(dao.selectCustomerById(anyLong())).thenReturn(Optional.of(customer));
        var response = tester.getCustomer(1L);

        verify(dao).selectCustomerById(1L);
        assertThat(response).satisfies(result->{
            assertThat(result.getAge()).isEqualTo(1);
            assertThat(result.getName()).isEqualTo("name");
            assertThat(result.getEmail()).isEqualTo("email");
        });

    }
    @Test
    void getCustomerWithException(){
        assertThrows(ResourceNotFoundException.class, () -> tester.getCustomer(1L));

        verify(dao).selectCustomerById(1L);
    }

    @Test
    void insertCustomer() {
        CustomerRegistrationRequest record = new CustomerRegistrationRequest("name", "email", 1);

        when(dao.existCustomerWithEmailId(anyString())).thenReturn(false);

        tester.insertCustomer(record);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        verify(dao).insertCustomer(captor.capture());

        Customer customer = captor.getValue();
        assertThat(customer).satisfies(res ->{
            assertThat(res.getId()).isNull();
            assertThat(res.getEmail()).isEqualTo(record.email());
            assertThat(res.getName()).isEqualTo(record.name());
            assertThat(res.getAge()).isEqualTo(record.age());
        });
    }
    @Test
    void insertCustomerDuplicate(){
        CustomerRegistrationRequest record = new CustomerRegistrationRequest("name", "email", 1);

        when(dao.existCustomerWithEmailId(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, ()-> tester.insertCustomer(record));

        assertThatThrownBy(()-> tester.insertCustomer(record))
                .isInstanceOf(DuplicateResourceException.class)
                        .hasMessage("customer with email [%s] already taken".formatted(record.email()));
        verify(dao, never()).insertCustomer(any());

    }

    @Test
    void updateCustomer() {
        Customer customer = new Customer("name",1,"email");

        CustomerRegistrationRequest record = new CustomerRegistrationRequest("random","xamrin",10);
        when(dao.selectCustomerById(anyLong())).thenReturn(Optional.of(customer));

        tester.updateCustomer(1L, record);

        ArgumentCaptor<Customer> captor = ArgumentCaptor.forClass(Customer.class);

        verify(dao).updateCustomer(anyLong(),captor.capture());

        var result = captor.getValue();

        assertThat(result).satisfies(res->{
            assertThat(res.getAge()).isEqualTo(record.age());
            assertThat(res.getName()).isEqualTo(record.name());
            assertThat(res.getEmail()).isEqualTo(record.email());
        });
    }
    @Test
    void updateCustomerWithNoChange(){
        Customer customer = new Customer("name",1,"email");

        CustomerRegistrationRequest record = new CustomerRegistrationRequest("name","email",1);
        when(dao.selectCustomerById(anyLong())).thenReturn(Optional.of(customer));

        assertThatThrownBy(()->tester.updateCustomer(1L, record))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No field change found to update resource");

        verify(dao, never()).updateCustomer(anyLong(), any());
    }
    @Test
    void updateCustomerWhenNotFound(){
        CustomerRegistrationRequest record = new CustomerRegistrationRequest("name","email",1);
        when(dao.selectCustomerById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(()-> tester.updateCustomer(1L, record))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("customer with id [%d] not found to update".formatted(1L));

        verify(dao, never()).updateCustomer(anyLong(), any());

    }

    @Test
    void deleteCustomer() {
        tester.deleteCustomer(1L);
        verify(dao).deleteCustomerById(1L);
    }
}