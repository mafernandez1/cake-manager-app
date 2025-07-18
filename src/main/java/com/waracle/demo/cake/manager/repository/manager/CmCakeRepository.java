package com.waracle.demo.cake.manager.repository.manager;

import com.waracle.demo.cake.manager.models.manager.CmCake;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmCakeRepository extends JpaRepository<CmCake, Long> {
}
