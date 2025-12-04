package com.project.shopapp.domains.iam.security;

import com.project.shopapp.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof User) {
            User user = (User) principal;
            return user.isActive() ? user : null;
        }
        return null;
    }

    public Long getLoggedInUserId() {
        User user = getLoggedInUser();
        return user != null ? user.getId() : null;
    }

}
