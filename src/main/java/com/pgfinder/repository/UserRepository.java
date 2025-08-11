package com.pgfinder.repository;

import com.pgfinder.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    Optional<User> findByEmailAndIsActiveTrue(String email);
    
    List<User> findByUserTypeAndIsActiveTrue(User.UserType userType);
    
    @Query("SELECT u FROM User u WHERE u.userType = :userType AND u.isActive = true")
    List<User> findActiveUsersByType(@Param("userType") User.UserType userType);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.userType = :userType AND u.isActive = true")
    Long countActiveUsersByType(@Param("userType") User.UserType userType);
    
    // Email verification methods
    Optional<User> findByVerificationToken(String verificationToken);
    
    // Refresh token methods
    Optional<User> findByRefreshToken(String refreshToken);
}
