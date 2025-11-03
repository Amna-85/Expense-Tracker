package com.expensetracker.controller;

import com.expensetracker.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;

@Controller
public class DashboardController {
    
    @GetMapping("/dashboard")
    public String showDashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) {
            return "redirect:/users/login";
        }
        model.addAttribute("user", user);
        return "dashboard";
    }
    
    @GetMapping("/")
    public String home() {
        return "redirect:/users/login";
    }
}