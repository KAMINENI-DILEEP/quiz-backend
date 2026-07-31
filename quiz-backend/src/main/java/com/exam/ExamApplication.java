package com.exam;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.exam.service.DisplayIdService;

@SpringBootApplication
@EnableAsync
public class ExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }

    @Bean
    public CommandLineRunner initSingleAdmin(UserRepository userRepository, PasswordEncoder passwordEncoder, DisplayIdService displayIdService) {
        return args -> {
            try {
                String email = "dileepkamineni@gmail.com";
                if (userRepository.findByEmail(email).isEmpty()) {
                    User admin = new User();
                    admin.setName("Dileep Kamineni");
                    admin.setEmail(email);
                    admin.setPasswordHash(passwordEncoder.encode("188930"));
                    admin.setRole(User.Role.ADMIN);
                    admin.setDisplayId(displayIdService.next(User.Role.ADMIN));
                    admin.setMfaEnabled(false);
                    userRepository.save(admin);
                    System.out.println("Default administrator created: " + email);
                }
            } catch (Exception e) {
                System.out.println("Skipping admin init on startup: " + e.getMessage());
            }
        };
    }
}