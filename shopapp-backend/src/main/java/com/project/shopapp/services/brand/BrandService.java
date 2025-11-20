package com.project.shopapp.services.brand;

import com.project.shopapp.dtos.BrandDTO;
import com.project.shopapp.models.Brand;

import java.util.List;

public interface BrandService {
    List<Brand> getAllBrands();
    Brand createBrand(BrandDTO brandDTO);
    Brand getBrandById(Long id);
    void deleteBrand(Long id);
}
