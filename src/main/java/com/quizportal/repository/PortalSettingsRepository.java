package com.quizportal.repository;

import com.quizportal.entity.PortalSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PortalSettingsRepository
        extends JpaRepository<PortalSettings, Integer> {
}