package com.maitrunghau.hotelbookingsystem.controller;

import com.maitrunghau.hotelbookingsystem.dto.CustomerDTO;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.response.ApiResponse;
import com.maitrunghau.hotelbookingsystem.service.CustomerService;
import com.maitrunghau.hotelbookingsystem.validator.CustomerValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/customers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerService customerService;
    private final CustomerValidator customerValidator;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success("Danh sách khách hàng", customers));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(ApiResponse.success("Thông tin khách hàng", customer)))
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng có ID: " + id)));
    }


    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> createCustomer(
            @Valid @ModelAttribute CustomerDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        log.info("🟢 Tạo khách hàng mới: {}", dto.getEmail());
        customerValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));

            log.warn("⚠️ Lỗi khi tạo khách hàng: {}", fieldErrors);

            ApiResponse<Object> res = ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Dữ liệu không hợp lệ")
                    .data(fieldErrors)
                    .build();
            return ResponseEntity.badRequest().body(res);
        }

        try {
            Customer newCustomer = customerService.addCustomer(dto, avatarFile);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.created("Thêm khách hàng thành công", newCustomer));

        } catch (Exception e) {
            log.error("❌ Lỗi khi thêm khách hàng: {}", e.getMessage(), e);
            ApiResponse<Object> res = ApiResponse.builder()
                    .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                    .message("Không thể thêm khách hàng: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.internalServerError().body(res);
        }
    }


    @PutMapping(value = "/{id}", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<?>> updateCustomer(
            @PathVariable Long id,
            @Valid @ModelAttribute CustomerDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "avatarFile", required = false) MultipartFile avatarFile) {

        log.info("🟡 Cập nhật khách hàng ID={}", id);
        customerValidator.validate(dto, bindingResult);

        if (bindingResult.hasErrors()) {
            Map<String, String> fieldErrors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e -> fieldErrors.put(e.getField(), e.getDefaultMessage()));

            log.warn("⚠️ Lỗi khi cập nhật khách hàng ID={}: {}", id, fieldErrors);

            ApiResponse<Object> res = ApiResponse.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message("Dữ liệu không hợp lệ")
                    .data(fieldErrors)
                    .build();
            return ResponseEntity.badRequest().body(res);
        }

        try {
            Customer updated = customerService.updateCustomer(id, dto, avatarFile);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật khách hàng thành công", updated));

        } catch (RuntimeException e) {
            log.error("❌ Lỗi khi cập nhật khách hàng ID={}: {}", id, e.getMessage(), e);
            ApiResponse<Object> res = ApiResponse.builder()
                    .status(HttpStatus.NOT_FOUND.value())
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }
    }



    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        try {
            customerService.deleteCustomer(id);
            return ResponseEntity.ok(ApiResponse.success("Xóa khách hàng thành công", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng có ID: " + id));
        }
    }
}
