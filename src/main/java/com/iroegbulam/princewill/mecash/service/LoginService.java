package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.LoginAttempt;
import com.iroegbulam.princewill.mecash.repository.LoginAttemptRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class LoginService {

    private final LoginAttemptRepository repository;

    public LoginService(LoginAttemptRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public void addLoginAttempt(String phone, boolean success) {
        LoginAttempt loginAttempt = new LoginAttempt(phone, success, LocalDateTime.now());
        repository.add(loginAttempt);
    }

    public List<LoginAttempt> findRecentLoginAttempts(String phone) {
        return repository.findRecent(phone);
    }
}
