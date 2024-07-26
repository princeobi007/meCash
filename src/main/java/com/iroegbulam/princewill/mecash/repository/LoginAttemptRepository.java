package com.iroegbulam.princewill.mecash.repository;


import com.iroegbulam.princewill.mecash.domain.LoginAttempt;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.util.List;

@Repository
public class LoginAttemptRepository {
    private static final int RECENT_COUNT = 10; // can be in the config
    private static final String INSERT = "INSERT INTO mecash.login_attempt (phone, success, created_at) VALUES(:phone, :success, :createdAt)";
    private static final String FIND_RECENT = "SELECT * FROM mecash.login_attempt WHERE phone = :phone ORDER BY created_at DESC LIMIT :recentCount";

    private final JdbcClient jdbcClient;

    public LoginAttemptRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    public void add(LoginAttempt loginAttempt) {
        long affected = jdbcClient.sql(INSERT)
                .param("phone", loginAttempt.phone())
                .param("success", loginAttempt.success())
                .param("createdAt", loginAttempt.createdAt())
                .update();

        Assert.isTrue(affected == 1, "Could not add login attempt.");
    }

    public List<LoginAttempt> findRecent(String phone) {
        return jdbcClient.sql(FIND_RECENT)
                .param("phone", phone)
                .param("recentCount", RECENT_COUNT)
                .query(LoginAttempt.class)
                .list();
    }
}
