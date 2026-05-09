package com.project.expensetrackerapi;

import org.junit.jupiter.api.Test;

/**
 * Smoke test — no Spring context loaded so it passes without DB or Redis.
 * Integration/context tests belong in a separate profile with Testcontainers.
 */
class ExpenseTrackerApiApplicationTests {

    @Test
    void contextLoads() {
        // Unit tests for services are in CategoryServiceImplTest and TransactionServiceImplTest
    }
}
