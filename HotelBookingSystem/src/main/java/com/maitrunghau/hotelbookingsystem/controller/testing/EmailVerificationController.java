package com.maitrunghau.hotelbookingsystem.controller.testing;

import com.maitrunghau.hotelbookingsystem.model.AuthToken;
import com.maitrunghau.hotelbookingsystem.model.TokenType;
import com.maitrunghau.hotelbookingsystem.repository.AuthTokenRepository;
import com.maitrunghau.hotelbookingsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/test/email")
public class EmailVerificationController {

    private final EmailService emailService;
    private final AuthTokenRepository tokenRepository;

    /** 📨 Gửi link xác minh */
    @PostMapping("/send-link")
    public String sendLink(@RequestParam String email, Model model) {
        String token = UUID.randomUUID().toString();
        String link = "http://localhost:8080/test/email/verify?token=" + token;

        tokenRepository.save(AuthToken.builder()
                .token(token)
                .type(TokenType.EMAIL_VERIFY)
                .expiresAt(LocalDateTime.now().plusMinutes(15))
                .used(false)
                .build());

        emailService.sendEmail(email, "🔗 Xác minh email (Test)", "mail-verify-link.html",
                Map.of("verifyLink", link, "expireMinutes", 15));

        model.addAttribute("success", "✅ Đã gửi link xác minh tới " + email);
        loadTokens(model);
        return "email/email-test";
    }

    /** 🔢 Gửi OTP */
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, Model model) {
        String otp = String.valueOf((int) (Math.random() * 900000 + 100000));

        tokenRepository.save(AuthToken.builder()
                .token(otp)
                .type(TokenType.EMAIL_OTP)
                .expiresAt(LocalDateTime.now().plusMinutes(10))
                .used(false)
                .build());

        Map<String, Object> vars = Map.of(
                "otp", otp,   // đổi từ otpCode → otp
                "expireMinutes", 10
        );
        emailService.sendEmail(email, "🔢 Mã OTP xác thực (Test)", "mail-verify-otp.html", vars);


        model.addAttribute("success", "✅ Đã gửi mã OTP tới " + email);
        loadTokens(model);
        return "email/email-test";
    }

    /** ✅ Xác minh bằng link */
    @GetMapping("/verify")
    public String verifyByLink(@RequestParam String token, Model model) {
        AuthToken t = tokenRepository.findByToken(token).orElse(null);
        if (t == null || t.isUsed() || t.getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "❌ Liên kết không hợp lệ hoặc đã hết hạn!");
        } else {
            t.setUsed(true);
            tokenRepository.save(t);
            model.addAttribute("success", "✅ Xác minh email thành công qua link!");
        }
        loadTokens(model);
        return "email/email-verify-result";
    }

    /** ✅ Xác minh bằng OTP */
    @PostMapping("/verify-otp")
    public String verifyByOtp(@RequestParam String otp, Model model) {
        AuthToken t = tokenRepository.findByToken(otp).orElse(null);
        if (t == null || t.isUsed() || t.getExpiresAt().isBefore(LocalDateTime.now())) {
            model.addAttribute("error", "❌ Mã OTP không hợp lệ hoặc đã hết hạn!");
        } else {
            t.setUsed(true);
            tokenRepository.save(t);
            model.addAttribute("success", "✅ Xác minh email thành công bằng OTP!");
        }
        loadTokens(model);
        return "email/email-test";
    }

    /** 🧾 Trang test chính */
    @GetMapping
    public String showTestPage(Model model) {
        loadTokens(model);
        return "email/email-test";
    }

    /** 🔁 Load danh sách token hiện tại */
    private void loadTokens(Model model) {
        List<AuthToken> tokens = tokenRepository.findAll();
        tokens.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt())); // sắp xếp mới nhất trước
        model.addAttribute("tokens", tokens);
    }
}
