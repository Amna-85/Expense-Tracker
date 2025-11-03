package com.expensetracker.repository;

import com.expensetracker.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository
public class UserRepository {
    private static final String USERS_FILE = "users.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<User> loadUsers() {
        try {
            File file = new File(USERS_FILE);
            if (file.exists()) {
                User[] usersArray = objectMapper.readValue(file, User[].class);
                return new ArrayList<>(Arrays.asList(usersArray));
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading users: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    public void saveUsers(List<User> users) {
        try {
            objectMapper.writeValue(new File(USERS_FILE), users);
            System.out.println("✅ Saved " + users.size() + " users to file");
        } catch (IOException e) {
            System.out.println("❌ Error saving users: " + e.getMessage());
        }
    }
}