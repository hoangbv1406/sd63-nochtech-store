package com.project.shopapp.controllers;

import com.project.shopapp.dtos.BrandDTO;
import com.project.shopapp.models.Brand;
import com.project.shopapp.responses.ResponseObject;
import com.project.shopapp.services.brand.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/brands")
@RequiredArgsConstructor
public class BrandController {
    private final BrandService brandService;

    @GetMapping("")
    public ResponseEntity<ResponseObject> getAllBrands() {
        List<Brand> brands = brandService.getAllBrands();
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get all brands successfully")
                .data(brands)
                .build());
    }

    @PostMapping("")
    public ResponseEntity<ResponseObject> createBrand(@Valid @RequestBody BrandDTO brandDTO) {
        Brand brand = brandService.createBrand(brandDTO);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.CREATED)
                .message("Create brand successfully")
                .data(brand)
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseObject> getBrandById(@PathVariable Long id) {
        Brand brand = brandService.getBrandById(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Get brand successfully")
                .data(brand)
                .build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseObject> deleteBrand(@PathVariable Long id) {
        brandService.deleteBrand(id);
        return ResponseEntity.ok(ResponseObject.builder()
                .status(HttpStatus.OK)
                .message("Delete brand successfully")
                .build());
    }
}