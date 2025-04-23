package demo.RealEstate.integreationTests;

import com.fasterxml.jackson.databind.ObjectMapper;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.AuthResponse;
import demo.RealEstate.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import demo.RealEstate.request.LoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserIntegrationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private UserDAO testUser;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        testUser = new UserDAO();
        testUser.setName("Integration");
        testUser.setSurname("Test");
        testUser.setUsername("integrationtest");
        testUser.setEmail("integration@test.com");
        testUser.setPassword("Password123!");

        userRepository.deleteAll();

        mockMvc.perform(post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isCreated());

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail(testUser.getUsername());
        loginRequest.setPassword(testUser.getPassword());

        MvcResult result = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        AuthResponse response = objectMapper.readValue(content, AuthResponse.class);
        authToken = response.getToken();
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void getUserProfile_ShouldReturnUserData() throws Exception {
        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer " + authToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(testUser.getUsername()))
                .andExpect(jsonPath("$.email").value(testUser.getEmail()));
    }

    @Test
    void updateUserProfile_ShouldUpdateUserData() throws Exception {
        UserDAO updatedUser = new UserDAO();
        updatedUser.setName("Updated");
        updatedUser.setSurname("User");
        updatedUser.setUsername("updateduser");
        updatedUser.setEmail("updated@test.com");
        updatedUser.setPassword("UpdatedPassword123!");

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer " + authToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(updatedUser.getUsername()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()));
    }
}