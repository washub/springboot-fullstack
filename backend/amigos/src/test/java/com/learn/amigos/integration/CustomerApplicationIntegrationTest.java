package com.learn.amigos.integration;

import com.learn.amigos.customer.Customer;
import com.learn.amigos.customer.CustomerRegistrationRequest;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
//@TestPropertySource("classpath:application-test.properties")
public class CustomerApplicationIntegrationTest {
    @Autowired
    WebTestClient webTestClient;
    private static final Faker faker = new Faker();

    @Test
    void canRegisterCustomer(){
        var name = faker.name().name();
        var email = name+"@integrationTest.com";
        var age = faker.number().numberBetween(20,80);
        // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );


        // make a post call to register
        webTestClient.post()
                .uri("/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        // make sure the customer exist

        Customer expected = new Customer(name, age, email);
        assertThat(allCustomers).usingRecursiveFieldByFieldElementComparatorIgnoringFields("id")
                .contains(expected);

        // get customer by id
        var id = allCustomers.stream()
                .filter(cus -> cus.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        expected.setId(id);

        webTestClient.get()
                .uri("/v1/customer/{id}",id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectBody(new ParameterizedTypeReference<Customer>() {
                })
                .isEqualTo(expected);
    }

    @Test
    void canDeleteCustomer(){
        var name = faker.name().name();
        var email = name+"@integrationTest.com";
        var age = faker.number().numberBetween(20,80);
        // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );


        // make a post call to register
        webTestClient.post()
                .uri("/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        // get customer by id
        var id = allCustomers.stream()
                .filter(cus -> cus.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        webTestClient.delete()
                .uri("/v1/customer/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk();

        webTestClient.get()
                .uri("/v1/customer/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isNotFound();
    }
    @Test
    void canUpdateCustomer(){
        var name = faker.name().name();
        var email = name+"@integrationTest.com";
        var age = faker.number().numberBetween(20,80);
        // create registration request
        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                name, email, age
        );


        // make a post call to register
        webTestClient.post()
                .uri("/v1/customer")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(request), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus().isOk();
        // get all customers
        List<Customer> allCustomers = webTestClient.get()
                .uri("/v1/customers")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(new ParameterizedTypeReference<Customer>() {})
                .returnResult().getResponseBody();

        // get customer by id
        var id = allCustomers.stream()
                .filter(cus -> cus.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();
        var toUpdate = new CustomerRegistrationRequest("updatedGuy", null, null);
        webTestClient.put()
                .uri("/v1/customer/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(toUpdate), CustomerRegistrationRequest.class)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(String.class)
                .isEqualTo("Resource updated Successfully");


        var updated = webTestClient.get()
                .uri("/v1/customer/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Customer.class)
                .returnResult().getResponseBody();
        var expected = new Customer("updatedGuy", age, email);
        expected.setId(id);

        assertThat(updated).isEqualTo(expected);
    }
}
