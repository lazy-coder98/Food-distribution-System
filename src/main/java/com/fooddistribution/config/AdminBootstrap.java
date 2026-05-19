package com.fooddistribution.config;

import com.fooddistribution.user.Role;
import com.fooddistribution.user.User;
import com.fooddistribution.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminBootstrap implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${ADMIN_EMAIL:}")
    private String adminEmail;

    @Value("${ADMIN_PASSWORD:}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (adminEmail == null || adminEmail.isBlank() || adminPassword == null || adminPassword.isBlank()) {
            return;
        }
        String normalizedEmail = adminEmail.toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            return;
        }
        User admin = User.builder()
                .fullName("System Administrator")
                .email(normalizedEmail)
                .password(passwordEncoder.encode(adminPassword))
                .phoneNumber("0000000000")
                .role(Role.ADMIN)
                .build();
        userRepository.save(admin);
        log.info("Bootstrap admin user created for {}", normalizedEmail);
    }
}
