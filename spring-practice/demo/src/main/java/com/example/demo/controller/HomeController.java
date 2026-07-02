package com.example.demo.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Authentication authentication) {
        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetails;
        return loggedIn ? "redirect:/game" : "redirect:/login";
    }
}
