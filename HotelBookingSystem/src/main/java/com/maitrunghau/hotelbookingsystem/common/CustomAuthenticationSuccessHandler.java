package com.maitrunghau.hotelbookingsystem.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


//Xử lý khi đăng nhập thành công, và quyết định chuyển hướng
// (redirect) người dùng theo Role.
@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String role = authentication.getAuthorities().iterator().next().getAuthority();

        if (role.contains("Admin") || role.contains("Manager")) {
            response.sendRedirect("/admin/dashboard");
        } else if (role.contains("Customer")) {
            response.sendRedirect("/customer/home");
        } else {
            response.sendRedirect("/");
        }
    }
}
// nâng cao là sẽ vào trang lúc đang  truy cập cần tác vụ đăng nhập để hoàn thiện