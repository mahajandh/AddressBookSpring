package com.example.AddressBookApplication.Controller;

import com.example.AddressBookApplication.dto.LoginDTO;
import com.example.AddressBookApplication.dto.UserDTO;
import com.example.AddressBookApplication.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // Register User
    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@Valid @RequestBody UserDTO userDTO) {
        String response = userService.registerUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response); // Return 201 Created
    }

    // Login User and Generate JWT Token
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@Valid @RequestBody LoginDTO loginRequest) {
        String token = userService.authenticateUser(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (token.equals("User not found!") || token.equals("Invalid email or password!")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("error", token)); // 401 Unauthorized
        }

        return ResponseEntity.ok(Map.of("message", "Login successful!", "token", token));
    }
    // Forgot Password: Update password and send email
    @PutMapping("/forgot-Password/{email}")
    public ResponseEntity<?> forgotPassword(@PathVariable String email,@Valid @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "New password cannot be empty!"));
        }
        String response = userService.forgotPassword(email, newPassword);
        return ResponseEntity.ok(Map.of("message", response));
    }

    // Reset Password   : Validate current password before updating
    @PutMapping("/reset-Password/{email}")
    public ResponseEntity<?> resetPassword(
            @PathVariable String email,
            @Valid @RequestParam String currentPassword,
            @Valid @RequestParam String newPassword) {

        String response = userService.resetPassword(email, currentPassword, newPassword);
        return ResponseEntity.ok(Map.of("message", response));
    }
}