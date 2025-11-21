package com.project.shopapp.services.user;

import com.project.shopapp.dtos.UserDTO;
import com.project.shopapp.dtos.UserLoginDTO;
import com.project.shopapp.dtos.UserUpdateDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.exceptions.InvalidPasswordException;
import com.project.shopapp.models.User;

import java.util.List;

public interface UserService {
    List<User> getAllUsers();
    User createUser(UserDTO userDTO) throws Exception;
    User updateUser(Long userId, UserUpdateDTO userUpdateDTO) throws Exception;
    User getUserDetailsFromToken(String token) throws Exception;
    String loginSocial(UserLoginDTO userLoginDTO) throws Exception;
    void changeProfileImage(Long userId, String imageName) throws Exception;
    void blockOrEnable(Long userId, Boolean active) throws DataNotFoundException;
    void resetPassword(Long userId, String newPassword) throws InvalidPasswordException, DataNotFoundException;
}
