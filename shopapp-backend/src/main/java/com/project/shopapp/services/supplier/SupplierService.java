package com.project.shopapp.services.supplier;

import com.project.shopapp.dtos.SupplierDTO;
import com.project.shopapp.models.Supplier;

import java.util.List;

public interface SupplierService {
    List<Supplier> getAllSuppliers();
    Supplier createSupplier(SupplierDTO supplierDTO);
    Supplier getSupplierById(Long id);
    Supplier updateSupplier(Long id, SupplierDTO supplierDTO);
    void deleteSupplier(Long id);
}
