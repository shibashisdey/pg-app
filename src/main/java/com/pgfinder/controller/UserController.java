package com.pgfinder.controller;

import com.pgfinder.dto.JwtResponse;
import com.pgfinder.dto.LoginRequest;
import com.pgfinder.dto.RefreshTokenRequest;
import com.pgfinder.exception.EmailNotVerifiedException;
import com.pgfinder.exception.InvalidTokenException;
import com.pgfinder.exception.UserAlreadyExistsException;
import com.pgfinder.exception.UserNotFoundException;
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
        sanitizedUser.put("isVerified", user.isVerified());
        sanitizedUser.put("createdAt", user.getCreatedAt());
        sanitizedUser.put("updatedAt", user.getUpdatedAt());
        return sanitizedUser;
    }
    
    // ===============================
    // JWT AUTHENTICATION ENDPOINTS
    // ===============================
    
    @PostMapping("/auth/register")
    public ResponseEntity<?> registerWithEmailVerification(@Valid @RequestBody User user) {
        try {
            User registeredUser = userService.registerUserWithEmailVerification(user);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully. Please check your email to verify your account.");
            response.put("user", sanitizeUser(registeredUser));
            
            return ResponseEntity.ok(response);
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Registration failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/auth/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            JwtResponse jwtResponse = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", jwtResponse);
            
            return ResponseEntity.ok(response);
        } catch (EmailNotVerifiedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "errorCode", "EMAIL_NOT_VERIFIED"
            ));
        } catch (InvalidTokenException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", "Invalid credentials"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Authentication failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/auth/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
        try {
            JwtResponse jwtResponse = userService.refreshToken(refreshTokenRequest.getRefreshToken());
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token refreshed successfully");
            response.put("data", jwtResponse);
            
            return ResponseEntity.ok(response);
        } catch (InvalidTokenException | UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Token refresh failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/auth/logout")
    public ResponseEntity<?> logout(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.logout(email);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Logged out successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Logout failed: " + e.getMessage()
            ));
        }
    }
    
    // ===============================
    // EMAIL VERIFICATION ENDPOINTS
    // ===============================
    
    @GetMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {
        try {
            boolean verified = userService.verifyEmail(token);
            
            if (verified) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Email verified successfully. You can now login to your account."
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Email verification failed"
                ));
            }
        } catch (InvalidTokenException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Email verification failed: " + e.getMessage()
            ));
        }
    }
    
    @PostMapping("/resend-verification")
    public ResponseEntity<?> resendVerificationEmail(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            userService.resendVerificationEmail(email);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Verification email sent successfully. Please check your inbox."
            ));
        } catch (UserNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "success", false,
                "message", "Failed to send verification email: " + e.getMessage()
            ));
        }
    }
}
