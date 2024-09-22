package com.assignment.project.BookingMs.Config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class BookingCircuitBreaker {

    @Bean
    public CircuitBreaker inventoryServiceCircuitBreaker() {
        // Configure the circuit breaker
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .failureRateThreshold(50) // When 50% of requests fail, open the circuit
                .waitDurationInOpenState(Duration.ofSeconds(10)) // Wait for 10 seconds before trying again
                .slidingWindowSize(10) // Use the last 10 requests to determine the failure rate
                .build();

        // Create a registry with the default or custom configuration
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        // Return a CircuitBreaker instance for "inventoryService"
        return circuitBreakerRegistry.circuitBreaker("inventoryService");
    }

    @Bean
    public RetryConfig retryConfig() {
        return RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(100))
                .retryExceptions(Exception.class)
                .build();
    }
}
