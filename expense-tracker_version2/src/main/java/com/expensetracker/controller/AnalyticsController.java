package com.expensetracker.controller;

import com.expensetracker.model.User;
import com.expensetracker.model.Expense;
import com.expensetracker.service.AnalyticsService;
import com.expensetracker.service.ExpenseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@Controller
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final ExpenseService expenseService;
    
    public AnalyticsController(AnalyticsService analyticsService, ExpenseService expenseService) {
        this.analyticsService = analyticsService;
        this.expenseService = expenseService;
    }
    
    @GetMapping("/analytics")
    public String showAnalytics(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user == null) return "redirect:/users/login";
        
        try {
            // Get user expenses for basic calculations
            List<Expense> expenses = expenseService.getExpensesByUserId(user.getId());
            
            // Calculate basic statistics
            double totalSpent = expenses.stream().mapToDouble(Expense::getAmount).sum();
            int expensesCount = expenses.size();
            long categoryCount = expenses.stream().map(Expense::getCategory).distinct().count();
            double averageExpense = expensesCount > 0 ? totalSpent / expensesCount : 0;
            
            // Calculate budget progress
            double monthlyIncome = user.getMonthlyIncome();
            double expenseLimit = user.getMonthlyExpenseLimit();
            double remainingBudget = expenseLimit - totalSpent;
            double percentageOfLimit = expenseLimit > 0 ? (totalSpent / expenseLimit) * 100 : 0;
            
            // Get analytics data
            Map<String, Double> categorySpending = analyticsService.getCategorySpending(user.getId());
            Map<String, Double> categoryPercentages = analyticsService.getCategoryPercentage(user.getId());
            List<String> recommendations = analyticsService.getSpendingRecommendations(user.getId(), user);
            
            // Prepare chart data
            List<String> chartLabels = new ArrayList<>(categorySpending.keySet());
            List<Double> chartData = new ArrayList<>(categorySpending.values());
            
            // Add all attributes to model
            model.addAttribute("user", user);
            model.addAttribute("expensesCount", expensesCount);
            model.addAttribute("totalSpent", totalSpent);
            model.addAttribute("categoryCount", categoryCount);
            model.addAttribute("averageExpense", averageExpense);
            model.addAttribute("remainingBudget", remainingBudget);
            model.addAttribute("percentageOfLimit", percentageOfLimit);
            model.addAttribute("categorySpending", categorySpending);
            model.addAttribute("categoryPercentages", categoryPercentages);
            model.addAttribute("chartLabels", chartLabels);
            model.addAttribute("chartData", chartData);
            model.addAttribute("recommendations", recommendations != null ? recommendations : new ArrayList<>());
            
            System.out.println("✅ Analytics loaded for user: " + user.getUsername());
            System.out.println("📊 Expenses: " + expensesCount + ", Total: $" + totalSpent + ", Categories: " + categoryCount);
            
            return "analytics";
            
        } catch (Exception e) {
            System.out.println("❌ Error in analytics: " + e.getMessage());
            e.printStackTrace();
            
            // Fallback with basic data
            model.addAttribute("user", user);
            model.addAttribute("error", "Unable to load analytics data. Please try again.");
            model.addAttribute("expensesCount", 0);
            model.addAttribute("totalSpent", 0.0);
            model.addAttribute("categoryCount", 0);
            model.addAttribute("averageExpense", 0.0);
            model.addAttribute("remainingBudget", user.getMonthlyExpenseLimit());
            model.addAttribute("percentageOfLimit", 0.0);
            model.addAttribute("categorySpending", new HashMap<>());
            model.addAttribute("categoryPercentages", new HashMap<>());
            model.addAttribute("chartLabels", new ArrayList<>());
            model.addAttribute("chartData", new ArrayList<>());
            model.addAttribute("recommendations", Arrays.asList("Start adding expenses to see analytics!"));
            
            return "analytics";
        }
    }
}