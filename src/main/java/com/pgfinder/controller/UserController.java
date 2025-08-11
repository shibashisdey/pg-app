package com.pgfinder.controller;

import com.pgfinder.model.User;
import com.pgfinder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@Valid @RequestBody User user) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User registeredUser = userService.registerUser(user);
            response.put("success", true);
            response.put("message", "User registered successfully");
            response.put("user", sanitizeUser(registeredUser));
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Registration failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> loginUser(@RequestBody Map<String, String> loginRequest) {
        Map<String, Object> response = new HashMap<>();
        
        String email = loginRequest.get("email");
        String password = loginRequest.get("password");
        
        Optional<User> userOpt = userService.loginUser(email, password);
        
        if (userOpt.isPresent()) {
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("user", sanitizeUser(userOpt.get()));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getUserById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> userOpt = userService.findById(id);
        
        if (userOpt.isPresent()) {
            response.put("success", true);
            response.put("user", sanitizeUser(userOpt.get()));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "User not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateUser(
            @PathVariable Long id, 
            @Valid @RequestBody User userUpdate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<User> userOpt = userService.findById(id);
            
            if (userOpt.isPresent()) {
                User existingUser = userOpt.get();
                existingUser.setName(userUpdate.getName());
                existingUser.setPhoneNumber(userUpdate.getPhoneNumber());
                
                User updatedUser = userService.updateUser(existingUser);
                response.put("success", true);
                response.put("message", "User updated successfully");
                response.put("user", sanitizeUser(updatedUser));
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "User not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Update failed");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Map<String, Object>> changePassword(
            @PathVariable Long id, 
            @RequestBody Map<String, String> passwordRequest) {
        Map<String, Object> response = new HashMap<>();
        
        String currentPassword = passwordRequest.get("currentPassword");
        String newPassword = passwordRequest.get("newPassword");
        
        boolean success = userService.changePassword(id, currentPassword, newPassword);
        
        if (success) {
            response.put("success", true);
            response.put("message", "Password changed successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Invalid current password");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deactivateUser(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        userService.deactivateUser(id);
        response.put("success", true);
        response.put("message", "User deactivated successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/type/{userType}")
    public ResponseEntity<Map<String, Object>> getUsersByType(@PathVariable String userType) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            User.UserType type = User.UserType.valueOf(userType.toUpperCase());
            List<User> users = userService.findUsersByType(type);
            
            response.put("success", true);
            response.put("users", users.stream().map(this::sanitizeUser).toList());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", "Invalid user type");
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> response = new HashMap<>();
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalUsers", userService.countUsersByType(User.UserType.USER));
        stats.put("totalOwners", userService.countUsersByType(User.UserType.OWNER));
        stats.put("totalAdmins", userService.countUsersByType(User.UserType.ADMIN));
        
        response.put("success", true);
        response.put("stats", stats);
        return ResponseEntity.ok(response);
    }
    
    // Helper method to remove password from response
    private Map<String, Object> sanitizeUser(User user) {
        Map<String, Object> sanitizedUser = new HashMap<>();
        sanitizedUser.put("id", user.getId());
        sanitizedUser.put("name", user.getName());
        sanitizedUser.put("email", user.getEmail());
        sanitizedUser.put("phoneNumber", user.getPhoneNumber());
        sanitizedUser.put("userType", user.getUserType());
        sanitizedUser.put("isActive", user.getIsActive());
        sanitizedUser.put("createdAt", user.getCreatedAt());
        sanitizedUser.put("updatedAt", user.getUpdatedAt());
        return sanitizedUser;
    }
}
