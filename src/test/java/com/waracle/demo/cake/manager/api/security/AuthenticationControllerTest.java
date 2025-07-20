package com.waracle.demo.cake.manager.api.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waracle.demo.cake.manager.TestContainersConfiguration;
import com.waracle.demo.cake.manager.api.security.dto.LoginRequest;
import com.waracle.demo.cake.manager.api.security.dto.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("Test user registration and login")
    void testUserRegistrationAndLogin() throws Exception {
        // Test user registration
        SignUpRequest signupRequest = new SignUpRequest("test@example.com", "password123", Set.of());

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User registered successfully!"));

        // Test user login
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password123");

        String response = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andReturn().getResponse().getContentAsString();

        // Extract token for authenticated requests
        com.fasterxml.jackson.databind.JsonNode jsonNode = objectMapper.readTree(response);
        String token = jsonNode.get("accessToken").asText();

        // Test authenticated endpoint
        mockMvc.perform(get("/cake")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Test user registration with existing email")
    void testDuplicateUserRegistration() throws Exception {
        // Register first user
        SignUpRequest signupRequest = new SignUpRequest("duplicate@example.com", "password123", Set.of());

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Try to register user with same username
        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: Email is already in use!"));
    }

    @Test
    @DisplayName("Test user registration with invalid role")
    void testUserRegistrationWithInvalidRole() throws Exception {
        // Register first user
        SignUpRequest signupRequest = new SignUpRequest("duplicate@example.com", "password123", Set.of("invalid_role"));

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error: 'invalid_role' not found."));;
    }

    @Test
    @DisplayName("Test unauthorized access to protected endpoint")
    void testUnauthorizedAccess() throws Exception {
        // Test accessing protected endpoint without token
        mockMvc.perform(get("/api/cake"))
                .andExpect(status().isUnauthorized());
    }
}