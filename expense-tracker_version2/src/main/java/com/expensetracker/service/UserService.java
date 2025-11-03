package com.expensetracker.service;

import com.expensetracker.model.User;
import com.expensetracker.repository.UserRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        System.out.println("✅ UserService initialized");
    }
    
    public boolean registerUser(User user) {
        try {
            List<User> users = userRepository.loadUsers();
            
            // Check if user exists
            for (User u : users) {
                if (u.getUsername().equals(user.getUsername()) || u.getEmail().equals(user.getEmail())) {
                    return false;
                }
            }
            
            // Generate ID
            Long newId = users.isEmpty() ? 1L : users.get(users.size() - 1).getId() + 1;
            user.setId(newId);
            
            users.add(user);
            userRepository.saveUsers(users);
            return true;
            
        } catch (Exception e) {
            System.out.println("❌ Error registering user: " + e.getMessage());
            return false;
        }
    }
    
    public User loginUser(String username, String password) {
        try {
            List<User> users = userRepository.loadUsers();
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                    return user;
                }
            }
            return null;
        } catch (Exception e) {
            System.out.println("❌ Error logging in: " + e.getMessage());
            return null;
        }
    }
    
    public List<User> getAllUsers() {
        return userRepository.loadUsers();
    }
}