package com.waracle.demo.cake.manager.repository.security;

import com.waracle.demo.cake.manager.models.security.CmUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CmUserRepository extends JpaRepository<CmUser, Long> {
    @Query("SELECT u FROM CmUser u JOIN FETCH u.roles WHERE u.email = :email")
    Optional<CmUser> findByEmail(String email);
    Boolean existsByEmail(String email);
}
