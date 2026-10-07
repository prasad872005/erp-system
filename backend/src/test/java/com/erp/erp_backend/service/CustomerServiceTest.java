package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.CustomerRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private CustomerRequest request;

    @BeforeEach
    void setUp() {

        request = new CustomerRequest();

        request.setCustomerName("ABC Enterprises");
        request.setEmail("abc@example.com");
        request.setPhone("9876543210");
        request.setAddress("Mumbai");
    }

    @Test
    void createCustomer_shouldCreateSuccessfully() {

        when(customerRepository.existsByEmail("abc@example.com"))
                .thenReturn(false);

        Customer savedCustomer = Customer.builder()
                .id(1L)
                .customerName("ABC Enterprises")
                .email("abc@example.com")
                .phone("9876543210")
                .address("Mumbai")
                .build();

        when(customerRepository.save(any(Customer.class)))
                .thenReturn(savedCustomer);

        Customer result = customerService.createCustomer(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ABC Enterprises", result.getCustomerName());
        assertEquals("abc@example.com", result.getEmail());

        verify(customerRepository)
                .existsByEmail("abc@example.com");

        verify(customerRepository)
                .save(any(Customer.class));
    }

    @Test
    void createCustomer_shouldThrowConflictWhenEmailExists() {

        when(customerRepository.existsByEmail("abc@example.com"))
                .thenReturn(true);

        assertThrows(
                ResourceConflictException.class,
                () -> customerService.createCustomer(request)
        );

        verify(customerRepository)
                .existsByEmail("abc@example.com");

        verify(customerRepository, never())
                .save(any(Customer.class));
    }

    @Test
    void getCustomerById_shouldReturnCustomer() {

        Customer customer = Customer.builder()
                .id(1L)
                .customerName("ABC Enterprises")
                .email("abc@example.com")
                .phone("9876543210")
                .address("Mumbai")
                .build();

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(customer));

        Customer result = customerService.getCustomerById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("ABC Enterprises", result.getCustomerName());

        verify(customerRepository).findById(1L);
    }

    @Test
    void getCustomerById_shouldThrowNotFound() {

        when(customerRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> customerService.getCustomerById(99L)
        );

        verify(customerRepository).findById(99L);
    }

    @Test
    void getAllCustomers_shouldReturnCustomers() {

        Customer customer1 = Customer.builder()
                .id(1L)
                .customerName("ABC Enterprises")
                .email("abc@example.com")
                .phone("9876543210")
                .address("Mumbai")
                .build();

        Customer customer2 = Customer.builder()
                .id(2L)
                .customerName("XYZ Traders")
                .email("xyz@example.com")
                .phone("9876543211")
                .address("Pune")
                .build();

        when(customerRepository.findAll())
                .thenReturn(List.of(customer1, customer2));

        List<Customer> result = customerService.getAllCustomers();

        assertEquals(2, result.size());
        assertEquals("ABC Enterprises", result.get(0).getCustomerName());
        assertEquals("XYZ Traders", result.get(1).getCustomerName());

        verify(customerRepository).findAll();
    }
}