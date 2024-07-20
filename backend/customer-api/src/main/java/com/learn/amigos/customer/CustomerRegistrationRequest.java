package com.learn.amigos.customer;

public record CustomerRegistrationRequest(String name, String email, Integer age) {
    public Customer mapToCustomerEntity(){
        return new Customer(this.name, this.age, this.email);
    }
}
