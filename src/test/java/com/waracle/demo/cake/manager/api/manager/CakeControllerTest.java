package com.waracle.demo.cake.manager.api.manager;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.waracle.demo.cake.manager.TestContainersConfiguration;
import com.waracle.demo.cake.manager.api.manager.dto.CmCakeDto;
import com.waracle.demo.cake.manager.api.security.dto.LoginRequest;
import com.waracle.demo.cake.manager.config.DataInitialiser;
import com.waracle.demo.cake.manager.models.manager.CmCake;
import com.waracle.demo.cake.manager.repository.manager.CmCakeRepository;
import org.junit.jupiter.api.BeforeEach;
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

import java.util.List;

import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Import(TestContainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class CakeControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private CmCakeRepository cakeRepository;
    @Autowired
    private ObjectMapper objectMapper;

    private String jwtToken;

    @BeforeEach
    void setUp() throws Exception {
        // Clear the repository before each test
        cakeRepository.deleteAll();
        jwtToken = loginAndGetToken(DataInitialiser.DEFAULT_ADMIN_EMAIL, DataInitialiser.DEFAULT_ADMIN_PASSWORD);
    }

    @Test
    @DisplayName("Test to get all cakes")
    void testGetAllCakes() throws Exception {
        createTestCakes();

        mockMvc.perform(get("/cake")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Test to create a new cake")
    void testCreateCake() throws Exception {
        // verifying that the repository is empty before creating a new cake
        assertEquals(0, cakeRepository.count());

        CmCakeDto newCake = new CmCakeDto( null,"Red Velvet Cake", "Delicious red velvet cake", "redvelvet.jpg");

        // Create a new cake
        mockMvc.perform(post("/cake")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCake)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Red Velvet Cake"))
                .andExpect(jsonPath("$.description").value("Delicious red velvet cake"));

        // Verify the cake was saved
        mockMvc.perform(get("/cake")
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThan(0))));
    }

    @Test
    @DisplayName("Test updating an existing cake")
    void testUpdateCake() throws Exception {
        createTestCakes();
        CmCake existingCake = cakeRepository.findAll().getFirst();
        existingCake.setTitle("Updated Cake Name");
        existingCake.setDescription("Updated description for the cake");

        // Update the existing cake
        mockMvc.perform(put("/cake")
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(CmCakeDto.toDto(existingCake))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Cake Name"))
                .andExpect(jsonPath("$.description").value("Updated description for the cake"));
    }

    @Test
    @DisplayName("Test deleting an existing cake")
    void testDeleteCake() throws Exception {
        createTestCakes();
        CmCake existingCake = cakeRepository.findAll().getFirst();

        // Delete the existing cake
        mockMvc.perform(delete("/cake/" + existingCake.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNoContent());

        // Verify the cake was deleted
        mockMvc.perform(get("/cake/" + existingCake.getId())
                        .header("Authorization", "Bearer " + jwtToken))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Test deleting a cake with insufficient permissions")
    void testDeleteCakeWithInsufficientPermissions() throws Exception {
        createTestCakes();
        CmCake existingCake = cakeRepository.findAll().getFirst();

        // Attempt to delete the cake without admin permissions
        String userToken = loginAndGetToken(DataInitialiser.DEFAULT_USER_EMAIL, DataInitialiser.DEFAULT_USER_PASSWORD);
        mockMvc.perform(delete("/cake/" + existingCake.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    private void createTestCakes() {
        CmCake cake1 = new CmCake(null, "Chocolate Cake", "Delicious chocolate cake", "chocolate.jpg");
        CmCake cake2 = new CmCake(null, "Vanilla Cake", "Tasty vanilla cake", "vanilla.jpg");
        cakeRepository.saveAll(List.of(cake1, cake2));
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(email, password);
        String response = mockMvc.perform(post("/auth/sign-in")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("accessToken").asText();
    }
}