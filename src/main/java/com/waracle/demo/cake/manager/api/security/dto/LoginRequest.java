package com.waracle.demo.cake.manager.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Login request containing user credentials")
public record LoginRequest(
        @Schema(description = "User email address", example = "user@example.com")
        @NotBlank String email,

        @Schema(description = "User password", example = "P@ssw0rd!")
        @NotBlank String password
) {}
