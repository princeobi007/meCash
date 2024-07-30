package com.iroegbulam.princewill.mecash.service;

import com.iroegbulam.princewill.mecash.domain.User;
import com.iroegbulam.princewill.mecash.exception.AccessDeniedException;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class MeCashService {

    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            return (User) authentication.getPrincipal();
        }
        throw new AccessDeniedException("Kindly confirm user is logged in");
    }
}
