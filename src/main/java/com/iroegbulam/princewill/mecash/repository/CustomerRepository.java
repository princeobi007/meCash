package com.iroegbulam.princewill.mecash.repository;

import com.iroegbulam.princewill.mecash.domain.Customer;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.Optional;

@Repository
public class CustomerRepository {

    private static final String INSERT = "INSERT INTO mecash.customer (phone, email, password, dob, firstname, middlename, lastname, nin, bvn) VALUES(:phone, :email, :password, :dob, :firstname, :middlename, :lastname, :nin, :bnv)";
    private static final String FIND_BY_PHONE = "SELECT * FROM mecash.customer WHERE phone = :phone";

    private final JdbcClient jdbcClient;

    public CustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void add(Customer user) {
        long affected = jdbcClient.sql(INSERT)
                .param("phone", user.phoneNumber())
                .param("email", user.email())
                .param("password", user.password())
                .param("dob", user.dob())
                .param("firstname", user.firstname())
                .param("middlename", user.middleName())
                .param("lastname", user.lastname())
                .param("nin", user.nin())
                .param("bvn", user.bvn())
                .update();

        Assert.isTrue(affected == 1, "Could not add user.");
    }

    public Optional<Customer> findByPhone(String phone) {
        return jdbcClient.sql(FIND_BY_PHONE)
                .param("phone", phone)
                .query(Customer.class)
                .optional();
    }
}
