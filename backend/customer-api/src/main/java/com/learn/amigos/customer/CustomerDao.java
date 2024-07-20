package com.learn.amigos.customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomers();
    Optional<Customer> selectCustomerById(Long id);
    void insertCustomer(Customer entity);
    boolean existCustomerWithEmailId(String email);
    void deleteCustomerById(Long id);
    void updateCustomer(Long id, Customer customer);
}
