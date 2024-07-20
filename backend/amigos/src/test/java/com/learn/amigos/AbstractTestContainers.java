package com.learn.amigos;

import net.datafaker.Faker;
import org.flywaydb.core.Flyway;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.sql.DataSource;

@Testcontainers
public abstract class AbstractTestContainers {
	@Container
	private static final PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
			.withDatabaseName("postgres_unit_test")
			.withUsername("admin")
			.withPassword("admin");

	@DynamicPropertySource
	public static void registerDataSourceProperties(DynamicPropertyRegistry registry){
		registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
		registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
		registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
	}
	@BeforeAll
    public static void initFlyWayDBMigration(){
		Flyway flyway =  Flyway.configure().dataSource(
				postgreSQLContainer.getJdbcUrl(),
				postgreSQLContainer.getUsername(),
				postgreSQLContainer.getPassword()).load();
		flyway.migrate();
	}

	private DataSource getDataSource(){
		return DataSourceBuilder.create()
				.driverClassName(postgreSQLContainer.getDriverClassName())
				.url(postgreSQLContainer.getJdbcUrl())
				.username(postgreSQLContainer.getUsername())
				.password(postgreSQLContainer.getPassword())
				.build();
	}

	protected JdbcTemplate getJdbcTemplate(){
		return new JdbcTemplate(getDataSource());

	}
	protected static Faker faker = new Faker();

}
