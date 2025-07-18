package com.waracle.demo.cake.manager.api.security.dto;

import java.util.Set;

public record JwtResponse(String accessToken, String tokenType, Long id, String email, Set<String> roles) {
}
