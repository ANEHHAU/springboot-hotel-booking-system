package com.maitrunghau.hotelbookingsystem.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {
    @GetMapping("/customers")
    public String customerView() {
        return "customer";
    }
}
