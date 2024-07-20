package com.learn.amigos.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @GetMapping("/customers")
    public ResponseEntity<List<Customer>> getAllCustomers(){
        return ResponseEntity.ok(customerService.getAllCustomers());
    }
    @GetMapping("/customer/{id}")
    public ResponseEntity<Customer> getCustomerById(@PathVariable Long id){
        return ResponseEntity.ok(customerService.getCustomer(id));
    }
    @PostMapping("/customer")
    public ResponseEntity<?> registerCustomer(@RequestBody CustomerRegistrationRequest request){
        customerService.insertCustomer(request);
        return ResponseEntity.ok("Customer Successfully Created");
    }
    @PutMapping("/customer/{id}")
    public ResponseEntity<?> updateCustomer(@RequestBody CustomerRegistrationRequest request, @PathVariable Long id){
        customerService.updateCustomer(id, request);
        return ResponseEntity.ok("Resource updated Successfully");
    }

    @DeleteMapping("/customer/{id}")
    public void deleteCustomerById(@PathVariable Long id){
        customerService.deleteCustomer(id);
    }
}
