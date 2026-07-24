package com.exam;

import com.exam.model.User;
import com.exam.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootApplication
@EnableAsync
public class ExamApplication {

    public static void main(String[] args) {
        SpringApplication.run(ExamApplication.class, args);
    }

    @Bean
    @Transactional
    public CommandLineRunner initSingleAdmin(UserRepository userRepo, PasswordEncoder encoder) {
        return args -> {
            String adminEmail = "admin@edu.com";
            
            // Checks if the master admin exists before creating it
            if (userRepo.findByEmail(adminEmail).isEmpty()) {
                User admin = new User();
                admin.setName("System Admin");
                admin.setEmail(adminEmail);
                admin.setPasswordHash(encoder.encode("admin123"));
                admin.setRole(User.Role.ADMIN);
                
                userRepo.save(admin);
                System.out.println(">> Single Admin account initialized: " + adminEmail);
            } else {
                System.out.println(">> Admin account already present in database.");
            }
        };
    }
}