package com.civicwatch.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.civicwatch.model.Role;
import com.civicwatch.model.User;
import com.civicwatch.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User login(String email, String rawPassword) {
        Optional<User> opt = userRepository.findByEmail(email);

        if (opt.isPresent()) {
            User user = opt.get();

            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return user;
            }
        }

        return null;
    }

    // Get all officers for Admin assignment
    public List<User> getOfficers() {
        return userRepository.findByRole(Role.OFFICER);
    }

    @PostConstruct
    public void seedData() {

        if (userRepository.count() == 0) {

            // Demo Citizen
            User citizen = new User();
            citizen.setName("Demo Citizen");
            citizen.setEmail("citizen@civicwatch.com");
            citizen.setPassword(passwordEncoder.encode("citizen123"));
            citizen.setRole(Role.CITIZEN);
            userRepository.save(citizen);

            // Officer 1
            User officer = new User();
            officer.setName("Demo Officer");
            officer.setEmail("officer@civicwatch.com");
            officer.setPassword(passwordEncoder.encode("officer123"));
            officer.setRole(Role.OFFICER);
            userRepository.save(officer);

            // Officer 2
            User officer2 = new User();
            officer2.setName("Demo Officer 2");
            officer2.setEmail("officer2@civicwatch.com");
            officer2.setPassword(passwordEncoder.encode("officer123"));
            officer2.setRole(Role.OFFICER);
            userRepository.save(officer2);

            // Officer 3
            User officer3 = new User();
            officer3.setName("Demo Officer 3");
            officer3.setEmail("officer3@civicwatch.com");
            officer3.setPassword(passwordEncoder.encode("officer123"));
            officer3.setRole(Role.OFFICER);
            userRepository.save(officer3);

            // Demo Admin
            User admin = new User();
            admin.setName("Demo Admin");
            admin.setEmail("admin@civicwatch.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

        } else {

            // Add Officer 2 if missing
            if (userRepository.findByEmail("officer2@civicwatch.com").isEmpty()) {
                User officer2 = new User();
                officer2.setName("Demo Officer 2");
                officer2.setEmail("officer2@civicwatch.com");
                officer2.setPassword(passwordEncoder.encode("officer123"));
                officer2.setRole(Role.OFFICER);
                userRepository.save(officer2);
            }

            // Add Officer 3 if missing
            if (userRepository.findByEmail("officer3@civicwatch.com").isEmpty()) {
                User officer3 = new User();
                officer3.setName("Demo Officer 3");
                officer3.setEmail("officer3@civicwatch.com");
                officer3.setPassword(passwordEncoder.encode("officer123"));
                officer3.setRole(Role.OFFICER);
                userRepository.save(officer3);
            }
        }
    }
}