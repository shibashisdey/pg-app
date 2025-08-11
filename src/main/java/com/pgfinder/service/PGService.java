package com.pgfinder.service;

import com.pgfinder.model.PG;
import com.pgfinder.model.Review;
import com.pgfinder.model.User;
import com.pgfinder.repository.PGRepository;
import com.pgfinder.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PGService {
    
    @Autowired
    private PGRepository pgRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    public PG createPG(PG pg) {
        pg.setIsActive(true);
        pg.setIsVerified(false);
        return pgRepository.save(pg);
    }
    
    public Optional<PG> findById(Long id) {
        return pgRepository.findById(id);
    }
    
    public PG updatePG(PG pg) {
        return pgRepository.save(pg);
    }
    
    public void deletePG(Long id) {
        Optional<PG> pgOpt = pgRepository.findById(id);
        if (pgOpt.isPresent()) {
            PG pg = pgOpt.get();
            pg.setIsActive(false);
            pgRepository.save(pg);
        }
    }
    
    public Page<PG> getAllActivePGs(Pageable pageable) {
        return pgRepository.findByIsActiveTrueOrderByCreatedAtDesc(pageable);
    }
    
    public Page<PG> getPGsByCity(String city, Pageable pageable) {
        return pgRepository.findByCityIgnoreCaseAndIsActiveTrueOrderByCreatedAtDesc(city, pageable);
    }
    
    public List<PG> getPGsByOwner(User owner) {
        return pgRepository.findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(owner);
    }
    
    public Page<PG> getPGsByOwner(User owner, Pageable pageable) {
        return pgRepository.findByOwnerAndIsActiveTrueOrderByCreatedAtDesc(owner, pageable);
    }
    
    public Page<PG> searchPGs(String keyword, Pageable pageable) {
        return pgRepository.searchPGs(keyword, pageable);
    }
    
    public Page<PG> filterPGsByRentRange(BigDecimal minRent, BigDecimal maxRent, Pageable pageable) {
        return pgRepository.findByRentBetweenAndIsActiveTrueOrderByRentAsc(minRent, maxRent, pageable);
    }
    
    public Page<PG> filterPGsByTypeAndGender(PG.PGType pgType, PG.GenderPreference genderPreference, Pageable pageable) {
        return pgRepository.findByTypeAndGenderPreference(pgType, genderPreference, pageable);
    }
    
    public Page<PG> searchWithFilters(String city, BigDecimal minRent, BigDecimal maxRent, 
                                     PG.PGType pgType, PG.GenderPreference genderPreference,
                                     Boolean wifiRequired, Boolean acRequired, Boolean parkingRequired,
                                     Pageable pageable) {
        return pgRepository.searchWithFilters(city, minRent, maxRent, pgType, genderPreference,
                                            wifiRequired, acRequired, parkingRequired, pageable);
    }
    
    public Page<PG> getAvailablePGs(Pageable pageable) {
        return pgRepository.findAvailablePGs(pageable);
    }
    
    public Page<PG> getTopRatedPGs(BigDecimal minRating, Integer minReviews, Pageable pageable) {
        return pgRepository.findTopRatedPGs(minRating, minReviews, pageable);
    }
    
    public Page<PG> getVerifiedPGs(Pageable pageable) {
        return pgRepository.findByIsVerifiedTrueAndIsActiveTrueOrderByRatingDescCreatedAtDesc(pageable);
    }
    
    public List<String> getAllCities() {
        return pgRepository.findDistinctCities();
    }
    
    public Long countPGsByOwner(User owner) {
        return pgRepository.countByOwnerAndIsActiveTrue(owner);
    }
    
    public void updateAvailableRooms(Long pgId, Integer availableRooms) {
        Optional<PG> pgOpt = pgRepository.findById(pgId);
        if (pgOpt.isPresent()) {
            PG pg = pgOpt.get();
            pg.setAvailableRooms(availableRooms);
            pgRepository.save(pg);
        }
    }
    
    public void updatePGRating(PG pg) {
        BigDecimal averageRating = reviewRepository.getAverageRatingByPg(pg);
        Long totalReviews = reviewRepository.countByPg(pg);
        
        if (averageRating != null) {
            pg.setRating(averageRating.setScale(1, RoundingMode.HALF_UP));
        } else {
            pg.setRating(BigDecimal.ZERO);
        }
        
        pg.setTotalReviews(totalReviews.intValue());
        pgRepository.save(pg);
    }
    
    public PG verifyPG(Long pgId) {
        Optional<PG> pgOpt = pgRepository.findById(pgId);
        if (pgOpt.isPresent()) {
            PG pg = pgOpt.get();
            pg.setIsVerified(true);
            return pgRepository.save(pg);
        }
        throw new IllegalArgumentException("PG not found");
    }
    
    public void decreaseAvailableRooms(Long pgId) {
        Optional<PG> pgOpt = pgRepository.findById(pgId);
        if (pgOpt.isPresent()) {
            PG pg = pgOpt.get();
            if (pg.getAvailableRooms() > 0) {
                pg.setAvailableRooms(pg.getAvailableRooms() - 1);
                pgRepository.save(pg);
            }
        }
    }
    
    public void increaseAvailableRooms(Long pgId) {
        Optional<PG> pgOpt = pgRepository.findById(pgId);
        if (pgOpt.isPresent()) {
            PG pg = pgOpt.get();
            if (pg.getAvailableRooms() < pg.getTotalRooms()) {
                pg.setAvailableRooms(pg.getAvailableRooms() + 1);
                pgRepository.save(pg);
            }
        }
    }
}
