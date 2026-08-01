package com.quizportal.service;

import com.quizportal.entity.PortalSettings;

public interface PortalSettingsService {

    PortalSettings getSettings();

    PortalSettings save(PortalSettings settings);

    boolean isRegistrationEnabled();

}