package demo.RealEstate.services;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.ApiException;
import demo.RealEstate.mapper.UserMapper;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.config.PasswordEncoderUtil;
import demo.RealEstate.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper userMapper;
    //delete user by id
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
    //updates user by id
    public UserDAO updateUserById(Long id, UserDAO user) {
        Optional<UserDAO> optionalExisting = userRepository.findById(id);
        if (optionalExisting.isEmpty()) {
            throw new ApiException("User not found with id: " + id, HttpStatus.NOT_FOUND);
        }
        UserDAO existing = optionalExisting.get();

        if (!existing.getUsername().equals(user.getUsername())) {
            if (userRepository.findByUsername(user.getUsername()).isPresent()) {
                throw new ApiException("Username already exists", HttpStatus.CONFLICT);
            }
            existing.setUsername(user.getUsername());
        }

        if (!existing.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent()) {
                throw new ApiException("Email already exists", HttpStatus.CONFLICT);
            }
            existing.setEmail(user.getEmail());
        }

        existing.setName(user.getName());
        existing.setSurname(user.getSurname());

        if (user.getPassword() != null && !user.getPassword().isBlank()) {
            existing.setPassword(PasswordEncoderUtil.encodePassword(user.getPassword()));
        }

        return userRepository.save(existing);
    }
    //returns all users
    public List<UserDTO> getAllUsers() {
        List<UserDAO> users = userRepository.findAll();
        return userMapper.toDTOList(users);
    }
    //returns user by id
    public UserDTO findUserById(Long id) {
        UserDAO user = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("User not found with id: " + id, HttpStatus.NOT_FOUND));
        return userMapper.toDTO(user);
    }
    //used for login
    public Optional<UserDAO> findByUsernameOrEmail(String username, String email) {
        return userRepository.findByUsernameOrEmail(username, email);
    }
    //registers users in the system
    public void registerUser(UserDAO userDAO) {
        userDAO.setPassword(PasswordEncoderUtil.encodePassword(userDAO.getPassword()));
        if (userRepository.findByUsernameOrEmail(userDAO.getUsername(), userDAO.getEmail()).isPresent()) {
            throw new ApiException("Username or email already exists", HttpStatus.CONFLICT);
        }
        userRepository.save(userDAO);
    }
}