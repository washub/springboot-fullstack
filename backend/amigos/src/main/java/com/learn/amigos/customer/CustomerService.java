package com.learn.amigos.customer;

import com.learn.amigos.exceptions.DuplicateResourceException;
import com.learn.amigos.exceptions.RequestValidationException;
import com.learn.amigos.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private final CustomerDao customerDao;

    public CustomerService(@Qualifier("jdbc") CustomerDao customerDao){
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers(){
        return customerDao.selectAllCustomers();
    }
    public Customer getCustomer(Long id){
        return customerDao.selectCustomerById(id).orElseThrow(
                () -> new ResourceNotFoundException("customer with id [%d] is not found".formatted(id))
        );
    }
    public void insertCustomer(CustomerRegistrationRequest customer){
        if(customerDao.existCustomerWithEmailId(customer.email())){
            throw new DuplicateResourceException("customer with email [%s] already taken".formatted(customer.email()));
        }
        customerDao.insertCustomer(customer.mapToCustomerEntity());
    }

    public void updateCustomer(Long id, CustomerRegistrationRequest request){
        var entity = customerDao.selectCustomerById(id);
        if(entity.isEmpty()){
            throw new ResourceNotFoundException("customer with id [%d] not found to update".formatted(id));
        }
        var customer = entity.get();
        boolean updateRequired=false;
        if(request.name()!=null && !request.name().equals(customer.getName())){
            customer.setName(request.name());
            updateRequired=true;
        }
        if(request.email()!=null && !request.email().equals(customer.getEmail())){
            customer.setEmail(request.email());
            updateRequired=true;
        }
        if(request.age()!=null && !request.age().equals(customer.getAge())){
            customer.setAge(request.age());
            updateRequired=true;
        }
        if(!updateRequired){
            throw new RequestValidationException("No field change found to update resource");
        }
        customerDao.updateCustomer(id, customer);
    }
    public void deleteCustomer(Long id){
        customerDao.deleteCustomerById(id);
    }
}
