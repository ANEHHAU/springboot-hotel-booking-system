package com.maitrunghau.hotelbookingsystem.controller.auth;

import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.CustomerRepository;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ----------------------------------------------------
    // 🔐 1️⃣ Login Page
    // ----------------------------------------------------
    @GetMapping("/login")
    public String loginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            @RequestParam(value = "unauthorized", required = false) String unauthorized,
            Model model
    ) {
        if (error != null) model.addAttribute("error", "Sai thông tin đăng nhập hoặc tài khoản đã bị vô hiệu!");
        if (logout != null) model.addAttribute("success", "Đăng xuất thành công!");
        if (unauthorized != null) model.addAttribute("error", "Vui lòng đăng nhập để tiếp tục!");
        return "auth/login";
    }

    // ----------------------------------------------------
    // 📝 2️⃣ Register Page → Customer tự tạo tài khoản
    // ----------------------------------------------------
    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerAccount(
            @RequestParam String full_name,
            @RequestParam String email,
            @RequestParam String password,
            @RequestParam(required = false) String phone_number,
            @RequestParam(required = false) String address,
            Model model
    ) {
        log.info("🧾 Register attempt: {}", email);

        // ✅ Kiểm tra trùng email (cả Customer lẫn Employee)
        boolean emailExists =
                customerRepository.findByEmail(email).isPresent() ||
                        employeeRepository.findByEmail(email).isPresent();

        if (emailExists) {
            model.addAttribute("error", "Email này đã được sử dụng!");
            return "auth/register";
        }

        try {
            Customer customer = Customer.builder()
                    .full_name(full_name)
                    .email(email)
                    .password(passwordEncoder.encode(password))
                    .phone_number(phone_number)
                    .address(address)
                    .active(true)
                    .role(Role.Customer) // ✅ auto Customer
                    .build();

            customerRepository.save(customer);
            log.info("✅ Created new customer: {}", email);

            model.addAttribute("success", "Đăng ký thành công! Hãy đăng nhập để tiếp tục.");
            return "auth/login";
        } catch (Exception e) {
            log.error("🔥 Error while registering user", e);
            model.addAttribute("error", "Lỗi khi tạo tài khoản: " + e.getMessage());
            return "auth/register";
        }
    }

    // ----------------------------------------------------
    // 🚫 5️⃣ Access Denied
    // ----------------------------------------------------
    @GetMapping("/403")
    public String accessDenied(Model model) {
        model.addAttribute("error", "Bạn không có quyền truy cập vào trang này!");
        return "error/403";
    }
}
