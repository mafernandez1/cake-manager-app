package com.waracle.demo.cake.manager.repository.security;

import com.waracle.demo.cake.manager.models.security.CmRole;
import com.waracle.demo.cake.manager.models.security.CmRoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CmRoleRepository extends JpaRepository<CmRole, Long> {

    Optional<CmRole> findByName(CmRoleType name);
}
