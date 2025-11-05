package com.maitrunghau.hotelbookingsystem.controller.customer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("/customer")
public class CustomerViewController {

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("pageTitle", "Trang khách hàng");
        log.info("🏠 Truy cập Customer Home");
        return "customer/home";
    }
}
