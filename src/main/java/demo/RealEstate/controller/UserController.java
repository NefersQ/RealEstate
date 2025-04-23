package demo.RealEstate.controller;

import demo.RealEstate.dto.UserDTO;
import demo.RealEstate.model.UserDAO;
import demo.RealEstate.jwt.JwtUtil;
import demo.RealEstate.services.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  @Autowired
  private UserService userService;

  @Autowired
  private JwtUtil jwtUtil;
  // returns information about current user
  @GetMapping("/me")
  public ResponseEntity<UserDTO> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.replace("Bearer ", "");
    Long userId = jwtUtil.getUserIdFromToken(token);
    UserDTO dto = userService.findUserById(userId);
    return ResponseEntity.ok(dto);
  }
  //updates current user information
  @PutMapping("/me")
  public ResponseEntity<UserDTO> updateCurrentUser(@RequestHeader("Authorization") String authHeader,
                                                   @Valid @RequestBody UserDAO user) {
    String token = authHeader.replace("Bearer ", "");
    Long userId = jwtUtil.getUserIdFromToken(token);
    userService.updateUserById(userId, user);
    UserDTO updated = userService.findUserById(userId);
    return ResponseEntity.ok(updated);
  }
  //for admin: returns information about certain user by his id
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public ResponseEntity<UserDTO> findUserById(@PathVariable Long id) {
    UserDTO userDTO = userService.findUserById(id);
    return ResponseEntity.ok(userDTO);
  }

  //for admin: update user by id
  @PreAuthorize("hasRole('ADMIN')")
  @PutMapping("/{id}")
  public ResponseEntity<UserDTO> updateUserById(@PathVariable Long id, @Valid @RequestBody UserDAO user) {
    userService.updateUserById(id, user);
    UserDTO updated = userService.findUserById(id);
    return ResponseEntity.ok(updated);
  }
  // for admin: delete user by ID
  @PreAuthorize("hasRole('ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
    userService.deleteUserById(id);
    return ResponseEntity.noContent().build();
  }
  //for admin: get all existing users
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping
  public ResponseEntity<List<UserDTO>> getAllUsers() {
    List<UserDTO> users = userService.getAllUsers();
    if (users.isEmpty()) {
      return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
  }
}
