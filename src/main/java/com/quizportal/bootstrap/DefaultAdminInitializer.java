package com.quizportal.bootstrap;

import com.quizportal.entity.DisplayIdSequence;
import com.quizportal.entity.PortalSettings;
import com.quizportal.entity.User;
import com.quizportal.enums.Gender;
import com.quizportal.enums.Role;
import com.quizportal.repository.DisplayIdSequenceRepository;
import com.quizportal.repository.PortalSettingsRepository;
import com.quizportal.repository.UserRepository;
import com.quizportal.service.DisplayIdService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DefaultAdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PortalSettingsRepository portalRepository;
    private final DisplayIdSequenceRepository sequenceRepository;
    private final DisplayIdService displayIdService;
    private final PasswordEncoder passwordEncoder;

    public DefaultAdminInitializer(
            UserRepository userRepository,
            PortalSettingsRepository portalRepository,
            DisplayIdSequenceRepository sequenceRepository,
            DisplayIdService displayIdService,
            PasswordEncoder passwordEncoder) {

        this.userRepository = userRepository;
        this.portalRepository = portalRepository;
        this.sequenceRepository = sequenceRepository;
        this.displayIdService = displayIdService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {

        initializeSequences();

        initializePortalSettings();

        initializeAdmin();
    }

    private void initializeSequences() {

        if (!sequenceRepository.existsById("ADM")) {
            sequenceRepository.save(
                    new DisplayIdSequence("ADM", 0L));
        }

        if (!sequenceRepository.existsById("STU")) {
            sequenceRepository.save(
                    new DisplayIdSequence("STU", 0L));
        }

    }

    private void initializePortalSettings() {

        if (!portalRepository.existsById(1)) {

            PortalSettings settings = new PortalSettings();

            settings.setId(1);
            settings.setRegistrationEnabled(true);
            settings.setMaintenanceMode(false);
            settings.setDarkThemeDefault(true);
            settings.setAutoRefreshSeconds(5);
            settings.setMaxUsers(1000);

            portalRepository.save(settings);

        }

    }

    private void initializeAdmin() {

        if (userRepository.countByRole(Role.ADMIN) > 0) {
            return;
        }

        User admin = new User();

        admin.setDisplayId(displayIdService.generateAdminId());
        admin.setName("System Administrator");
        admin.setEmail("dileepkamineni@gmail.com");
        admin.setGender(Gender.MALE);
        admin.setRole(Role.ADMIN);
        admin.setAccountEnabled(true);

        admin.setPasswordHash(
                passwordEncoder.encode("188930"));

        userRepository.save(admin);

        System.out.println("--------------------------------");
        System.out.println("Default Admin Created");
        System.out.println("Email : dileepkamineni@gmail.com");
        System.out.println("Password : 188930");
        System.out.println("--------------------------------");

    }

}