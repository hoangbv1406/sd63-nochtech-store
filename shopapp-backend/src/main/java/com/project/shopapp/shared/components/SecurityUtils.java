package com.project.shopapp.shared.components;

import com.project.shopapp.models.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof User) {
            User selectedUser = (User) authentication.getPrincipal();
            if (!selectedUser.isActive()) {
                return null;
            }
            return selectedUser;
        }
        return null;
    }

}
