package com.maitrunghau.hotelbookingsystem.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import java.io.IOException;


//Xử lý khi người dùng chưa đăng nhập
// mà cố truy cập vào một trang yêu cầu xác thực.
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException {

        response.sendRedirect("/auth/login?error=unauthorized");
    }
}
