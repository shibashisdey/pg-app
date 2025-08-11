package com.pgfinder.repository;

import com.pgfinder.model.PG;
import com.pgfinder.model.PGImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PGImageRepository extends JpaRepository<PGImage, Long> {
    
    List<PGImage> findByPg(PG pg);
    
    List<PGImage> findByPgOrderByIsPrimaryDescUploadedAtDesc(PG pg);
    
    List<PGImage> findByPgAndIsPrimaryTrue(PG pg);
    
    long countByPg(PG pg);
    
    boolean existsByPgAndIsPrimaryTrue(PG pg);
    
    void deleteByPg(PG pg);
}
