package demo.RealEstate.controller;

import demo.RealEstate.jwt.AuthResponse;
import demo.RealEstate.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import demo.RealEstate.request.LoginRequest;

@RestController
public class LoginController {
    @Autowired
    private AuthService authService;
    // validates requests to log in in the system and returns JWT
    @PostMapping("api/v1/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = authService.login(loginRequest.getUsernameOrEmail(), loginRequest.getPassword());
        if (authResponse != null) {
            return ResponseEntity.ok(authResponse);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username/email or password");
        }
    }
}
