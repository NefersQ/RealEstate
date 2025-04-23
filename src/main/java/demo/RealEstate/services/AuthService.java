package demo.RealEstate.services;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.UserNotExistException;
import demo.RealEstate.mapper.UserMapper;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.AuthResponse;
import demo.RealEstate.jwt.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserMapper userMapper;
    //method to login client to the system
    public AuthResponse login(String usernameOrEmail, String password) {
        Optional<UserDAO> userOptional = userService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isPresent()) {
            UserDAO user = userOptional.get();
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                String jwtToken = jwtUtil.generateToken(user);
                UserDTO userDTO = userMapper.toDTO(user);
                return new AuthResponse(userDTO, jwtToken);
            } else {
                throw new UserNotExistException("Invalid username or password");
            }
        } else {
            throw new UserNotExistException("User " + usernameOrEmail + " does not exist");
        }
    }
}