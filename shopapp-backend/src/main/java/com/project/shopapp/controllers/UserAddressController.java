package com.project.shopapp.controllers;

import com.project.shopapp.components.SecurityUtils;
import com.project.shopapp.dtos.UserAddressDTO;
import com.project.shopapp.models.User;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.responses.user.UserAddressResponse;
import com.project.shopapp.services.address.UserAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/user-addresses")
@RequiredArgsConstructor
public class UserAddressController {
    private final UserAddressService userAddressService;
    private final SecurityUtils securityUtils;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getMyAddresses() throws Exception {
        User user = securityUtils.getLoggedInUser();
        List<UserAddressResponse> addresses = userAddressService.getAllAddressByUserId(user.getId());
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(addresses)
                .message("Get addresses successfully")
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createAddress(@Valid @RequestBody UserAddressDTO addressDTO) throws Exception {
        User user = securityUtils.getLoggedInUser();
        addressDTO.setUserId(user.getId());
        UserAddressResponse newAddress = userAddressService.createUserAddress(addressDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(newAddress)
                .message("Address created successfully")
                .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResponseObject> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressDTO addressDTO
    ) throws Exception {
        UserAddressResponse updatedAddress = userAddressService.updateUserAddress(id, addressDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(updatedAddress)
                .message("Address updated successfully")
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteAddress(@PathVariable Long id) {
        userAddressService.deleteUserAddress(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Address deleted successfully")
                .build());
    }

}
