package com.pgfinder.controller;

import com.pgfinder.model.PG;
import com.pgfinder.model.User;
import com.pgfinder.service.PGService;
import com.pgfinder.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/pgs")
@CrossOrigin(origins = "*")
public class PGController {
    
    @Autowired
    private PGService pgService;
    
    @Autowired
    private UserService userService;
    
    @PostMapping
    public ResponseEntity<Map<String, Object>> createPG(@Valid @RequestBody PG pg) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PG createdPG = pgService.createPG(pg);
            response.put("success", true);
            response.put("message", "PG created successfully");
            response.put("pg", createdPG);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to create PG: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getPGById(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        Optional<PG> pgOpt = pgService.findById(id);
        
        if (pgOpt.isPresent()) {
            response.put("success", true);
            response.put("pg", pgOpt.get());
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "PG not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updatePG(@PathVariable Long id, @Valid @RequestBody PG pgUpdate) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            Optional<PG> pgOpt = pgService.findById(id);
            
            if (pgOpt.isPresent()) {
                PG existingPG = pgOpt.get();
                
                // Update fields
                existingPG.setName(pgUpdate.getName());
                existingPG.setDescription(pgUpdate.getDescription());
                existingPG.setAddress(pgUpdate.getAddress());
                existingPG.setCity(pgUpdate.getCity());
                existingPG.setState(pgUpdate.getState());
                existingPG.setPincode(pgUpdate.getPincode());
                existingPG.setRent(pgUpdate.getRent());
                existingPG.setSecurityDeposit(pgUpdate.getSecurityDeposit());
                existingPG.setPgType(pgUpdate.getPgType());
                existingPG.setGenderPreference(pgUpdate.getGenderPreference());
                existingPG.setTotalRooms(pgUpdate.getTotalRooms());
                existingPG.setAvailableRooms(pgUpdate.getAvailableRooms());
                existingPG.setMaxOccupancyPerRoom(pgUpdate.getMaxOccupancyPerRoom());
                
                // Update amenities
                existingPG.setWifiAvailable(pgUpdate.getWifiAvailable());
                existingPG.setAcAvailable(pgUpdate.getAcAvailable());
                existingPG.setParkingAvailable(pgUpdate.getParkingAvailable());
                existingPG.setLaundryAvailable(pgUpdate.getLaundryAvailable());
                existingPG.setKitchenAvailable(pgUpdate.getKitchenAvailable());
                existingPG.setMealsProvided(pgUpdate.getMealsProvided());
                existingPG.setCleaningService(pgUpdate.getCleaningService());
                
                // Update rules
                existingPG.setSmokingAllowed(pgUpdate.getSmokingAllowed());
                existingPG.setDrinkingAllowed(pgUpdate.getDrinkingAllowed());
                existingPG.setVisitorsAllowed(pgUpdate.getVisitorsAllowed());
                existingPG.setPetsAllowed(pgUpdate.getPetsAllowed());
                
                // Update contact info
                existingPG.setContactPerson(pgUpdate.getContactPerson());
                existingPG.setContactPhone(pgUpdate.getContactPhone());
                existingPG.setContactEmail(pgUpdate.getContactEmail());
                
                PG updatedPG = pgService.updatePG(existingPG);
                response.put("success", true);
                response.put("message", "PG updated successfully");
                response.put("pg", updatedPG);
                return ResponseEntity.ok(response);
            } else {
                response.put("success", false);
                response.put("message", "PG not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to update PG: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deletePG(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        pgService.deletePG(id);
        response.put("success", true);
        response.put("message", "PG deactivated successfully");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllPGs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.getAllActivePGs(pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchPGs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.searchPGs(keyword, pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/filter")
    public ResponseEntity<Map<String, Object>> filterPGs(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minRent,
            @RequestParam(required = false) BigDecimal maxRent,
            @RequestParam(required = false) String pgType,
            @RequestParam(required = false) String genderPreference,
            @RequestParam(defaultValue = "false") Boolean wifiRequired,
            @RequestParam(defaultValue = "false") Boolean acRequired,
            @RequestParam(defaultValue = "false") Boolean parkingRequired,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            Pageable pageable = PageRequest.of(page, size);
            
            PG.PGType pgTypeEnum = pgType != null ? PG.PGType.valueOf(pgType.toUpperCase()) : null;
            PG.GenderPreference genderPrefEnum = genderPreference != null ? 
                PG.GenderPreference.valueOf(genderPreference.toUpperCase()) : null;
            
            Page<PG> pgs = pgService.searchWithFilters(city, minRent, maxRent, pgTypeEnum, 
                genderPrefEnum, wifiRequired, acRequired, parkingRequired, pageable);
            
            response.put("success", true);
            response.put("pgs", pgs.getContent());
            response.put("pagination", createPaginationInfo(pgs));
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Filter failed: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    @GetMapping("/city/{city}")
    public ResponseEntity<Map<String, Object>> getPGsByCity(
            @PathVariable String city,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.getPGsByCity(city, pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/owner/{ownerId}")
    public ResponseEntity<Map<String, Object>> getPGsByOwner(
            @PathVariable Long ownerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        Optional<User> ownerOpt = userService.findById(ownerId);
        
        if (ownerOpt.isPresent()) {
            Pageable pageable = PageRequest.of(page, size);
            Page<PG> pgs = pgService.getPGsByOwner(ownerOpt.get(), pageable);
            
            response.put("success", true);
            response.put("pgs", pgs.getContent());
            response.put("pagination", createPaginationInfo(pgs));
            return ResponseEntity.ok(response);
        } else {
            response.put("success", false);
            response.put("message", "Owner not found");
            return ResponseEntity.notFound().build();
        }
    }
    
    @GetMapping("/available")
    public ResponseEntity<Map<String, Object>> getAvailablePGs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.getAvailablePGs(pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/top-rated")
    public ResponseEntity<Map<String, Object>> getTopRatedPGs(
            @RequestParam(defaultValue = "4.0") BigDecimal minRating,
            @RequestParam(defaultValue = "5") Integer minReviews,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.getTopRatedPGs(minRating, minReviews, pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/verified")
    public ResponseEntity<Map<String, Object>> getVerifiedPGs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        
        Map<String, Object> response = new HashMap<>();
        
        Pageable pageable = PageRequest.of(page, size);
        Page<PG> pgs = pgService.getVerifiedPGs(pageable);
        
        response.put("success", true);
        response.put("pgs", pgs.getContent());
        response.put("pagination", createPaginationInfo(pgs));
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cities")
    public ResponseEntity<Map<String, Object>> getAllCities() {
        Map<String, Object> response = new HashMap<>();
        
        List<String> cities = pgService.getAllCities();
        
        response.put("success", true);
        response.put("cities", cities);
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/{id}/verify")
    public ResponseEntity<Map<String, Object>> verifyPG(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            PG verifiedPG = pgService.verifyPG(id);
            response.put("success", true);
            response.put("message", "PG verified successfully");
            response.put("pg", verifiedPG);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/{id}/update-rooms")
    public ResponseEntity<Map<String, Object>> updateAvailableRooms(
            @PathVariable Long id,
            @RequestParam Integer availableRooms) {
        
        Map<String, Object> response = new HashMap<>();
        
        pgService.updateAvailableRooms(id, availableRooms);
        response.put("success", true);
        response.put("message", "Available rooms updated successfully");
        return ResponseEntity.ok(response);
    }
    
    // Helper method to create pagination information
    private Map<String, Object> createPaginationInfo(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("currentPage", page.getNumber());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("size", page.getSize());
        pagination.put("hasNext", page.hasNext());
        pagination.put("hasPrevious", page.hasPrevious());
        return pagination;
    }
}
