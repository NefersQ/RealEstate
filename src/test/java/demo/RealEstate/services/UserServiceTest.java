package demo.RealEstate.services;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.ApiException;
import demo.RealEstate.exception.UserAlreadyExistsException;
import demo.RealEstate.mapper.UserMapper;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private UserDAO testUser;
    private UserDTO testUserDTO;

    @BeforeEach
    void setUp() {
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
    void deleteUserById_ShouldCallRepository() {
        userService.deleteUserById(1L);
        verify(userRepository).deleteById(1L);
    }

    @Test
    void findUserById_ShouldReturnUser_WhenExists() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userMapper.toDTO(testUser)).thenReturn(testUserDTO);

        UserDTO result = userService.findUserById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("johndoe");
    }

    @Test
    void findUserById_ShouldThrowException_WhenNotExists() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> {
            userService.findUserById(99L);
        });
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        List<UserDAO> usersList = Arrays.asList(testUser);
        List<UserDTO> userDTOList = Arrays.asList(testUserDTO);

        when(userRepository.findAll()).thenReturn(usersList);
        when(userMapper.toDTOList(usersList)).thenReturn(userDTOList);

        List<UserDTO> result = userService.getAllUsers();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getUsername()).isEqualTo("johndoe");
    }

    @Test
    void updateUserById_ShouldUpdateUser_WhenValid() {
        UserDAO userToUpdate = new UserDAO();
        userToUpdate.setUsername("newUsername");
        userToUpdate.setEmail("new@example.com");
        userToUpdate.setName("New");
        userToUpdate.setSurname("Name");
        userToUpdate.setPassword("NewPassword123!");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("newUsername")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(userRepository.save(any(UserDAO.class))).thenReturn(testUser);

        UserDAO result = userService.updateUserById(1L, userToUpdate);

        assertThat(result).isNotNull();
        verify(userRepository).save(any(UserDAO.class));
    }

    @Test
    void updateUserById_ShouldThrowException_WhenUserNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> {
            userService.updateUserById(99L, new UserDAO());
        });
    }

    @Test
    void updateUserById_ShouldThrowException_WhenUsernameAlreadyExists() {
        UserDAO userToUpdate = new UserDAO();
        userToUpdate.setUsername("existingUser");
        userToUpdate.setEmail("new@example.com");

        UserDAO existingUser = new UserDAO();
        existingUser.setUserId(2L);
        existingUser.setUsername("existingUser");

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(userRepository.findByUsername("existingUser")).thenReturn(Optional.of(existingUser));

        assertThrows(ApiException.class, () -> {
            userService.updateUserById(1L, userToUpdate);
        });
    }

    @Test
    void registerUser_ShouldSaveUser_WhenValid() {
        when(userRepository.findByUsernameOrEmail(testUser.getUsername(), testUser.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepository.save(any(UserDAO.class))).thenReturn(testUser);

        userService.registerUser(testUser);

        verify(userRepository).save(any(UserDAO.class));
    }

    @Test
    void registerUser_ShouldThrowException_WhenUserAlreadyExists() {
        when(userRepository.findByUsernameOrEmail(testUser.getUsername(), testUser.getEmail()))
                .thenReturn(Optional.of(testUser));

        assertThrows(ApiException.class, () -> {
            userService.registerUser(testUser);
        });
    }

    @Test
    void findByUsernameOrEmail_ShouldReturnUser_WhenExists() {
        when(userRepository.findByUsernameOrEmail("johndoe", "john@example.com"))
                .thenReturn(Optional.of(testUser));

        Optional<UserDAO> result = userService.findByUsernameOrEmail("johndoe", "john@example.com");

        assertThat(result).isPresent();
        assertEquals("johndoe", result.get().getUsername());
    }
}