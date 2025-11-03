package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }
    
    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, Model model, HttpSession session) {
        boolean success = userService.registerUser(user);
        if (success) {
            // ✅ FIXED: Auto-login after successful registration
            User loggedInUser = userService.loginUser(user.getUsername(), user.getPassword());
            if (loggedInUser != null) {
                session.setAttribute("user", loggedInUser);
                System.out.println("✅ Auto-login successful after registration for: " + loggedInUser.getUsername());
                return "redirect:/dashboard"; // ✅ Redirect to dashboard
            } else {
                model.addAttribute("error", "Registration successful but auto-login failed. Please login manually.");
                return "redirect:/users/login";
            }
        } else {
            model.addAttribute("error", "Registration failed! User may already exist.");
            // Keep form data for retry
            model.addAttribute("firstName", user.getFirstName());
            model.addAttribute("lastName", user.getLastName());
            model.addAttribute("username", user.getUsername());
            model.addAttribute("email", user.getEmail());
            model.addAttribute("monthlyIncome", user.getMonthlyIncome());
            model.addAttribute("monthlyExpenseLimit", user.getMonthlyExpenseLimit());
            return "register";
        }
    }
    
    @GetMapping("/login")
    public String showLoginPage(Model model) {
        return "login";
    }
    
    @PostMapping("/login")
    public String loginUser(@RequestParam String username, @RequestParam String password, 
                           Model model, HttpSession session) {
        User user = userService.loginUser(username, password);
        if (user != null) {
            session.setAttribute("user", user);
            System.out.println("✅ Login successful! Session created for: " + user.getUsername());
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid username or password!");
            return "login";
        }
    }
    
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/users/login";
    }
}