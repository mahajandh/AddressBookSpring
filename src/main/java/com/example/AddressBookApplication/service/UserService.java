package com.example.AddressBookApplication.service;

import com.example.AddressBookApplication.dto.UserDTO;
import com.example.AddressBookApplication.Model.User;
import com.example.AddressBookApplication.repository.UserRepository;
import com.example.AddressBookApplication.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService implements IUserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private JwtUtil jwtUtil;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    // Register User
    @Override
    public String registerUser(UserDTO userdto) {
        if (userRepository.existsByEmail(userdto.getEmail())) {
            return "Email is already in use!";
        }

        User user = new User();
        user.setUsername(userdto.getName());
        user.setEmail(userdto.getEmail());
        user.setPassword(passwordEncoder.encode(userdto.getPassword())); // Encrypt password
        String subject = "Welcome to Our Platform!";
        String body = "<h1>Hello " + userdto.getName() + "!</h1>"
                + "<p>Thank you for registering on our platform.</p>"
                + "<p>We are excited to have you on board.</p>";

        emailService.sendEmail(user.getEmail(), subject, body);

        userRepository.save(user);
        return "User registered successfully!";
    }

    // Authenticate User and Generate Token
    @Override
    public String authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return "User not found!";
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(password, user.getPassword())) {
            return "Invalid email or password!";
        }

        // Generate JWT Token using HMAC256
        return jwtUtil.generateToken(email);
    }
    // Forgot Password Implementation
    @Override
    public String forgotPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "Sorry! We cannot find the user email: " + email;
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        String subject = "Password Change Notification";
        String content = "<h2>Hello " + user.getUsername() + ",</h2>"
                + "<p>Your password has been changed successfully.</p>"
                + "<br><p>Regards,</p><p><strong>AddressBookApp Team</strong></p>";

        emailService.sendEmail(user.getEmail(), subject, content);

        return "Password has been changed successfully!";
    }

    // Reset Password Implementation
    @Override
    public String resetPassword(String email, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return "User not found with email: " + email;
        }
        User user = userOpt.get();

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return "Current password is incorrect!";
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        String subject = "Password Reset Notification";
        String content = "<h2>Hello " + user.getUsername() + ",</h2>"
                + "<p>Your password has been reset successfully.</p>"
                + "<br><p>Regards,</p><p><strong>AddressBookApp Team</strong></p>";

        emailService.sendEmail(user.getEmail(), subject, content);

        return "Password reset successfully!";
    }
}