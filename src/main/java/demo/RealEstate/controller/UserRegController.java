package demo.RealEstate.controller;


import demo.RealEstate.model.UserDAO;
import demo.RealEstate.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class  UserRegController {
    @Autowired
    private UserService userService;
    // new user creation
    @PostMapping("/api/v1/register")
    public ResponseEntity<String> userRegistration(@Valid @RequestBody UserDAO userDAO) {
        userService.registerUser(userDAO);
        return new ResponseEntity<>("User created successfully", HttpStatus.CREATED);
    }
}
