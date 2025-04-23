package demo.RealEstate.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.GlobalExceptionHandler;
import demo.RealEstate.exception.UserAlreadyExistsException;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.JwtUtil;
import demo.RealEstate.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserController userController;

    private ObjectMapper objectMapper;
    private UserDAO testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        mockMvc = MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(new LocalValidatorFactoryBean())
                .build();
        testUser = new UserDAO();
        testUser.setUserId(1L);
        testUser.setName("John");
        testUser.setSurname("Doe");
        testUser.setUsername("johndoe");
        testUser.setEmail("john@example.com");
        testUser.setPassword("Password123!");
        testUser.setUserCreated(LocalDateTime.now());

        testUserDTO = new UserDTO();
        testUserDTO.setUserId(1L);
        testUserDTO.setName("John");
        testUserDTO.setSurname("Doe");
        testUserDTO.setUsername("johndoe");
        testUserDTO.setEmail("john@example.com");
        testUserDTO.setUserCreated(LocalDateTime.now());
    }

    @Test
    void getCurrentUser_ShouldReturnUser_WhenTokenIsValid() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userService.findUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/v1/users/me")
                        .header("Authorization", "Bearer validToken"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    void updateCurrentUser_ShouldReturnUpdatedUser_WhenValid() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userService.updateUserById(eq(1L), any(UserDAO.class))).thenReturn(testUser);
        when(userService.findUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    void updateCurrentUser_ShouldReturnConflict_WhenUserAlreadyExists() throws Exception {
        when(jwtUtil.getUserIdFromToken(anyString())).thenReturn(1L);
        when(userService.updateUserById(eq(1L), any(UserDAO.class)))
                .thenThrow(new UserAlreadyExistsException("Username already exists"));

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isConflict())
                .andExpect(content().string("Username already exists"));
    }

    @Test
    void findUserById_ShouldReturnUser_WhenExists() throws Exception {
        when(userService.findUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    void updateUserById_ShouldReturnUpdatedUser_WhenValid() throws Exception {
        when(userService.updateUserById(eq(1L), any(UserDAO.class))).thenReturn(testUser);
        when(userService.findUserById(1L)).thenReturn(testUserDTO);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId", is(1)))
                .andExpect(jsonPath("$.username", is("johndoe")));
    }

    @Test
    void updateUserById_ShouldReturnNotFound_WhenUserDoesNotExist() throws Exception {
        when(userService.updateUserById(eq(99L), any(UserDAO.class)))
                .thenThrow(new NoSuchElementException("User not found"));

        mockMvc.perform(put("/api/v1/users/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(testUser)))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Requested resource not found"));
    }

    @Test
    void deleteUserById_ShouldReturnNoContent_WhenDeleted() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() throws Exception {
        List<UserDTO> users = Arrays.asList(testUserDTO);
        when(userService.getAllUsers()).thenReturn(users);

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].userId", is(1)))
                .andExpect(jsonPath("$[0].username", is("johndoe")));
    }

    @Test
    void getAllUsers_ShouldReturnNoContent_WhenNoUsers() throws Exception {
        when(userService.getAllUsers()).thenReturn(Arrays.asList());

        mockMvc.perform(get("/api/v1/users"))
                .andExpect(status().isNoContent());
    }
    @Test
    void updateCurrentUser_ShouldReturnMultipleValidationErrors_WhenMultipleInvalidFieldsProvided() throws Exception {
        UserDAO invalidUser = new UserDAO();
        invalidUser.setName("");
        invalidUser.setSurname("");
        invalidUser.setUsername("usr");
        invalidUser.setEmail("invalid-email");
        invalidUser.setPassword("weak");
        invalidUser.setUserCreated(LocalDateTime.now());

        mockMvc.perform(put("/api/v1/users/me")
                        .header("Authorization", "Bearer validToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.name").exists())
                .andExpect(jsonPath("$.surname").exists())
                .andExpect(jsonPath("$.username").exists())
                .andExpect(jsonPath("$.email").exists())
                .andExpect(jsonPath("$.password").exists());
    }
}