package com.learn.amigos.customer;

import com.learn.amigos.exceptions.DuplicateResourceException;
import com.learn.amigos.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("jpa")
@RequiredArgsConstructor
public class CustomerJPADataAccessService implements CustomerDao{
    private final CustomerRepository customerRepository;

    @Override
    public List<Customer> selectAllCustomers() {
        return customerRepository.findAll();
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    @Override
    public void insertCustomer(Customer entity) {
        if(existCustomerWithEmailId(entity.getEmail())){
            throw new DuplicateResourceException("customer with email [%s] already taken".formatted(entity.getEmail()));
        }
        customerRepository.save(entity);
    }

    @Override
    public boolean existCustomerWithEmailId(String email) {
        return customerRepository.findByEmail(email).isPresent();
    }

    @Override
    public void deleteCustomerById(Long id) {
        customerRepository.deleteById(id);
    }

    @Override
    public void updateCustomer(Long id, Customer entity) {
        customerRepository.save(entity);

    }
}
