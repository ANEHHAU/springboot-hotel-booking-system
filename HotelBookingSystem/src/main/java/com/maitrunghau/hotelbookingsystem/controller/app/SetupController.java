package com.maitrunghau.hotelbookingsystem.controller.app;

import com.maitrunghau.hotelbookingsystem.dto.EmployeeDTO;
import com.maitrunghau.hotelbookingsystem.model.Employee;
import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Slf4j
@Controller
@RequiredArgsConstructor
public class SetupController {
    //@todo thiếu up ảnh của admin
    private final EmployeeRepository employeeRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


    @GetMapping("/setup/create-admin")
    public String showCreateAdminPage(Model model) {
        long adminCount = employeeRepository.countByRole(Role.Admin);
        System.out.println("✅ Setup page loaded, adminCount = " + adminCount);
        if (adminCount > 0) {
            return "redirect:/auth/login";
        }
        model.addAttribute("employee", new EmployeeDTO());
        return "setup/create-admin-account";
    }

// @todo       tránh đứa nào bố láo vào nhưng mà chưa sạch cần xử lý lại



    @PostMapping("/setup/create-admin")
//    public String createAdmin(@Valid @ModelAttribute("employee") EmployeeDTO dto,
    public String createAdmin( @ModelAttribute("employee") EmployeeDTO dto,
                               BindingResult result,
                               Model model) {

        System.out.println("ngu");


        log.info("📩 Received request to create admin:");
        log.info(" - Full name: {}", dto.getFull_name());
        log.info(" - Email: {}", dto.getEmail());
        log.info(" - Password: {}", dto.getPassword());
        log.info(" - Phone: {}", dto.getPhone_number());
        log.info(" - Address: {}", dto.getAddress());
        log.info(" - DOB: {}", dto.getDate_of_birth());

        // 🔍 Log toàn bộ lỗi validation chi tiết
        if (result.hasErrors()) {
            log.warn("⚠️ Validation errors detected! ({} errors)", result.getErrorCount());
            result.getFieldErrors().forEach(err ->
                    log.warn(" ❌ Field '{}' - {} (rejected value: {})",
                            err.getField(), err.getDefaultMessage(), err.getRejectedValue()));
            return "setup/create-admin-account";
        }

        long existingAdmins = employeeRepository.countByRole(Role.Admin);
        log.info("👤 Existing admin count: {}", existingAdmins);

        if (existingAdmins > 0) {
            log.warn("❌ Attempt to create new admin when one already exists.");
            model.addAttribute("error", "Hệ thống đã có tài khoản quản trị!");
            return "redirect:/auth/login";
        }

        try {
            Employee admin = Employee.builder()
                    .full_name(dto.getFull_name())
                    .email(dto.getEmail())
                    .password(passwordEncoder.encode(dto.getPassword()))
                    .role(Role.Admin)
                    .phone_number(dto.getPhone_number())
                    .address(dto.getAddress())
                    .date_of_birth(dto.getDate_of_birth())
                    .active(true)
                    .build();

            log.info("💾 Saving new admin: {}", admin.getEmail());
            employeeRepository.save(admin);
            log.info("✅ Admin saved successfully with ID: {}", admin.getId());

            model.addAttribute("success", "✅ Tạo tài khoản Admin thành công!");
            return "redirect:/auth/login";

        } catch (Exception e) {
            log.error("🔥 Error while saving admin account", e);
            model.addAttribute("error", "Lỗi khi lưu tài khoản quản trị: " + e.getMessage());
            return "setup/create-admin-account";
        }
    }

}
