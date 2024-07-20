package com.learn.amigos.customer;

import com.learn.amigos.exceptions.DuplicateResourceException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service("list")
public class CustomerListDataAccessService implements CustomerDao{
    private static final List<Customer> customers=new ArrayList<>();
    static {
        var alex = new Customer("Alex", 22, "alex@gmail.com");
        var bart = new Customer("Bart", 21, "bart@gmail.com");
        customers.add(alex);
        customers.add(bart);
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return customers;
    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return customers.stream().filter(customer -> Objects.equals(customer.getId(), id)).findFirst();
    }

    @Override
    public void insertCustomer(Customer entity) {
        if(existCustomerWithEmailId(entity.getEmail())){
            throw new DuplicateResourceException("customer with email [%s] already taken".formatted(entity.getEmail()));
        }
        customers.add(entity);
    }

    @Override
    public boolean existCustomerWithEmailId(String email) {
        return customers.stream().anyMatch(customer -> customer.getEmail().equals(email));
    }

    @Override
    public void deleteCustomerById(Long id) {
        customers.stream().filter(customer -> Objects.equals(customer.getId(), id))
                .findFirst().ifPresent(customers::remove);
    }

    @Override
    public void updateCustomer(Long id, Customer customer) {
        var data = customers.stream().filter(entity -> entity.getId().equals(id)).findFirst();
        if(data.isPresent()){
            var obj = data.get();
            obj.setEmail(customer.getEmail());
            obj.setAge(customer.getAge());
            obj.setName(customer.getName());
        }
    }
}
