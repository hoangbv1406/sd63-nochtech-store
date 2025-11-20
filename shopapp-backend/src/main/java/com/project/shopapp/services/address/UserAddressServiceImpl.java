package com.project.shopapp.services.address;

import com.project.shopapp.dtos.UserAddressDTO;
import com.project.shopapp.exceptions.DataNotFoundException;
import com.project.shopapp.models.User;
import com.project.shopapp.models.UserAddress;
import com.project.shopapp.repositories.UserAddressRepository;
import com.project.shopapp.repositories.UserRepository;
import com.project.shopapp.responses.user.UserAddressResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserAddressServiceImpl implements UserAddressService {
    private final UserAddressRepository userAddressRepository;
    private final UserRepository userRepository;

    @Override
    public List<UserAddressResponse> getAllAddressByUserId(Long userId) {
        List<UserAddress> addresses = userAddressRepository.findByUserId(userId);
        return addresses.stream().map(UserAddressResponse::fromUserAddress).collect(Collectors.toList());
    }

    @Override
    public UserAddressResponse getAddressById(Long addressId) {
        UserAddress address = userAddressRepository.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        return UserAddressResponse.fromUserAddress(address);
    }

    @Override
    @Transactional
    public UserAddressResponse createUserAddress(UserAddressDTO addressDTO) throws Exception {
        User user = userRepository.findById(addressDTO.getUserId()).orElseThrow(() -> new DataNotFoundException("User not found"));

        if (addressDTO.isDefault()) {
            UserAddress currentDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(user.getId());
            if (currentDefault != null) {
                currentDefault.setDefault(false);
                userAddressRepository.save(currentDefault);
            }
        }

        UserAddress newAddress = UserAddress.builder()
                .user(user)
                .recipientName(addressDTO.getRecipientName())
                .phoneNumber(addressDTO.getPhoneNumber())
                .addressDetail(addressDTO.getAddressDetail())
                .provinceCode(addressDTO.getProvinceCode())
                .districtCode(addressDTO.getDistrictCode())
                .wardCode(addressDTO.getWardCode())
                .isDefault(addressDTO.isDefault())
                .build();

        return UserAddressResponse.fromUserAddress(userAddressRepository.save(newAddress));
    }

    @Override
    @Transactional
    public UserAddressResponse updateUserAddress(Long addressId, UserAddressDTO addressDTO) throws Exception {
        UserAddress existingAddress = userAddressRepository.findById(addressId).orElseThrow(() -> new DataNotFoundException("Address not found"));

        existingAddress.setRecipientName(addressDTO.getRecipientName());
        existingAddress.setPhoneNumber(addressDTO.getPhoneNumber());
        existingAddress.setAddressDetail(addressDTO.getAddressDetail());
        existingAddress.setProvinceCode(addressDTO.getProvinceCode());
        existingAddress.setDistrictCode(addressDTO.getDistrictCode());
        existingAddress.setWardCode(addressDTO.getWardCode());

        if (addressDTO.isDefault() && !existingAddress.isDefault()) {
            UserAddress currentDefault = userAddressRepository.findByUserIdAndIsDefaultTrue(existingAddress.getUser().getId());
            if (currentDefault != null) {
                currentDefault.setDefault(false);
                userAddressRepository.save(currentDefault);
            }
            existingAddress.setDefault(true);
        }

        return UserAddressResponse.fromUserAddress(userAddressRepository.save(existingAddress));
    }

    @Override
    @Transactional
    public void deleteUserAddress(Long addressId) {
        userAddressRepository.deleteById(addressId);
    }

}
