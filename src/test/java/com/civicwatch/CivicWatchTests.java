package com.civicwatch;

import com.civicwatch.model.Complaint;
import com.civicwatch.model.Role;
import com.civicwatch.model.User;
import com.civicwatch.service.PriorityService;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

public class CivicWatchTests {

    @Test
    public void testPriorityCalculation() {
        PriorityService service = new PriorityService();
        double score = service.calculateScore(5); // Severity 5
        assertEquals("CRITICAL", service.calculateLevel(score));
        
        double scoreLow = service.calculateScore(1); // Severity 1
        assertEquals("LOW", service.calculateLevel(scoreLow));
    }

    @Test
    public void testPasswordVerification() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();
        String rawPassword = "password123";
        String encoded = encoder.encode(rawPassword);
        
        assertTrue(encoder.matches(rawPassword, encoded));
        assertFalse(encoder.matches("wrongpass", encoded));
    }
    
    @Test
    public void testComplaintCreation() {
        User citizen = new User();
        citizen.setName("Test");
        citizen.setRole(Role.CITIZEN);
        
        Complaint complaint = new Complaint();
        complaint.setTitle("Test Pothole");
        complaint.setSeverity(4);
        
        assertNotNull(complaint);
        assertEquals("Test Pothole", complaint.getTitle());
    }
}
