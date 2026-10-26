package com.smartloan.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.smartloan.engine.FinancialIntelligenceService.PrepaymentOptimizationPlan;
import com.smartloan.model.Loan;
import com.smartloan.model.User;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.Test;

class FinancialIntelligenceServiceTest {
    @Test
    void comparesAvalancheAndSnowballUsingProvidedExecutor() {
        List<Loan> loans = List.of(
            new Loan(1, 1, "Home", 100000, 5, 120),
            new Loan(2, 1, "Personal", 20000, 12, 36),
            new Loan(3, 1, "Auto", 50000, 8, 48)
        );
        User user = new User(1, "demo", "demo", 100000, 30000, 90000);
        AtomicInteger submittedTasks = new AtomicInteger();
        Set<String> executingThreads = ConcurrentHashMap.newKeySet();
        ExecutorService workers = Executors.newFixedThreadPool(
            2, task -> new Thread(task, "strategy-test")
        );
        Executor trackingExecutor = task -> {
            submittedTasks.incrementAndGet();
            workers.execute(() -> {
                executingThreads.add(Thread.currentThread().getName());
                task.run();
            });
        };

        try {
            PrepaymentOptimizationPlan plan =
                FinancialIntelligenceService.evaluatePrepayment(user, loans, 5000, trackingExecutor);

            assertEquals(2, submittedTasks.get());
            assertTrue(!executingThreads.isEmpty());
            assertTrue(executingThreads.stream().allMatch(name -> name.startsWith("strategy-test")));
            assertEquals(4, plan.getStrategyComparisons().size());
            assertTrue(plan.getStrategyComparisons().get(0).getStrategyName().startsWith("Debt Avalanche"));
            assertTrue(plan.getStrategyComparisons().get(0).getTargetLoanName().startsWith("Personal"));
            assertEquals("Debt Snowball", plan.getStrategyComparisons().get(1).getStrategyName());
            assertTrue(plan.getStrategyComparisons().get(1).getTargetLoanName().startsWith("Personal"));
        } finally {
            workers.shutdownNow();
        }
    }
}
