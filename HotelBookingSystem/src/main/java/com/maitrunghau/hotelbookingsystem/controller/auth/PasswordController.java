package com.maitrunghau.hotelbookingsystem.controller.auth;

import com.maitrunghau.hotelbookingsystem.model.AuthToken;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.model.Employee;
import com.maitrunghau.hotelbookingsystem.model.TokenType;
import com.maitrunghau.hotelbookingsystem.repository.AuthTokenRepository;
import com.maitrunghau.hotelbookingsystem.repository.CustomerRepository;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import com.maitrunghau.hotelbookingsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth")
public class PasswordController {

    private final EmployeeRepository employeeRepository;
    private final CustomerRepository customerRepository;
    private final AuthTokenRepository tokenRepository;
    private final EmailService emailService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ==============================================================
    // 1️⃣ Trang yêu cầu OTP (quên mật khẩu)
    // ==============================================================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String handleForgotPassword(@RequestParam String identity, Model model) {
        log.info("📩 Yêu cầu khôi phục mật khẩu từ: {}", identity);

        Optional<Employee> empOpt = employeeRepository.findByEmail(identity)
                .or(() -> employeeRepository.findByPhoneNumber(identity));
        Optional<Customer> cusOpt = customerRepository.findByEmail(identity)
                .or(() -> customerRepository.findByPhoneNumber(identity));

        if (empOpt.isEmpty() && cusOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản phù hợp!");
            return "auth/forgot-password";
        }

        // ✅ Tạo mã OTP và lưu DB
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));
        AuthToken token = AuthToken.builder()
                .token(otp)
                .type(TokenType.PASSWORD_RESET)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .used(false)
                .build();
        tokenRepository.save(token);

        // ✅ Gửi email OTP
        String targetEmail = empOpt.map(Employee::getEmail).orElseGet(() -> cusOpt.get().getEmail());
        emailService.sendEmail(targetEmail,
                "🔐 Mã đặt lại mật khẩu",
                "mail-reset-password.html",
                Map.of("otp", otp, "expireMinutes", 5));

        model.addAttribute("success", "Đã gửi mã xác minh đến " + targetEmail);
        model.addAttribute("identity", identity);
        return "auth/reset-password";
    }

    // ==============================================================
    // 2️⃣ Trang nhập mật khẩu mới
    // ==============================================================
    @GetMapping("/reset-password")
    public String resetPasswordPage(@RequestParam(required = false) String identity, Model model) {
        model.addAttribute("identity", identity);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String handleResetPassword(@RequestParam String identity,
                                      @RequestParam String otp,
                                      @RequestParam String newPassword,
                                      Model model) {
        log.info("🔑 Đặt lại mật khẩu cho {}", identity);

        Optional<AuthToken> tokenOpt = tokenRepository.findByToken(otp);
        if (tokenOpt.isEmpty()) {
            model.addAttribute("error", "Mã xác minh không hợp lệ!");
            return "auth/reset-password";
        }

        AuthToken token = tokenOpt.get();
        if (token.isUsed() || token.getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "Mã OTP đã hết hạn hoặc đã được sử dụng!");
            return "auth/reset-password";
        }

        // ✅ Xác nhận người dùng tồn tại
        Employee emp = employeeRepository.findByEmail(identity)
                .or(() -> employeeRepository.findByPhoneNumber(identity))
                .orElse(null);

        Customer cus = customerRepository.findByEmail(identity)
                .or(() -> customerRepository.findByPhoneNumber(identity))
                .orElse(null);

        if (emp == null && cus == null) {
            model.addAttribute("error", "Không tìm thấy tài khoản hợp lệ!");
            return "auth/reset-password";
        }

        try {
            // ✅ Cập nhật mật khẩu
            if (emp != null) {
                emp.setPassword(passwordEncoder.encode(newPassword));
                employeeRepository.save(emp);
            } else {
                cus.setPassword(passwordEncoder.encode(newPassword));
                customerRepository.save(cus);
            }

            // ✅ Đánh dấu token đã dùng
            token.setUsed(true);
            tokenRepository.save(token);

            model.addAttribute("success", "✅ Mật khẩu đã được đặt lại thành công!");
            return "auth/login";

        } catch (Exception e) {
            log.error("❌ Lỗi khi đặt lại mật khẩu", e);
            model.addAttribute("error", "Đã xảy ra lỗi khi cập nhật mật khẩu!");
            return "auth/reset-password";
        }
    }
}
