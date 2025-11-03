package com.expensetracker.controller;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import com.expensetracker.service.ExpenseService;

import jakarta.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    
    private final ExpenseService expenseService;
    
    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }
    
    @GetMapping
    public String showExpenses(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/users/login";
            }

            List<Expense> expenses = expenseService.getExpensesByUserId(user.getId());

              double totalAmount = 0.0;
        for (Expense expense : expenses) {
            totalAmount += expense.getAmount();
            System.out.println("💰 Expense: " + expense.getDescription() + " - $" + expense.getAmount());
        }
        long categoryCount = expenses.stream()
                .map(Expense::getCategory)
                .distinct()
                .count();
        
        double averageExpense = expenses.isEmpty() ? 0 : totalAmount / expenses.size();

         // Debug output
        System.out.println("🔢 Statistics calculated:");
        System.out.println("   Total Expenses: " + expenses.size());
        System.out.println("   Total Amount: $" + totalAmount);
        System.out.println("   Categories: " + categoryCount);
        System.out.println("   Average: $" + averageExpense);
        
            
            model.addAttribute("user", user);
            model.addAttribute("expenses", expenses);
            model.addAttribute("newExpense", new Expense());

            System.out.println("✅ Expenses page loaded for: " + user.getUsername() + " with " + expenses.size() + " expenses");
             System.out.println("💰 Total Amount: $" + totalAmount);
       
            
            return "expenses";
        } catch (Exception e) {
            System.out.println("❌ Error loading expenses: " + e.getMessage());
            return "redirect:/users/login";
        }
    }
    
    @GetMapping("/add")
    public String showAddExpensePage(HttpSession session, Model model) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/users/login";
            }
            
            model.addAttribute("user", user);
            model.addAttribute("expense", new Expense());
            return "add-expense";
        } catch (Exception e) {
            System.out.println("❌ Error loading add expense page: " + e.getMessage());
            return "redirect:/users/login";
        }
    }

    @PostMapping("/add")
    public String addExpense(@RequestParam String description,
                            @RequestParam String category,
                            @RequestParam Double amount,
                            HttpSession session,
                            Model model) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/users/login";
            }
            
            // Validate inputs
            if (description == null || description.trim().isEmpty()) {
                model.addAttribute("error", "Description is required");
                model.addAttribute("user", user);
                return "add-expense";
            }
            
            if (amount == null || amount <= 0) {
                model.addAttribute("error", "Amount must be greater than zero");
                model.addAttribute("user", user);
                return "add-expense";
            }
            
            if (category == null || category.trim().isEmpty()) {
                model.addAttribute("error", "Category is required");
                model.addAttribute("user", user);
                return "add-expense";
            }
            
            // Create Expense object
            Expense expense = new Expense();
            expense.setDescription(description.trim());
            expense.setCategory(category);
            expense.setAmount(amount);
            
            expenseService.addExpense(expense, user.getId());
            
            return "redirect:/expenses?success=Expense+added+successfully";
            
        } catch (Exception e) {
            System.out.println("❌ ERROR ADDING EXPENSE: " + e.getMessage());
            
            if (session.getAttribute("user") != null) {
                User user = (User) session.getAttribute("user");
                model.addAttribute("user", user);
            }
            
            model.addAttribute("error", "Failed to add expense: " + e.getMessage());
            return "add-expense";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable Long id, HttpSession session) {
        try {
            User user = (User) session.getAttribute("user");
            if (user == null) {
                return "redirect:/users/login";
            }

            expenseService.deleteExpense(id);
            return "redirect:/expenses?success=Expense+deleted+successfully";
            
        } catch (Exception e) {
            System.out.println("❌ Error deleting expense: " + e.getMessage());
            return "redirect:/expenses?error=Failed+to+delete+expense";
        }
    }
}