package com.pgfinder.service;

import com.pgfinder.dto.JwtResponse;
import com.pgfinder.exception.EmailNotVerifiedException;
import com.pgfinder.exception.InvalidTokenException;
import com.pgfinder.exception.UserAlreadyExistsException;
import com.pgfinder.exception.UserNotFoundException;
import com.pgfinder.model.User;
import com.pgfinder.repository.UserRepository;
import com.pgfinder.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private EmailService emailService;
    
    @Value("${app.verification.timeout:180000}") // 3 minutes default
    private Long verificationTimeoutMs;
    
    public User registerUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        
        return userRepository.save(user);
    }
    
    public Optional<User> loginUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        
        if (userOpt.isPresent() && passwordEncoder.matches(password, userOpt.get().getPassword())) {
            return userOpt;
        }
        
        return Optional.empty();
    }
    
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }
    
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    public User updateUser(User user) {
        return userRepository.save(user);
    }
    
    public void deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            userRepository.save(user);
        }
    }
    
    public List<User> findUsersByType(User.UserType userType) {
        return userRepository.findByUserTypeAndIsActiveTrue(userType);
    }
    
    public Long countUsersByType(User.UserType userType) {
        return userRepository.countActiveUsersByType(userType);
    }
    
    public List<User> getAllActiveUsers() {
        return userRepository.findActiveUsersByType(null);
    }
    
    public boolean changePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepository.save(user);
                return true;
            }
        }
        
        return false;
    }
    
    public User updateProfile(Long userId, String name, String phoneNumber) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setName(name);
            user.setPhoneNumber(phoneNumber);
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found");
    }
    
    // ===============================
    // EMAIL VERIFICATION METHODS
    // ===============================
    
    public User registerUserWithEmailVerification(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("Email already exists");
        }
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setIsActive(true);
        user.setVerified(false);
        
        // Generate verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        user.setLastVerificationSent(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Send verification email
        try {
            emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getName());
        } catch (Exception e) {
            // Log error but don't fail registration
            System.err.println("Failed to send verification email: " + e.getMessage());
        }
        
        return savedUser;
    }
    
    public boolean verifyEmail(String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        
        if (userOpt.isEmpty()) {
            throw new InvalidTokenException("Invalid verification token");
        }
        
        User user = userOpt.get();
        
        if (user.getVerificationTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Verification token has expired");
        }
        
        user.setVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);
        userRepository.save(user);
        
        return true;
    }
    
    public void resendVerificationEmail(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        
        User user = userOpt.get();
        
        if (user.isVerified()) {
            throw new IllegalArgumentException("Email is already verified");
        }
        
        // Check if we need to wait before sending another verification email
        if (user.getLastVerificationSent() != null) {
            LocalDateTime canResendAfter = user.getLastVerificationSent().plusSeconds(verificationTimeoutMs / 1000);
            if (LocalDateTime.now().isBefore(canResendAfter)) {
                throw new IllegalArgumentException("Please wait before requesting another verification email");
            }
        }
        
        // Generate new verification token
        String verificationToken = UUID.randomUUID().toString();
        user.setVerificationToken(verificationToken);
        user.setVerificationTokenExpiry(LocalDateTime.now().plusHours(24));
        user.setLastVerificationSent(LocalDateTime.now());
        
        userRepository.save(user);
        
        // Send verification email
        emailService.sendVerificationEmail(user.getEmail(), verificationToken, user.getName());
    }
    
    // ===============================
    // JWT AUTHENTICATION METHODS
    // ===============================
    
    public JwtResponse authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(email);
        
        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPassword())) {
            throw new InvalidTokenException("Invalid credentials");
        }
        
        User user = userOpt.get();
        
        // Check if email is verified
        if (!user.isVerified()) {
            throw new EmailNotVerifiedException("Please verify your email before logging in");
        }
        
        // Generate tokens
        String accessToken = jwtUtil.generateToken(user.getEmail(), user.getUserType().toString(), user.getId());
        String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getUserType().toString(), user.getId());
        
        // Save refresh token to database
        user.setRefreshToken(refreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7)); // 7 days
        userRepository.save(user);
        
        return new JwtResponse(accessToken, refreshToken, user.getEmail(), user.getName(), 
                             user.getUserType().toString(), user.getId(), user.isVerified());
    }
    
    public JwtResponse refreshToken(String refreshToken) {
        // Validate refresh token format
        if (!jwtUtil.isRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        
        String userEmail = jwtUtil.extractUsername(refreshToken);
        Optional<User> userOpt = userRepository.findByEmailAndIsActiveTrue(userEmail);
        
        if (userOpt.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }
        
        User user = userOpt.get();
        
        // Check if refresh token matches and is not expired
        if (!refreshToken.equals(user.getRefreshToken()) || 
            user.getRefreshTokenExpiry().isBefore(LocalDateTime.now())) {
            throw new InvalidTokenException("Invalid or expired refresh token");
        }
        
        // Generate new tokens
        String newAccessToken = jwtUtil.generateToken(user.getEmail(), user.getUserType().toString(), user.getId());
        String newRefreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getUserType().toString(), user.getId());
        
        // Update refresh token in database
        user.setRefreshToken(newRefreshToken);
        user.setRefreshTokenExpiry(LocalDateTime.now().plusDays(7));
        userRepository.save(user);
        
        return new JwtResponse(newAccessToken, newRefreshToken, user.getEmail(), user.getName(),
                             user.getUserType().toString(), user.getId(), user.isVerified());
    }
    
    public void logout(String userEmail) {
        Optional<User> userOpt = userRepository.findByEmail(userEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setRefreshToken(null);
            user.setRefreshTokenExpiry(null);
            userRepository.save(user);
        }
    }
    
    // ===============================
    // ADMIN METHODS
    // ===============================
    
    public void createHardcodedAdmin() {
        String adminEmail = "admin@pgfinder.com";
        if (!userRepository.existsByEmail(adminEmail)) {
            User admin = new User();
            admin.setName("System Administrator");
            admin.setEmail(adminEmail);
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setPhoneNumber("+919876543210");
            admin.setUserType(User.UserType.ADMIN);
            admin.setIsActive(true);
            admin.setVerified(true); // Admin doesn't need verification
            userRepository.save(admin);
        }
    }
}
