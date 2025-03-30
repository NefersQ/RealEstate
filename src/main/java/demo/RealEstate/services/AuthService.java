package demo.RealEstate.services;


import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.exception.UserNotActiveException;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.pswrdhashing.AuthResponse;
import demo.RealEstate.pswrdhashing.JwtUtil;
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

    public AuthResponse login(String usernameOrEmail, String password) {
        Optional<UserDAO> userOptional = userService.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
        if (userOptional.isPresent()) {
            UserDAO user = userOptional.get();
            if (!user.getIsActive()) {
                throw new UserNotActiveException("User " + usernameOrEmail + " was not found, please register.");
            }
            if (bCryptPasswordEncoder.matches(password, user.getPassword())) {
                String jwtToken = jwtUtil.generateToken(user);
                UserDTO userDTO = UserDTO.mapUserDAOToDTO(user);
                return new AuthResponse(userDTO, jwtToken);
            } else {
                throw new UserNotActiveException("Invalid username or password");
            }
        } else {
            throw new UserNotActiveException("User " + usernameOrEmail + " does not exist");
        }
    }

    public boolean isTokenValid(String token) {
        return jwtUtil.validateToken(token);
    }
}