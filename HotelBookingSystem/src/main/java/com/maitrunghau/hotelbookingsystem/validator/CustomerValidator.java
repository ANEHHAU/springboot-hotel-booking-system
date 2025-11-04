package com.maitrunghau.hotelbookingsystem.validator;

import com.maitrunghau.hotelbookingsystem.dto.CustomerDTO;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomerValidator implements Validator {

    private final CustomerRepository customerRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return CustomerDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        CustomerDTO dto = (CustomerDTO) target;
        Long id = dto.getId();

        // 🔹 Kiểm tra email trùng (chỉ khi thêm mới hoặc đổi email)
        if (dto.getEmail() != null) {
            Optional<Customer> existingEmail = customerRepository.findByEmail(dto.getEmail());
            if (existingEmail.isPresent()) {
                if (id == null || !existingEmail.get().getId().equals(id)) {
                    errors.rejectValue("email", "email.exists", "Email đã được sử dụng");
                }
            }
        }

        // 🔹 Kiểm tra số điện thoại trùng
        if (dto.getPhone_number() != null && !dto.getPhone_number().isBlank()) {
            Optional<Customer> existingPhone = customerRepository.findByPhoneNumber(dto.getPhone_number());
            if (existingPhone.isPresent()) {
                if (id == null || !existingPhone.get().getId().equals(id)) {
                    errors.rejectValue("phone_number", "phone.exists", "Số điện thoại đã được sử dụng");
                }
            }
        }

        // 🔹 Kiểm tra ngày sinh hợp lệ
        if (dto.getDate_of_birth() == null) {
            errors.rejectValue("date_of_birth", "dob.empty", "Ngày sinh không được để trống");
        } else {
            try {
                LocalDate today = LocalDate.now();
                LocalDate dob = dto.getDate_of_birth();

                if (dob.isAfter(today)) {
                    errors.rejectValue("date_of_birth", "dob.future", "Ngày sinh phải trước ngày hiện tại");
                } else if (dob.isBefore(today.minusYears(120))) {
                    errors.rejectValue("date_of_birth", "dob.invalid", "Ngày sinh không hợp lệ (quá 120 tuổi)");
                }
            } catch (Exception ex) {
                errors.rejectValue("date_of_birth", "dob.parse", "Ngày sinh không hợp lệ");
            }
        }


        // 🔹 Trạng thái không được null
        if (dto.getActive() == null) {
            errors.rejectValue("active", "active.null", "Trạng thái không được để trống");
        }

        log.debug("✅ Validator hoàn tất cho khách hàng: {}", dto.getEmail());
    }
}
