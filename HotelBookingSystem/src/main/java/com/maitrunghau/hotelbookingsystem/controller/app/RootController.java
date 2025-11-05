package com.maitrunghau.hotelbookingsystem.controller.app;

import com.maitrunghau.hotelbookingsystem.model.Role;
import com.maitrunghau.hotelbookingsystem.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RootController {

    private final EmployeeRepository employeeRepository;

    @GetMapping("/")
    public String handleRootRedirect() {
        long adminCount = employeeRepository.countByRole(Role.Admin);
        System.out.println(">>> Root redirect check, adminCount = " + adminCount);

        if (adminCount == 0) {
            // ❌ KHÔNG redirect ở đây nữa – filter lo rồi
            return "forward:/setup/create-admin"; // hoặc return "redirect:/setup/create-admin"; nếu bạn muốn thủ công
        } else {
            System.out.println(">>> Redirecting to /auth/login");
            return "redirect:/auth/login";
        }
    }
}
