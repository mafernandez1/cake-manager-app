package com.waracle.demo.cake.manager.api.security.services;

import com.waracle.demo.cake.manager.models.security.CmRole;
import com.waracle.demo.cake.manager.models.security.CmRoleType;
import com.waracle.demo.cake.manager.repository.security.CmRoleRepository;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class CmRoleService {
    private final CmRoleRepository roleRepository;

    public CmRoleService(CmRoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    public Optional<CmRole> getRoleByName(String roleName) {
        CmRoleType roleType;
        switch (roleName.toLowerCase(Locale.ROOT)) {
            case "admin":
                roleType = CmRoleType.ADMIN;
                break;
            case "user":
                roleType = CmRoleType.USER;
                break;
            default:
                return Optional.empty();
        }
        return roleRepository.findByName(roleType);
    }
}
