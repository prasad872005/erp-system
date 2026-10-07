package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.SupplierRequest;
import com.erp.erp_backend.entity.Supplier;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public Supplier createSupplier(SupplierRequest request) {

        if (supplierRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException(
                    "Supplier email already exists"
            );
        }

        Supplier supplier = Supplier.builder()
                .supplierName(request.getSupplierName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        return supplierRepository.save(supplier);
    }

    public List<Supplier> getAllSuppliers() {
        return supplierRepository.findAll();
    }

    public Supplier getSupplierById(Long id) {

        return supplierRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Supplier not found"
                        )
                );
    }

    public Supplier updateSupplier(
            Long id,
            SupplierRequest request
    ) {

        Supplier supplier = getSupplierById(id);

        if (!supplier.getEmail().equals(request.getEmail())
                && supplierRepository.existsByEmail(request.getEmail())) {

            throw new ResourceConflictException(
                    "Supplier email already exists"
            );
        }

        supplier.setSupplierName(request.getSupplierName());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setAddress(request.getAddress());

        return supplierRepository.save(supplier);
    }

    public void deleteSupplier(Long id) {

        Supplier supplier = getSupplierById(id);

        supplierRepository.delete(supplier);
    }
}