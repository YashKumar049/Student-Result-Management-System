package com.srms.srms_app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    // Show the login page
    @GetMapping("/login")
    public String login() {
        // Corresponds to src/main/resources/templates/index.html
        return "index";
    }

    // Redirect root URL ("/") to the login page
    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}