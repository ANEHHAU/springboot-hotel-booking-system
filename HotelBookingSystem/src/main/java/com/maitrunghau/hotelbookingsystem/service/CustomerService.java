package com.maitrunghau.hotelbookingsystem.service;

import com.maitrunghau.hotelbookingsystem.dto.CustomerDTO;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

public interface CustomerService {

    List<Customer> getAllCustomers();

    Optional<Customer> getCustomerById(Long id);

    Customer addCustomer(CustomerDTO dto, MultipartFile avatarFile);   // ⬅️ thêm

    Customer updateCustomer(Long id, CustomerDTO dto, MultipartFile avatarFile); // ⬅️ thêm

    void deleteCustomer(Long id);
}
