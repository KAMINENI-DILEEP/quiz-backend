package com.quizportal.service.impl;

import com.quizportal.entity.PortalSettings;
import com.quizportal.repository.PortalSettingsRepository;
import com.quizportal.service.PortalSettingsService;
import org.springframework.stereotype.Service;

@Service
public class PortalSettingsServiceImpl implements PortalSettingsService {

    private final PortalSettingsRepository repository;

    public PortalSettingsServiceImpl(
            PortalSettingsRepository repository) {

        this.repository = repository;
    }

    @Override
    public PortalSettings getSettings() {

        return repository.findById(1)
                .orElseGet(() -> {

                    PortalSettings settings = new PortalSettings();

                    settings.setId(1);

                    return repository.save(settings);

                });

    }

    @Override
    public PortalSettings save(PortalSettings settings) {

        return repository.save(settings);

    }

    @Override
    public boolean isRegistrationEnabled() {

        return getSettings().getRegistrationEnabled();

    }

}