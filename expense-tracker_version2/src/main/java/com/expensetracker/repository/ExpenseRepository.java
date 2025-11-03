package com.expensetracker.repository;

import com.expensetracker.model.Expense;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Repository  // ✅ MUST HAVE THIS ANNOTATION
public class ExpenseRepository {
    private static final String EXPENSES_FILE = "expenses.json";
    private final ObjectMapper objectMapper = new ObjectMapper();

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

    public void saveExpenses(List<Expense> expenses) {
        try {
            objectMapper.writeValue(new File(EXPENSES_FILE), expenses);
            System.out.println("✅ Saved " + expenses.size() + " expenses to file");
        } catch (IOException e) {
            System.out.println("❌ Error saving expenses: " + e.getMessage());
        }
    }
}