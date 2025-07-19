package com.waracle.demo.cake.manager.api.security.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Generic message response")
public record MessageResponse(
        @Schema(description = "Response message", example = "User registered successfully")
        String message
) {}