package com.project.shopapp.controllers;

import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.address.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/address")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService addressService;

    @GetMapping("/provinces")
    public ResponseEntity<ResponseObject> getAllProvinces() {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get provinces successfully")
                .data(addressService.getAllProvinces())
                .build());
    }

    @GetMapping("/districts/{provinceCode}")
    public ResponseEntity<ResponseObject> getDistricts(@PathVariable String provinceCode) {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get districts successfully")
                .data(addressService.getDistrictsByProvince(provinceCode))
                .build());
    }

    @GetMapping("/wards/{districtCode}")
    public ResponseEntity<ResponseObject> getWards(@PathVariable String districtCode) {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get wards successfully")
                .data(addressService.getWardsByDistrict(districtCode))
                .build());
    }

}
