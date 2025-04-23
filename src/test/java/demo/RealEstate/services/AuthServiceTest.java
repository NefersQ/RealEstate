package demo.RealEstate.services;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.UserNotExistException;
import demo.RealEstate.mapper.UserMapper;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.AuthResponse;
import demo.RealEstate.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private AuthService authService;

    private UserDAO testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
        testUser = new UserDAO();
        testUser.setUserId(1L);
        testUser.setUsername("test_user");
        testUser.setEmail("test@example.com");
        testUser.setPassword("hashed_password");
        testUser.setName("Test");
        testUser.setSurname("User");
        testUser.setUserCreated(LocalDateTime.now());

        testUserDTO = new UserDTO();
        testUserDTO.setUserId(1L);
        testUserDTO.setUsername("test_user");
        testUserDTO.setEmail("test@example.com");
        testUserDTO.setName("Test");
        testUserDTO.setSurname("User");
        testUserDTO.setUserCreated(LocalDateTime.now());
    }

    @Test
    void login_ShouldReturnAuthResponse_WhenCredentialsAreValid() {
        String username = "test_user";
        String password = "correct_password";
        String token = "jwt_token";

        when(userService.findByUsernameOrEmail(username, username)).thenReturn(Optional.of(testUser));
        when(bCryptPasswordEncoder.matches(password, testUser.getPassword())).thenReturn(true);
        when(jwtUtil.generateToken(testUser)).thenReturn(token);
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        AuthResponse response = authService.login(username, password);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(token);
        assertThat(response.getUser()).isEqualTo(testUserDTO);
    }

    @Test
    void login_ShouldThrowException_WhenUserDoesNotExist() {
        String username = "nonexistent_user";
        String password = "any_password";

        when(userService.findByUsernameOrEmail(username, username)).thenReturn(Optional.empty());

        assertThrows(UserNotExistException.class, () -> {
            authService.login(username, password);
        });
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        String username = "test_user";
        String password = "wrong_password";

        when(userService.findByUsernameOrEmail(username, username)).thenReturn(Optional.of(testUser));
        when(bCryptPasswordEncoder.matches(password, testUser.getPassword())).thenReturn(false);

        assertThrows(UserNotExistException.class, () -> {
            authService.login(username, password);
        });
    }
}
