package com.expensetracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ExpenseTrackerApplicationTests {

    @Test
    void contextLoads() {
        // Basic test - verifies Spring context loads
    }

    @Test
    void applicationStarts() {
        // Test that the main method can be executed
        ExpenseTrackerApplication.main(new String[] {});
    }

    @Test
    void basicAssertion() {
        // Simple test to verify JUnit is working
        assertThat(1 + 1).isEqualTo(2);
    }
}