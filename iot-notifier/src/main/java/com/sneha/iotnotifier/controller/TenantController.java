package com.sneha.iotnotifier.controller;

import com.sneha.iotnotifier.model.Tenant;
import com.sneha.iotnotifier.repository.TenantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/tenants")
public class TenantController {

    @Autowired
    private TenantRepository tenantRepository;

    // Create a new tenant
    @PostMapping
    public Tenant createTenant(@RequestBody Tenant tenant) {
        return tenantRepository.save(tenant);
    }

    // Get all tenants
    @GetMapping
    public List<Tenant> getAllTenants() {
        return tenantRepository.findAll();
    }

    // Get tenant by ID
    @GetMapping("/{id}")
    public Optional<Tenant> getTenantById(@PathVariable Long id) {
        return tenantRepository.findById(id);
    }

    // Update tenant
    @PutMapping("/{id}")
    public Tenant updateTenant(@PathVariable Long id, @RequestBody Tenant updatedTenant) {
        return tenantRepository.findById(id).map(existingTenant -> {
            existingTenant.setName(updatedTenant.getName());
            existingTenant.setTopicPrefix(updatedTenant.getTopicPrefix());
            return tenantRepository.save(existingTenant);
        }).orElse(null);
    }

    // Delete tenant
    @DeleteMapping("/{id}")
    public void deleteTenant(@PathVariable Long id) {
        tenantRepository.deleteById(id);
    }
}
