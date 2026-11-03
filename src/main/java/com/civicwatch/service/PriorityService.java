package com.civicwatch.service;

import org.springframework.stereotype.Service;

@Service
public class PriorityService {
    
    // Score = (Severity × 40) + (LocationRisk × 30) + (Density × 20) + (Age × 10)
    // Severity: 1-5
    // For simplicity, we assume LocationRisk=3, Density=3, Age=1 for new complaints.
    public double calculateScore(int severity) {
        int locationRisk = 3;
        int density = 3;
        int age = 1;
        
        double maxPossibleScore = (5 * 40) + (5 * 30) + (5 * 20) + (5 * 10);
        double actualScore = (severity * 40) + (locationRisk * 30) + (density * 20) + (age * 10);
        
        return (actualScore / maxPossibleScore) * 100.0;
    }
    
    public String calculateLevel(double score) {
        if (score >= 80) return "CRITICAL";
        if (score >= 60) return "HIGH";
        if (score >= 40) return "MEDIUM";
        return "LOW";
    }
}
