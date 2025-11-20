package com.project.shopapp.services.address;

import com.project.shopapp.dtos.UserAddressDTO;
import com.project.shopapp.responses.user.UserAddressResponse;

import java.util.List;

public interface UserAddressService {
    List<UserAddressResponse> getAllAddressByUserId(Long userId);
    UserAddressResponse getAddressById(Long addressId);
    UserAddressResponse createUserAddress(UserAddressDTO addressDTO) throws Exception;
    UserAddressResponse updateUserAddress(Long addressId, UserAddressDTO addressDTO) throws Exception;
    void deleteUserAddress(Long addressId);
}
