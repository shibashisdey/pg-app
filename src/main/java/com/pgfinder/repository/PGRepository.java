package com.pgfinder.repository;

import com.pgfinder.model.PG;
import com.pgfinder.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface PGRepository extends JpaRepository<PG, Long> {
    
    // Find active PGs
    List<PG> findByIsActiveTrueOrderByCreatedAtDesc();
    
    Page<PG> findByIsActiveTrueOrderByCreatedAtDesc(Pageable pageable);
    
    // Find by city
    Page<PG> findByCityIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(String city, Pageable pageable);
    
    // Find by owner
    List<PG> findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(User owner);
    
    Page<PG> findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(User owner, Pageable pageable);
    
    // Search by name or city
    @Query("SELECT p FROM PG p WHERE (LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.city) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
           "OR LOWER(p.address) LIKE LOWER(CONCAT('%', :keyword, '%'))) " +
           "AND p.isActive = true ORDER BY p.createdAt DESC")
    Page<PG> searchPGs(@Param("keyword") String keyword, Pageable pageable);
    
    // Filter by rent range
    @Query("SELECT p FROM PG p WHERE p.rent BETWEEN :minRent AND :maxRent " +
           "AND p.isActive = true ORDER BY p.rent ASC")
    Page<PG> findByRentBetweenAndIsActiveTrueOrderByRentAsc(
        @Param("minRent") BigDecimal minRent, 
        @Param("maxRent") BigDecimal maxRent, 
        Pageable pageable);
    
    // Filter by PG type and gender preference
    @Query("SELECT p FROM PG p WHERE (:pgType IS NULL OR p.pgType = :pgType) " +
           "AND (:genderPreference IS NULL OR p.genderPreference = :genderPreference) " +
           "AND p.isActive = true ORDER BY p.createdAt DESC")
    Page<PG> findByTypeAndGenderPreference(
        @Param("pgType") PG.PGType pgType, 
        @Param("genderPreference") PG.GenderPreference genderPreference, 
        Pageable pageable);
    
    // Complex search with filters
    @Query("SELECT p FROM PG p WHERE " +
           "(:city IS NULL OR LOWER(p.city) = LOWER(:city)) " +
           "AND (:minRent IS NULL OR p.rent >= :minRent) " +
           "AND (:maxRent IS NULL OR p.rent <= :maxRent) " +
           "AND (:pgType IS NULL OR p.pgType = :pgType) " +
           "AND (:genderPreference IS NULL OR p.genderPreference = :genderPreference) " +
           "AND (:wifiRequired = false OR p.wifiAvailable = true) " +
           "AND (:acRequired = false OR p.acAvailable = true) " +
           "AND (:parkingRequired = false OR p.parkingAvailable = true) " +
           "AND p.availableRooms > 0 " +
           "AND p.isActive = true " +
           "ORDER BY p.rating DESC, p.createdAt DESC")
    Page<PG> searchWithFilters(
        @Param("city") String city,
        @Param("minRent") BigDecimal minRent,
        @Param("maxRent") BigDecimal maxRent,
        @Param("pgType") PG.PGType pgType,
        @Param("genderPreference") PG.GenderPreference genderPreference,
        @Param("wifiRequired") Boolean wifiRequired,
        @Param("acRequired") Boolean acRequired,
        @Param("parkingRequired") Boolean parkingRequired,
        Pageable pageable);
    
    // Find by availability
    @Query("SELECT p FROM PG p WHERE p.availableRooms > 0 AND p.isActive = true " +
           "ORDER BY p.rating DESC, p.createdAt DESC")
    Page<PG> findAvailablePGs(Pageable pageable);
    
    // Find top rated PGs
    @Query("SELECT p FROM PG p WHERE p.rating >= :minRating AND p.totalReviews >= :minReviews " +
           "AND p.isActive = true ORDER BY p.rating DESC, p.totalReviews DESC")
    Page<PG> findTopRatedPGs(@Param("minRating") BigDecimal minRating, 
                             @Param("minReviews") Integer minReviews, 
                             Pageable pageable);
    
    // Find verified PGs
    Page<PG> findByIsVerifiedTrueAndIsActiveTrueOrderByRatingDescCreatedAtDesc(Pageable pageable);
    
    // Get distinct cities
    @Query("SELECT DISTINCT p.city FROM PG p WHERE p.isActive = true ORDER BY p.city")
    List<String> findDistinctCities();
    
    // Count PGs by owner
    Long countByOwnerAndIsActiveTrue(User owner);
}
