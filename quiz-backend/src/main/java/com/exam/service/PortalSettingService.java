package com.exam.service;
import com.exam.model.PortalSetting;
import com.exam.repository.PortalSettingRepository;
import org.springframework.stereotype.Service;
@Service
public class PortalSettingService {
 private final PortalSettingRepository repo;
 public PortalSettingService(PortalSettingRepository repo){this.repo=repo;}
 public synchronized PortalSetting get(){return repo.findById(1L).orElseGet(()->{ PortalSetting s=new PortalSetting(); s.setId(1L); s.setRegistrationEnabled(true); return repo.save(s);});}
 public boolean enabled(){return get().isRegistrationEnabled();}
 public synchronized PortalSetting set(boolean enabled){PortalSetting s=get(); s.setRegistrationEnabled(enabled); return repo.save(s);}
}
