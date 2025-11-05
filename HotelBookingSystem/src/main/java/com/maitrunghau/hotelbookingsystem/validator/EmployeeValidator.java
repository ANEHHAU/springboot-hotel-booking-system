package com.maitrunghau.hotelbookingsystem.validator;

import com.maitrunghau.hotelbookingsystem.dto.EmployeeDTO;
import com.maitrunghau.hotelbookingsystem.model.Employee;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
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
public class EmployeeValidator implements Validator {

    private final EmployeeRepository employeeRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return EmployeeDTO.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        EmployeeDTO dto = (EmployeeDTO) target;
        Long id = dto.getId();

        // 🔹 Kiểm tra email trùng
        if (dto.getEmail() != null) {
            Optional<Employee> existingEmail = employeeRepository.findByEmail(dto.getEmail());
            if (existingEmail.isPresent()) {
                if (id == null || !existingEmail.get().getId().equals(id)) {
                    errors.rejectValue("email", "email.exists", "Email đã được sử dụng bởi nhân viên khác");
                }
            }
        }

        // 🔹 Kiểm tra ngày sinh hợp lệ
        if (dto.getDate_of_birth() != null) {
            LocalDate dob = dto.getDate_of_birth();
            LocalDate today = LocalDate.now();

            if (dob.isAfter(today)) {
                errors.rejectValue("date_of_birth", "dob.future", "Ngày sinh phải trước ngày hiện tại");
            } else if (dob.isBefore(today.minusYears(120))) {
                errors.rejectValue("date_of_birth", "dob.invalid", "Ngày sinh không hợp lệ (quá 120 tuổi)");
            }
        }

        // 🔹 Kiểm tra ngày tuyển dụng hợp lệ
        if (dto.getHire_date() != null && dto.getHire_date().isAfter(LocalDate.now())) {
            errors.rejectValue("hire_date", "hire.future", "Ngày tuyển dụng không được vượt quá ngày hiện tại");
        }

        // 🔹 Kiểm tra mức lương hợp lệ
        if (dto.getSalary() != null && dto.getSalary() < 0) {
            errors.rejectValue("salary", "salary.invalid", "Lương không được âm");
        }

        // 🔹 Trạng thái không được null
        if (dto.getActive() == null) {
            errors.rejectValue("active", "active.null", "Trạng thái không được để trống");
        }

        log.debug("✅ Validator hoàn tất cho nhân viên: {}", dto.getEmail());
    }
}
