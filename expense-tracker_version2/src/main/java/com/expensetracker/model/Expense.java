package com.expensetracker.model;

public class Expense {
    private Long id;
    private String description;
    private Double amount;
    private String category;
    private Long userId;

    // Simple constructor
    public Expense() {}

    // Constructor for creating expenses
    public Expense(String description, Double amount, String category) {
        this.description = description;
        this.amount = amount;
        this.category = category;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getAmount() { return amount; }
    public void setAmount(Double amount) { this.amount = amount; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { 
        this.userId = userId; 
        System.out.println("🔍 Setting User ID in Expense model: " + userId);
    }
}