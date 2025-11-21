package com.project.shopapp.controllers;

import com.project.shopapp.dtos.SupplierDTO;
import com.project.shopapp.models.Supplier;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.supplier.SupplierService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("${api.prefix}/suppliers")
@RequiredArgsConstructor
public class SupplierController {
    private final SupplierService supplierService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllSuppliers() {
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .data(supplierService.getAllSuppliers())
                .message("Get suppliers successfully")
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createSupplier(@Valid @RequestBody SupplierDTO supplierDTO) {
        Supplier supplier = supplierService.createSupplier(supplierDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .data(supplier)
                .message("Created supplier successfully")
                .build());
    }

}
