package com.learn.amigos;


import com.learn.amigos.customer.Customer;
import com.learn.amigos.customer.CustomerRepository;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

import java.util.List;
import java.util.UUID;

@SpringBootApplication
@PropertySources({
		@PropertySource("dbQueries.properties")
})
public class CustomerApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomerApplication.class, args);
	}
	@Bean
	public CommandLineRunner runner(CustomerRepository customerRepository){
		return args->{
			var faker = new Faker();
			String fullName = faker.name().fullName();
			String fullNamee = faker.name().fullName();
			var obj = new Customer(fullName, faker.number().numberBetween(11,99), fullName.split(" ")[0].concat(UUID.randomUUID().toString()+"@gmail.com"));
			var objj = new Customer(fullNamee, faker.number().numberBetween(11,99), fullNamee.split(" ")[0].concat(UUID.randomUUID().toString()+"@outlook.com"));
			//in future we may have to disable this feature
			customerRepository.saveAll(List.of(obj, objj));
		};
	}

}
