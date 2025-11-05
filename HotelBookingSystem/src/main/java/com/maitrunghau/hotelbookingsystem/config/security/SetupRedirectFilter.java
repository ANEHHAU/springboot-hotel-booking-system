package com.maitrunghau.hotelbookingsystem.config.security;

import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Order(1)
public class SetupRedirectFilter implements Filter {

    private final EmployeeRepository employeeRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String path = req.getRequestURI();

        long adminCount = employeeRepository.countByRole(Role.Admin);

        // 🧩 Ghi log cho debug
        System.out.println("🧩 [SetupRedirectFilter] path=" + path + " | adminCount=" + adminCount);

        // ✅ Nếu chưa có admin
        if (adminCount == 0) {
            // ⚠️ Chỉ cho phép đúng trang tạo admin và file tĩnh
            boolean allowed = path.startsWith("/setup")
                    || path.startsWith("/css")
                    || path.startsWith("/js")
                    || path.startsWith("/images")
                    || path.startsWith("/favicon")
                    || path.contains("."); // file tĩnh .css, .js, .png

            if (!allowed) {
                System.out.println("⚠️ [Filter] No Admin → Redirect from " + path + " → /setup/create-admin");
                res.sendRedirect(req.getContextPath() + "/setup/create-admin");
                return;
            }
        }

        chain.doFilter(request, response);
    }
}
