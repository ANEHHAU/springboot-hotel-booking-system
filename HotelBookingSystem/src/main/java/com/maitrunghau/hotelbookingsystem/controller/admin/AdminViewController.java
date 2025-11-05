package com.maitrunghau.hotelbookingsystem.controller.admin;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/admin")
public class AdminViewController {

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("pageTitle", "Bảng điều khiển quản trị");
        log.info("🧭 Truy cập Admin Dashboard");
        return "admin/dashboard"; // -> /templates/admin/dashboard.html
    }
}
