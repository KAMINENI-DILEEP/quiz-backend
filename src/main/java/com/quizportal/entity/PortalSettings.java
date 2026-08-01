package com.quizportal.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "portal_settings")
public class PortalSettings {

    @Id
    private Integer id;

    @Column(name = "registration_enabled")
    private Boolean registrationEnabled = true;

    @Column(name = "maintenance_mode")
    private Boolean maintenanceMode = false;

    @Column(name = "dark_theme_default")
    private Boolean darkThemeDefault = true;

    @Column(name = "auto_refresh_seconds")
    private Integer autoRefreshSeconds = 5;

    @Column(name = "max_users")
    private Integer maxUsers = 1000;

    public PortalSettings() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getRegistrationEnabled() {
        return registrationEnabled;
    }

    public void setRegistrationEnabled(Boolean registrationEnabled) {
        this.registrationEnabled = registrationEnabled;
    }

    public Boolean getMaintenanceMode() {
        return maintenanceMode;
    }

    public void setMaintenanceMode(Boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public Boolean getDarkThemeDefault() {
        return darkThemeDefault;
    }

    public void setDarkThemeDefault(Boolean darkThemeDefault) {
        this.darkThemeDefault = darkThemeDefault;
    }

    public Integer getAutoRefreshSeconds() {
        return autoRefreshSeconds;
    }

    public void setAutoRefreshSeconds(Integer autoRefreshSeconds) {
        this.autoRefreshSeconds = autoRefreshSeconds;
    }

    public Integer getMaxUsers() {
        return maxUsers;
    }

    public void setMaxUsers(Integer maxUsers) {
        this.maxUsers = maxUsers;
    }
}