package com.vincent.aiplatform.service;

import com.vincent.aiplatform.entity.Tenant;
import com.vincent.aiplatform.repository.TenantRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TenantService {

    private final TenantRepository tenantRepository;

    public TenantService(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    public Tenant createTenant(String name) {

        Tenant tenant = new Tenant();
        tenant.setName(name);

        return tenantRepository.save(tenant);
    }

    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }
}
