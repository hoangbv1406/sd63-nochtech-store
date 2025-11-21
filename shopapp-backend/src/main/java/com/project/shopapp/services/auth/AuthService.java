package com.project.shopapp.services.auth;

import com.project.shopapp.dtos.UserLoginDTO;

public interface AuthService {
    String login(UserLoginDTO userLoginDTO) throws Exception;
}
