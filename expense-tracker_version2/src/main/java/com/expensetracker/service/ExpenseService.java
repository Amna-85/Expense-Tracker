package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private static final String EXPENSES_FILE = "expenses.json";
    private final ObjectMapper objectMapper = new ObjectMapper();
    private List<Expense> expenses;
    private final AtomicLong expenseIdCounter = new AtomicLong(1);

    public ExpenseService() {
        // Load expenses from file on startup
        this.expenses = loadExpenses();
        
        // Set the next ID based on loaded expenses
        if (!expenses.isEmpty()) {
            long maxId = expenses.stream().mapToLong(Expense::getId).max().orElse(0L);
            expenseIdCounter.set(maxId + 1);
        }
        
        System.out.println("✅ ExpenseService initialized with " + expenses.size() + " expenses from storage");
        
        // If no expenses exist, add some sample data for testing
        if (expenses.isEmpty()) {
            addSampleExpenses();
        }
    }

    // Load expenses from JSON file
    public List<Expense> loadExpenses() {
        try {
            File file = new File(EXPENSES_FILE);
            if (file.exists()) {
                Expense[] expensesArray = objectMapper.readValue(file, Expense[].class);
                System.out.println("✅ Loaded " + expensesArray.length + " expenses from file");
                return new ArrayList<>(Arrays.asList(expensesArray));
            }
        } catch (IOException e) {
            System.out.println("❌ Error loading expenses: " + e.getMessage());
        }
        return new ArrayList<>();
    }

    // Save expenses to JSON file
    public void saveExpenses(List<Expense> expenses) {
        try {
            objectMapper.writeValue(new File(EXPENSES_FILE), expenses);
            System.out.println("✅ Saved " + expenses.size() + " expenses to file");
        } catch (IOException e) {
            System.out.println("❌ Error saving expenses: " + e.getMessage());
        }
    }

    // Add sample expenses for testing (only if no expenses exist)
    private void addSampleExpenses() {
        try {
            Expense expense1 = new Expense("Groceries", 150.75, "Food");
            expense1.setId(expenseIdCounter.getAndIncrement());
            expense1.setUserId(1L);

            Expense expense2 = new Expense("Electricity Bill", 89.99, "Utilities");
            expense2.setId(expenseIdCounter.getAndIncrement());
            expense2.setUserId(1L);

            Expense expense3 = new Expense("Dinner", 45.50, "Food");
            expense3.setId(expenseIdCounter.getAndIncrement());
            expense3.setUserId(1L);

            expenses.add(expense1);
            expenses.add(expense2);
            expenses.add(expense3);
            
            saveExpenses(expenses);
            System.out.println("✅ Added " + expenses.size() + " sample expenses for testing");
        } catch (Exception e) {
            System.out.println("❌ Error adding sample expenses: " + e.getMessage());
        }
    }

    // FIXED: Return void instead of boolean and add proper validation
    public void addExpense(Expense expense, Long userId) {
        try {
            System.out.println("=== ADDING EXPENSE ===");
            System.out.println("🔍 User ID: " + userId);
            System.out.println("🔍 Expense Description: " + expense.getDescription());
            System.out.println("🔍 Expense Amount: " + expense.getAmount());
            System.out.println("🔍 Expense Category: " + expense.getCategory());
            
            // Validate inputs
            if (userId == null) {
                throw new RuntimeException("User ID cannot be null");
            }
            if (expense.getDescription() == null || expense.getDescription().trim().isEmpty()) {
                throw new RuntimeException("Expense description cannot be empty");
            }
            if (expense.getAmount() == null || expense.getAmount() <= 0) {
                throw new RuntimeException("Expense amount must be greater than 0");
            }
            if (expense.getCategory() == null || expense.getCategory().trim().isEmpty()) {
                throw new RuntimeException("Expense category cannot be empty");
            }
            
            // Set expense properties
            expense.setId(expenseIdCounter.getAndIncrement());
            expense.setUserId(userId);
            
            // Add to list
            expenses.add(expense);
            
            // Save to file
            saveExpenses(expenses);
            
            System.out.println("✅ Expense added successfully for user ID " + userId + ": " + 
                expense.getDescription() + " - $" + expense.getAmount());
                
        } catch (Exception e) {
            System.out.println("❌ Error adding expense: " + e.getMessage());
            throw new RuntimeException("Failed to add expense: " + e.getMessage());
        }
    }

    // Get expenses by user ID
    public List<Expense> getExpensesByUserId(Long userId) {
        try {
            List<Expense> userExpenses = expenses.stream()
                    .filter(expense -> expense.getUserId() != null && expense.getUserId().equals(userId))
                    .collect(Collectors.toList());
            System.out.println("✅ Found " + userExpenses.size() + " expenses for user ID: " + userId);
            return userExpenses;
        } catch (Exception e) {
            System.out.println("❌ Error getting expenses for user " + userId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    // Get all expenses
    public List<Expense> getAllExpenses() {
        return new ArrayList<>(expenses);
    }

    // Delete expense
    public void deleteExpense(Long id) {
        try {
            boolean removed = expenses.removeIf(expense -> expense.getId().equals(id));
            if (removed) {
                saveExpenses(expenses);
                System.out.println("✅ Expense deleted: ID " + id);
            } else {
                System.out.println("⚠️ Expense not found for deletion: ID " + id);
            }
        } catch (Exception e) {
            System.out.println("❌ Error deleting expense: " + e.getMessage());
            throw new RuntimeException("Failed to delete expense: " + e.getMessage());
        }
    }
}