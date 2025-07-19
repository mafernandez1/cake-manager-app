package com.waracle.demo.cake.manager.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Set;

@Schema(description = "JWT authentication response containing the access token and user details")
public record JwtResponse(
        @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        String accessToken,

        @Schema(description = "Type of the token", example = "Bearer")
        String tokenType,

        @Schema(description = "User ID", example = "1")
        Long id,

        @Schema(description = "User email address", example = "user@example.com")
        String email,

        @Schema(description = "Set of user roles", example = "[\"ROLE_USER\"]")
        Set<String> roles
) {}
