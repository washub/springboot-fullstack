package com.learn.amigos.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Service("jdbc")
@RequiredArgsConstructor
public class CustomerJdbcDataAccessService implements CustomerDao{
    private final JdbcTemplate jdbcTemplate;

    @Value("${INSERT_CUSTOMER}")
    private String INSERT_CUSTOMER;
    @Value("${SELECT_CUSTOMERS}")
    private String SELECT_CUSTOMERS;
    @Value("${SELECT_CUSTOMER_BY_ID}")
    private String SELECT_CUSTOMER_BY_ID;
    @Value("${SELECT_CUSTOMER_BY_EMAIL}")
    private String SELECT_CUSTOMER_BY_EMAIL;
    @Value("${DELETE_CUSTOMER_BY_ID}")
    private String DELETE_CUSTOMER_BY_ID;
    @Value("${UPDATE_CUSTOMER}")
    private String UPDATE_CUSTOMER;
    static class CustomerRowMapper implements RowMapper<Customer>{

        @Override
        public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
            Customer entity = new Customer();
            entity.setId(rs.getLong("id"));
            entity.setName(rs.getString("name"));
            entity.setEmail(rs.getString("email"));
            entity.setAge(rs.getInt("age"));
            return entity;
        }
    }

    @Override
    public List<Customer> selectAllCustomers() {
        return jdbcTemplate.query(SELECT_CUSTOMERS, new CustomerRowMapper());

    }

    @Override
    public Optional<Customer> selectCustomerById(Long id) {
        return jdbcTemplate.query(SELECT_CUSTOMER_BY_ID, new CustomerRowMapper(), id).stream().findFirst();
    }

    @Override
    public void insertCustomer(Customer entity) {
        jdbcTemplate.update(INSERT_CUSTOMER,
                entity.getName(),
                entity.getEmail(),
                entity.getAge());
    }

    @Override
    public boolean existCustomerWithEmailId(String email) {
        return jdbcTemplate.query(SELECT_CUSTOMER_BY_EMAIL, new CustomerRowMapper(), email).stream().findFirst().isPresent();
    }

    @Override
    public void deleteCustomerById(Long id) {
        jdbcTemplate.update(DELETE_CUSTOMER_BY_ID, id);
    }

    @Override
    public void updateCustomer(Long id, Customer customer) {
        jdbcTemplate.update(UPDATE_CUSTOMER,
                customer.getName(),
                customer.getEmail(),
                customer.getAge(),
                id);
    }
}
