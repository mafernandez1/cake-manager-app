package com.waracle.demo.cake.manager.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

import java.util.Set;

@Schema(description = "Sign up request containing user registration data")
public record SignUpRequest(
        @Schema(description = "User email address", example = "user@example.com")
        @NotBlank String email,

        @Schema(description = "User password", example = "P@ssw0rd!")
        @NotBlank String password,

        @Schema(description = "Set of roles to assign to the user", example = "[\"USER\"]")
        Set<String> roles
) {}