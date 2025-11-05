package com.maitrunghau.hotelbookingsystem.service.impl;

import com.maitrunghau.hotelbookingsystem.config.AppProperties;
import com.maitrunghau.hotelbookingsystem.model.AuthToken;
import com.maitrunghau.hotelbookingsystem.model.Customer;
import com.maitrunghau.hotelbookingsystem.model.TokenType;
import com.maitrunghau.hotelbookingsystem.repository.AuthTokenRepository;
import com.maitrunghau.hotelbookingsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailAuthService {

    private final EmailService emailService;
    private final AuthTokenRepository tokenRepository;
    private final AppProperties appProps;

    public void sendVerificationLink(Customer customer) {
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(appProps.getEmailVerifyExpireMinutes());

        tokenRepository.save(AuthToken.builder()
                .customer(customer)
                .token(token)
                .type(TokenType.EMAIL_VERIFY)
                .expiresAt(expiresAt)
                .used(false)
                .build());

        String link = appProps.getBaseUrl() + "/auth/verify-email?token=" + token;

        Map<String, Object> vars = Map.of(
                "name", customer.getFull_name(),
                "verifyLink", link,
                "expireMinutes", appProps.getEmailVerifyExpireMinutes()
        );

        emailService.sendEmail(customer.getEmail(), "Xác minh tài khoản", "mail-verify-link.html", vars);
        log.info("✅ Đã gửi link xác minh đến {}", customer.getEmail());
    }

    public void sendOtp(Customer customer) {
        String otp = String.valueOf((int) (Math.random() * 900000) + 100000);
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(appProps.getOtpExpireMinutes());

        tokenRepository.save(AuthToken.builder()
                .customer(customer)
                .token(otp)
                .type(TokenType.EMAIL_OTP)
                .expiresAt(expiresAt)
                .used(false)
                .build());

        Map<String, Object> vars = Map.of(
                "name", customer.getFull_name(),
                "otp", otp,
                "expireMinutes", appProps.getOtpExpireMinutes()
        );

        emailService.sendEmail(customer.getEmail(), "Mã OTP xác thực", "otp-email.html", vars);
        log.info("✅ Đã gửi OTP {} đến {}", otp, customer.getEmail());
    }


}
