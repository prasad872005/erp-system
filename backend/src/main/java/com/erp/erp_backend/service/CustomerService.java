package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.CustomerRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(CustomerRequest request) {

        if (customerRepository.existsByEmail(request.getEmail())) {
            throw new ResourceConflictException(
                    "Customer email already exists"
            );
        }

        Customer customer = Customer.builder()
                .customerName(request.getCustomerName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .build();

        return customerRepository.save(customer);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer getCustomerById(Long id) {

        return customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found"
                        )
                );
    }

    public Customer updateCustomer(
            Long id,
            CustomerRequest request
    ) {

        Customer customer = getCustomerById(id);

        if (!customer.getEmail().equals(request.getEmail())
                && customerRepository.existsByEmail(request.getEmail())) {

            throw new ResourceConflictException(
                    "Customer email already exists"
            );
        }

        customer.setCustomerName(request.getCustomerName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());

        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {

        Customer customer = getCustomerById(id);

        customerRepository.delete(customer);
    }
}