package com.expensetracker.service;

import com.expensetracker.model.Expense;
import com.expensetracker.model.User;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    private final ExpenseService expenseService;

    public AnalyticsService(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    // Get category-wise spending breakdown
    public Map<String, Double> getCategorySpending(Long userId) {
        try {
            System.out.println("🔍 Getting expenses for user ID: " + userId);
            List<Expense> userExpenses = expenseService.getExpensesByUserId(userId);
            System.out.println("📊 Found " + userExpenses.size() + " expenses for analytics");
            
            if (userExpenses.isEmpty()) {
                System.out.println("⚠️ No expenses found for user " + userId);
                return new HashMap<>();
            }
            
            return userExpenses.stream()
                    .collect(Collectors.groupingBy(
                        Expense::getCategory,
                        Collectors.summingDouble(Expense::getAmount)
                    ));
        } catch (Exception e) {
            System.out.println("❌ Error in getCategorySpending for user " + userId + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Get spending percentages for each category
    public Map<String, Double> getCategoryPercentages(Long userId) {
        try {
            Map<String, Double> categorySpending = getCategorySpending(userId);
            double totalSpending = categorySpending.values().stream().mapToDouble(Double::doubleValue).sum();
            
            Map<String, Double> percentages = new HashMap<>();
            for (Map.Entry<String, Double> entry : categorySpending.entrySet()) {
                double percentage = totalSpending > 0 ? (entry.getValue() / totalSpending) * 100 : 0;
                percentages.put(entry.getKey(), Math.round(percentage * 100.0) / 100.0);
            }
            
            return percentages;
        } catch (Exception e) {
            System.out.println("❌ Error in getCategoryPercentages for user " + userId + ": " + e.getMessage());
            return new HashMap<>();
        }
    }

    // Generate personalized recommendations
    public List<String> getSpendingRecommendations(Long userId, User user) {
        try {
            List<String> recommendations = new ArrayList<>();
            
            // Add basic recommendations first
            recommendations.add("💡 Track your expenses daily for better insights");
            recommendations.add("📊 Review your spending patterns weekly");
            
            // Check if user exists
            if (user == null) {
                recommendations.add("ℹ️ Complete your financial profile for personalized recommendations");
                return recommendations;
            }
            
            // Check if financial data is configured (not the default 0.0 values)
            double monthlyIncome = user.getMonthlyIncome();
            double expenseLimit = user.getMonthlyExpenseLimit();
            
            // Check if financial data is configured (not the default 0.0 values)
            if (monthlyIncome <= 0 || expenseLimit <= 0) {
                recommendations.add("ℹ️ Set up your monthly income and expense limit for personalized recommendations");
                return recommendations;
            }
            
            Map<String, Double> percentages = getCategoryPercentages(userId);
            Map<String, Double> spending = getCategorySpending(userId);
            
            double totalSpending = spending.values().stream().mapToDouble(Double::doubleValue).sum();
        
            // Recommendation 1: Overall spending vs income
            if (totalSpending > monthlyIncome * 0.8) {
                recommendations.add("⚠️ You're spending over 80% of your income. Consider reducing non-essential expenses.");
            }
            
            if (totalSpending > expenseLimit) {
                recommendations.add("🚨 You've exceeded your monthly expense limit! Immediate action needed.");
            }
            
            // Recommendation 2: High spending categories
            for (Map.Entry<String, Double> entry : percentages.entrySet()) {
                if (entry.getValue() > 40) {
                    recommendations.add("📊 Your " + entry.getKey() + " spending is " + entry.getValue() + 
                                      "% of total. This seems high - consider budgeting for this category.");
                }
            }
            
            // Recommendation 3: Specific category advice
            if (percentages.getOrDefault("Entertainment", 0.0) > 20) {
                recommendations.add("🎬 Entertainment spending is high. Look for free alternatives.");
            }
            
            if (percentages.getOrDefault("Shopping", 0.0) > 25) {
                recommendations.add("🛍️ Shopping expenses are significant. Consider a 24-hour cooling off period before purchases.");
            }
            
            if (percentages.getOrDefault("Food", 0.0) > 30) {
                recommendations.add("🍽️ Food expenses are substantial. Meal planning could help reduce costs.");
            }
            
            // Recommendation 4: Positive reinforcement
            if (totalSpending < monthlyIncome * 0.5) {
                recommendations.add("✅ Great job! You're spending less than 50% of your income.");
            }
            
            return recommendations;
            
        } catch (Exception e) {
            System.out.println("❌ Error in getSpendingRecommendations for user " + userId + ": " + e.getMessage());
            List<String> fallback = new ArrayList<>();
            fallback.add("💡 Start tracking your expenses to get personalized recommendations");
            return fallback;
        }
    }

    // Get monthly progress
    public Map<String, Object> getMonthlyProgress(Long userId, User user) {
        try {
            Map<String, Object> progress = new HashMap<>();
            double totalSpent = expenseService.getExpensesByUserId(userId).stream()
                    .mapToDouble(Expense::getAmount)
                    .sum();
            
            // Since these are primitives (double), they can't be null
            double monthlyIncome = user.getMonthlyIncome();
            double expenseLimit = user.getMonthlyExpenseLimit();
            
            progress.put("totalSpent", totalSpent);
            progress.put("monthlyIncome", monthlyIncome);
            progress.put("expenseLimit", expenseLimit);
            progress.put("remainingBudget", expenseLimit - totalSpent);
            
            // Safe division to avoid divide by zero
            double percentageOfIncome = monthlyIncome > 0 ? (totalSpent / monthlyIncome) * 100 : 0;
            double percentageOfLimit = expenseLimit > 0 ? (totalSpent / expenseLimit) * 100 : 0;
            
            progress.put("percentageOfIncome", Math.round(percentageOfIncome * 100.0) / 100.0);
            progress.put("percentageOfLimit", Math.round(percentageOfLimit * 100.0) / 100.0);
            
            return progress;
            
        } catch (Exception e) {
            System.out.println("❌ Error in getMonthlyProgress for user " + userId + ": " + e.getMessage());
            Map<String, Object> fallback = new HashMap<>();
            fallback.put("totalSpent", 0.0);
            fallback.put("monthlyIncome", 0.0);
            fallback.put("expenseLimit", 0.0);
            fallback.put("remainingBudget", 0.0);
            fallback.put("percentageOfIncome", 0.0);
            fallback.put("percentageOfLimit", 0.0);
            return fallback;
        }
    }

    // Alternative method for user analytics
    public Map<String, Object> getUserAnalytics(User user) {
        Map<String, Object> analytics = new HashMap<>();
        try {
            if (user != null) {
                Long userId = user.getId();
                analytics.put("categorySpending", getCategorySpending(userId));
                analytics.put("categoryPercentages", getCategoryPercentages(userId));
                analytics.put("recommendations", getSpendingRecommendations(userId, user));
                analytics.put("monthlyProgress", getMonthlyProgress(userId, user));
                System.out.println("✅ User analytics generated for: " + user.getUsername());
            } else {
                System.out.println("❌ Cannot generate analytics for null user");
            }
        } catch (Exception e) {
            System.out.println("❌ Error generating user analytics: " + e.getMessage());
        }
        return analytics;
    }

    // Alias method for getCategoryPercentages
    public Map<String, Double> getCategoryPercentage(Long userId) {
        return getCategoryPercentages(userId);
    }
}