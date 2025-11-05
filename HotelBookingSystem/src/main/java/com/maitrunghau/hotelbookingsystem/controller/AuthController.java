package com.maitrunghau.hotelbookingsystem.controller;

import com.maitrunghau.hotelbookingsystem.model.User;
import com.maitrunghau.hotelbookingsystem.repository.UserRepository;
import com.maitrunghau.hotelbookingsystem.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Random;

@Controller
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // ✅ Đăng nhập
    @GetMapping("/login")
    public String showLoginPage() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String username,
                               @RequestParam String password,
                               Model model) {
        Optional<User> user = userRepository.findByUsername(username);
        if (user.isEmpty() || !passwordEncoder.matches(password, user.get().getPassword())) {
            model.addAttribute("error", "❌ Tên đăng nhập hoặc mật khẩu không đúng!");
            return "auth/login";
        }
        model.addAttribute("user", user.get());
        return "redirect:/home"; // tuỳ route bạn có
    }

    // ✅ Đăng ký
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/register";
    }

    @PostMapping("/register")
    public String processRegister(@ModelAttribute User user, Model model) {
        if (userRepository.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "❌ Email đã được sử dụng!");
            return "auth/register";
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("Customer");
        userRepository.save(user);

        model.addAttribute("success", "✅ Đăng ký thành công! Vui lòng đăng nhập.");
        return "redirect:/auth/login";
    }

    // ✅ Quên mật khẩu
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            model.addAttribute("error", "Không tìm thấy tài khoản với email này!");
            return "auth/forgot-password";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        emailService.sendOtpMail(email, otp);

        model.addAttribute("contact", email);
        model.addAttribute("otp", otp);
        return "auth/verify-otp";
    }

    // ✅ Xác minh email hoặc số điện thoại (check-identity)
    @GetMapping("/check-identity")
    public String showCheckIdentityPage() {
        return "auth/check-identity";
    }

    @PostMapping("/check-identity")
    public String processCheckIdentity(@RequestParam String identity, Model model) {
        boolean isEmail = identity.contains("@");

        if (isEmail && !userRepository.existsByEmail(identity)) {
            model.addAttribute("error", "Không tìm thấy tài khoản với email này!");
            return "auth/check-identity";
        }
        if (!isEmail && !userRepository.existsByPhone(identity)) {
            model.addAttribute("error", "Không tìm thấy tài khoản với số điện thoại này!");
            return "auth/check-identity";
        }

        String otp = String.format("%06d", new Random().nextInt(999999));
        if (isEmail) {
            emailService.sendOtpMail(identity, otp);
        } else {
            System.out.println("Gửi OTP SMS đến: " + identity + " → " + otp);
        }

        model.addAttribute("contact", identity);
        model.addAttribute("otp", otp);
        return "auth/verify-otp";
    }

    // ✅ Nhập và xác minh OTP
    @GetMapping("/verify-otp")
    public String showVerifyOtpPage(Model model) {
        model.addAttribute("otp", "");
        return "auth/verify-otp";
    }

    @PostMapping("/verify-otp")
    public String processVerifyOtp(@RequestParam String otp,
                                   @RequestParam String userOtp,
                                   Model model) {
        if (!otp.equals(userOtp)) {
            model.addAttribute("error", "❌ Mã OTP không đúng!");
            return "auth/verify-otp";
        }
        model.addAttribute("success", "✅ Xác minh thành công!");
        return "redirect:/auth/login";
    }
}
