package com.learn.amigos.customer;

import com.learn.amigos.AbstractTestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CustomerJPADataAccessServiceTest extends AbstractTestContainers {
    @Autowired
    ApplicationContext applicationContext;
    @Autowired
    CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        System.out.println(applicationContext.getBeanDefinitionCount());
    }

    @Test
    void existCustomerWithEmailId() {
        var name = faker.name().firstName();
        var email = name + "@gmail.com";
        Customer customer = new Customer(name, 11, email);

        customerRepository.save(customer);
        var result = customerRepository.findAll().stream()
                .filter(cust -> cust.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst().orElseThrow();

        var test = customerRepository.existsById(result);
        assertThat(test).isTrue();
    }

    @Test
    public void testExistByEmailFailWhenNoEmailPresent(){
        var name = faker.name().firstName();
        var email = name+"@gmail.com";
        Customer entity = new Customer(name, 11, email);

        customerRepository.save(entity);
        var id = customerRepository.findAll().stream()
                .filter(c-> c.getEmail().equals("random@gmail.com"))
                .map(Customer::getId)
                .findFirst()
                .orElse(null);
        assertThat(id).isNull();

        var res = customerRepository.findByEmail("random@gmail.com");

        assertThat(res).isEmpty();
    }

}