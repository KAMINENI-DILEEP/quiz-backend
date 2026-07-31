package com.exam.model;

import jakarta.persistence.*;

@Entity
@Table(name="portal_settings")
public class PortalSetting {
 @Id private Long id = 1L;
 @Column(name="registration_enabled", nullable=false) private boolean registrationEnabled = true;
 public Long getId(){return id;} public void setId(Long id){this.id=id;}
 public boolean isRegistrationEnabled(){return registrationEnabled;}
 public void setRegistrationEnabled(boolean v){registrationEnabled=v;}
}
