package com.erp.erp_backend.service;

import com.erp.erp_backend.dto.SalesOrderRequest;
import com.erp.erp_backend.entity.Customer;
import com.erp.erp_backend.entity.OrderStatus;
import com.erp.erp_backend.entity.Product;
import com.erp.erp_backend.entity.SalesOrder;
import com.erp.erp_backend.exception.ResourceConflictException;
import com.erp.erp_backend.exception.ResourceNotFoundException;
import com.erp.erp_backend.repository.CustomerRepository;
import com.erp.erp_backend.repository.ProductRepository;
import com.erp.erp_backend.repository.SalesOrderRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class SalesOrderService {

    private final SalesOrderRepository salesOrderRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    public SalesOrderService(
            SalesOrderRepository salesOrderRepository,
            CustomerRepository customerRepository,
            ProductRepository productRepository) {

        this.salesOrderRepository = salesOrderRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
    }

    public SalesOrder createSalesOrder(SalesOrderRequest request) {

        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Customer not found"
                        ));

        Product product = productRepository.findById(request.getProductId())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product not found"
                        ));

        if (product.getCurrentStock() < request.getQuantity()) {
            throw new ResourceConflictException(
                    "Insufficient product stock"
            );
        }

        BigDecimal totalAmount = product.getUnitPrice()
                .multiply(BigDecimal.valueOf(request.getQuantity()));

        SalesOrder salesOrder = SalesOrder.builder()
                .customer(customer)
                .product(product)
                .quantity(request.getQuantity())
                .orderDate(LocalDate.now())
                .status(OrderStatus.PENDING)
                .totalAmount(totalAmount)
                .build();

        return salesOrderRepository.save(salesOrder);
    }

    public List<SalesOrder> getAllSalesOrders() {
        return salesOrderRepository.findAll();
    }

    public SalesOrder getSalesOrderById(Long id) {

        return salesOrderRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Sales order not found"
                        ));
    }

    public List<SalesOrder> getSalesOrdersByCustomer(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new ResourceNotFoundException(
                    "Customer not found"
            );
        }

        return salesOrderRepository.findByCustomerId(customerId);
    }

    public SalesOrder updateSalesOrderStatus(
            Long id,
            OrderStatus status) {

        SalesOrder salesOrder = getSalesOrderById(id);

        salesOrder.setStatus(status);

        return salesOrderRepository.save(salesOrder);
    }

    public void deleteSalesOrder(Long id) {

        SalesOrder salesOrder = getSalesOrderById(id);

        salesOrderRepository.delete(salesOrder);
    }
}