package com.project.shopapp.controllers;

import com.project.shopapp.dtos.WarrantyRequestDTO;
import com.project.shopapp.models.WarrantyRequest;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.warranty.WarrantyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/warranties")
@RequiredArgsConstructor
public class WarrantyController {
    private final WarrantyService warrantyService;

    @PostMapping("")
    public ResponseEntity<ResponseObject> createWarrantyRequest(@Valid @RequestBody WarrantyRequestDTO dto) throws Exception {
        WarrantyRequest request = warrantyService.createWarrantyRequest(dto);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(request)
                .message("Warranty request created")
                .build());
    }

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllRequests() {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(warrantyService.getAllRequests())
                .message("Get warranty requests successfully")
                .build());
    }

}
