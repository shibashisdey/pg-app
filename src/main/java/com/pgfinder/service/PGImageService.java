package com.pgfinder.service;

import com.pgfinder.model.PG;
import com.pgfinder.model.PGImage;
import com.pgfinder.repository.PGImageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class PGImageService {

    @Autowired
    private PGImageRepository pgImageRepository;
    
    @Autowired
    private ImageUploadService imageUploadService;

    public PGImage uploadPGImage(PG pg, MultipartFile file, String caption, Boolean isPrimary) {
        try {
            // If this is set as primary, remove primary status from other images
            if (isPrimary != null && isPrimary) {
                List<PGImage> existingPrimaryImages = pgImageRepository.findByPgAndIsPrimaryTrue(pg);
                for (PGImage img : existingPrimaryImages) {
                    img.setIsPrimary(false);
                    pgImageRepository.save(img);
                }
            }

            // Upload image
            String imageUrl = imageUploadService.uploadImage(file, "pg-images");
            
            // Create PGImage entity
            PGImage pgImage = new PGImage();
            pgImage.setPg(pg);
            pgImage.setImageUrl(imageUrl);
            pgImage.setCaption(caption);
            pgImage.setIsPrimary(isPrimary != null ? isPrimary : false);
            
            return pgImageRepository.save(pgImage);
        } catch (Exception e) {
            throw new RuntimeException("Failed to upload image: " + e.getMessage(), e);
        }
    }

    public List<PGImage> getImagesByPG(PG pg) {
        return pgImageRepository.findByPgOrderByIsPrimaryDescUploadedAtDesc(pg);
    }
    
    public Optional<PGImage> getPrimaryImage(PG pg) {
        return pgImageRepository.findByPgAndIsPrimaryTrue(pg).stream().findFirst();
    }

    public void deleteImage(Long imageId) {
        Optional<PGImage> imageOpt = pgImageRepository.findById(imageId);
        if (imageOpt.isPresent()) {
            PGImage pgImage = imageOpt.get();
            
            // Delete physical file
            imageUploadService.deleteImage(pgImage.getImageUrl());
            
            // Delete database record
            pgImageRepository.delete(pgImage);
        }
    }
    
    public void deleteAllPGImages(PG pg) {
        List<PGImage> images = pgImageRepository.findByPg(pg);
        for (PGImage image : images) {
            imageUploadService.deleteImage(image.getImageUrl());
        }
        pgImageRepository.deleteByPg(pg);
    }

    public PGImage setPrimaryImage(Long imageId) {
        Optional<PGImage> imageOpt = pgImageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            throw new IllegalArgumentException("Image not found");
        }

        PGImage newPrimaryImage = imageOpt.get();
        PG pg = newPrimaryImage.getPg();

        // Remove primary status from all images of this PG
        List<PGImage> existingPrimaryImages = pgImageRepository.findByPgAndIsPrimaryTrue(pg);
        for (PGImage img : existingPrimaryImages) {
            img.setIsPrimary(false);
            pgImageRepository.save(img);
        }

        // Set new primary image
        newPrimaryImage.setIsPrimary(true);
        return pgImageRepository.save(newPrimaryImage);
    }

    public PGImage updateImageCaption(Long imageId, String caption) {
        Optional<PGImage> imageOpt = pgImageRepository.findById(imageId);
        if (imageOpt.isEmpty()) {
            throw new IllegalArgumentException("Image not found");
        }

        PGImage pgImage = imageOpt.get();
        pgImage.setCaption(caption);
        return pgImageRepository.save(pgImage);
    }

    public long countImagesByPG(PG pg) {
        return pgImageRepository.countByPg(pg);
    }
    
    public boolean hasPrimaryImage(PG pg) {
        return pgImageRepository.existsByPgAndIsPrimaryTrue(pg);
    }
}
