package com.pgfinder.repository;

import com.pgfinder.model.PG;
import com.pgfinder.model.Review;
import com.pgfinder.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find reviews by PG
    List<Review> findByPgOrderByCreatedAtDesc(PG pg);
    
    Page<Review> findByPgOrderByCreatedAtDesc(PG pg, Pageable pageable);
    
    // Find reviews by user
    List<Review> findByUserOrderByCreatedAtDesc(User user);
    
    Page<Review> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Check if user has already reviewed a PG
    Optional<Review> findByPgAndUser(PG pg, User user);
    
    boolean existsByPgAndUser(PG pg, User user);
    
    // Get average rating for a PG
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.pg = :pg")
    BigDecimal getAverageRatingByPg(@Param("pg") PG pg);
    
    // Count reviews by PG
    Long countByPg(PG pg);
    
    // Find reviews by rating
    List<Review> findByPgAndRatingOrderByCreatedAtDesc(PG pg, Integer rating);
    
    // Find reviews by rating range
    @Query("SELECT r FROM Review r WHERE r.pg = :pg AND r.rating BETWEEN :minRating AND :maxRating " +
           "ORDER BY r.createdAt DESC")
    List<Review> findByPgAndRatingBetweenOrderByCreatedAtDesc(
        @Param("pg") PG pg, 
        @Param("minRating") Integer minRating, 
        @Param("maxRating") Integer maxRating);
}
