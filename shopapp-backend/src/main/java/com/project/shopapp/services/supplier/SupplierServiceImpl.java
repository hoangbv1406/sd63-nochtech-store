package com.project.shopapp.services.supplier;

import com.project.shopapp.dtos.SupplierDTO;
import com.project.shopapp.models.Supplier;
import com.project.shopapp.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SupplierServiceImpl implements SupplierService {
    private final SupplierRepository supplierRepository;

    @Override
    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    @Override
    public Supplier createSupplier(SupplierDTO supplierDTO) {
        Supplier supplier = Supplier.builder()
                .name(supplierDTO.getName())
                .contactEmail(supplierDTO.getContactEmail())
                .contactPhone(supplierDTO.getContactPhone())
                .status("active")
                .build();
        return supplierRepository.save(supplier);
    }

    @Override
    public Supplier getSupplierById(Long id) {
        return supplierRepository.findById(id).orElseThrow(() -> new RuntimeException("Supplier not found"));
    }

    @Override
    public Supplier updateSupplier(Long id, SupplierDTO supplierDTO) {
        Supplier existing = getSupplierById(id);
        existing.setName(supplierDTO.getName());
        existing.setContactEmail(supplierDTO.getContactEmail());
        existing.setContactPhone(supplierDTO.getContactPhone());
        return supplierRepository.save(existing);
    }

    @Override
    public void deleteSupplier(Long id) {
        supplierRepository.deleteById(id);
    }

}
