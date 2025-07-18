package com.waracle.demo.cake.manager.api.security.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.Set;

public record SignUpRequest(@NotBlank String email, @NotBlank String password, Set<String> roles) {
}
