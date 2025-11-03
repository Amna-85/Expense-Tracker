package com.expensetracker.controller;

import com.expensetracker.model.User;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.SessionScope;

import jakarta.servlet.http.HttpSession;

@Component
@SessionScope
public class SessionController {
    private User currentUser;

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    public void logout() {
        this.currentUser = null;
    }
    
    // Helper method to get user from HttpSession
    public static User getCurrentUser(HttpSession session) {
        return (User) session.getAttribute("user");
    }
    
    // Helper method to check if user is logged in via HttpSession
    public static boolean isUserLoggedIn(HttpSession session) {
        return session.getAttribute("user") != null;
    }
}