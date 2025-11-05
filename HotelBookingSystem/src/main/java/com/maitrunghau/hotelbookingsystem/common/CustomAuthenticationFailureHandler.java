package com.maitrunghau.hotelbookingsystem.common;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;


//Xử lý khi đăng nhập thất bại, ví dụ:
@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        request.getSession().setAttribute("ERROR_MESSAGE", "Sai thông tin đăng nhập hoặc tài khoản bị vô hiệu!");
        response.sendRedirect("/auth/login?error=true");
    }
}
// đăng nhập fail