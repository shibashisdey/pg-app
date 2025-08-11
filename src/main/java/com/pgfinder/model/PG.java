package com.pgfinder.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pgs")
public class PG {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "PG name is required")
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @NotBlank(message = "Address is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String address;
    
    @NotBlank(message = "City is required")
    @Column(nullable = false)
    private String city;
    
    @NotBlank(message = "State is required")
    @Column(nullable = false)
    private String state;
    
    @NotBlank(message = "Pincode is required")
    @Column(nullable = false, length = 6)
    private String pincode;
    
    @Column(precision = 10, scale = 8)
    private BigDecimal latitude;
    
    @Column(precision = 11, scale = 8)
    private BigDecimal longitude;
    
    @NotNull(message = "Rent is required")
    @Min(value = 1000, message = "Rent must be at least 1000")
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal rent;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal securityDeposit;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PGType pgType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GenderPreference genderPreference;
    
    @NotNull(message = "Total rooms is required")
    @Min(value = 1, message = "Must have at least 1 room")
    @Column(name = "total_rooms", nullable = false)
    private Integer totalRooms;
    
    @NotNull(message = "Available rooms is required")
    @Min(value = 0, message = "Available rooms cannot be negative")
    @Column(name = "available_rooms", nullable = false)
    private Integer availableRooms;
    
    @Column(name = "max_occupancy_per_room")
    @Min(value = 1, message = "Max occupancy must be at least 1")
    @Max(value = 4, message = "Max occupancy cannot exceed 4")
    private Integer maxOccupancyPerRoom;
    
    // Amenities
    @Column(name = "wifi_available")
    private Boolean wifiAvailable = false;
    
    @Column(name = "ac_available")
    private Boolean acAvailable = false;
    
    @Column(name = "parking_available")
    private Boolean parkingAvailable = false;
    
    @Column(name = "laundry_available")
    private Boolean laundryAvailable = false;
    
    @Column(name = "kitchen_available")
    private Boolean kitchenAvailable = false;
    
    @Column(name = "meals_provided")
    private Boolean mealsProvided = false;
    
    @Column(name = "cleaning_service")
    private Boolean cleaningService = false;
    
    // Rules and Preferences
    @Column(name = "smoking_allowed")
    private Boolean smokingAllowed = false;
    
    @Column(name = "drinking_allowed")
    private Boolean drinkingAllowed = false;
    
    @Column(name = "visitors_allowed")
    private Boolean visitorsAllowed = true;
    
    @Column(name = "pets_allowed")
    private Boolean petsAllowed = false;
    
    // Contact Information
    @Column(name = "contact_person")
    private String contactPerson;
    
    @Column(name = "contact_phone")
    private String contactPhone;
    
    @Column(name = "contact_email")
    private String contactEmail;
    
    // Status and Verification
    @Column(name = "is_active")
    private Boolean isActive = true;
    
    @Column(name = "is_verified")
    private Boolean isVerified = false;
    
    @Min(value = 0, message = "Rating cannot be negative")
    @Max(value = 5, message = "Rating cannot exceed 5")
    @Column(precision = 2, scale = 1)
    private BigDecimal rating = BigDecimal.ZERO;
    
    @Column(name = "total_reviews")
    private Integer totalReviews = 0;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PGImage> images;
    
    @OneToMany(mappedBy = "pg", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Review> reviews;
    
    // Enums
    public enum PGType {
        BOYS, GIRLS, COED
    }
    
    public enum GenderPreference {
        MALE_ONLY, FEMALE_ONLY, MIXED
    }
    
    // Constructors
    public PG() {}
    
    public PG(String name, String address, String city, String state, String pincode, 
              BigDecimal rent, PGType pgType, GenderPreference genderPreference, 
              Integer totalRooms, Integer availableRooms, User owner) {
        this.name = name;
        this.address = address;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.rent = rent;
        this.pgType = pgType;
        this.genderPreference = genderPreference;
        this.totalRooms = totalRooms;
        this.availableRooms = availableRooms;
        this.owner = owner;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getPincode() {
        return pincode;
    }
    
    public void setPincode(String pincode) {
        this.pincode = pincode;
    }
    
    public BigDecimal getLatitude() {
        return latitude;
    }
    
    public void setLatitude(BigDecimal latitude) {
        this.latitude = latitude;
    }
    
    public BigDecimal getLongitude() {
        return longitude;
    }
    
    public void setLongitude(BigDecimal longitude) {
        this.longitude = longitude;
    }
    
    public BigDecimal getRent() {
        return rent;
    }
    
    public void setRent(BigDecimal rent) {
        this.rent = rent;
    }
    
    public BigDecimal getSecurityDeposit() {
        return securityDeposit;
    }
    
    public void setSecurityDeposit(BigDecimal securityDeposit) {
        this.securityDeposit = securityDeposit;
    }
    
    public PGType getPgType() {
        return pgType;
    }
    
    public void setPgType(PGType pgType) {
        this.pgType = pgType;
    }
    
    public GenderPreference getGenderPreference() {
        return genderPreference;
    }
    
    public void setGenderPreference(GenderPreference genderPreference) {
        this.genderPreference = genderPreference;
    }
    
    public Integer getTotalRooms() {
        return totalRooms;
    }
    
    public void setTotalRooms(Integer totalRooms) {
        this.totalRooms = totalRooms;
    }
    
    public Integer getAvailableRooms() {
        return availableRooms;
    }
    
    public void setAvailableRooms(Integer availableRooms) {
        this.availableRooms = availableRooms;
    }
    
    public Integer getMaxOccupancyPerRoom() {
        return maxOccupancyPerRoom;
    }
    
    public void setMaxOccupancyPerRoom(Integer maxOccupancyPerRoom) {
        this.maxOccupancyPerRoom = maxOccupancyPerRoom;
    }
    
    // Amenities getters and setters
    public Boolean getWifiAvailable() {
        return wifiAvailable;
    }
    
    public void setWifiAvailable(Boolean wifiAvailable) {
        this.wifiAvailable = wifiAvailable;
    }
    
    public Boolean getAcAvailable() {
        return acAvailable;
    }
    
    public void setAcAvailable(Boolean acAvailable) {
        this.acAvailable = acAvailable;
    }
    
    public Boolean getParkingAvailable() {
        return parkingAvailable;
    }
    
    public void setParkingAvailable(Boolean parkingAvailable) {
        this.parkingAvailable = parkingAvailable;
    }
    
    public Boolean getLaundryAvailable() {
        return laundryAvailable;
    }
    
    public void setLaundryAvailable(Boolean laundryAvailable) {
        this.laundryAvailable = laundryAvailable;
    }
    
    public Boolean getKitchenAvailable() {
        return kitchenAvailable;
    }
    
    public void setKitchenAvailable(Boolean kitchenAvailable) {
        this.kitchenAvailable = kitchenAvailable;
    }
    
    public Boolean getMealsProvided() {
        return mealsProvided;
    }
    
    public void setMealsProvided(Boolean mealsProvided) {
        this.mealsProvided = mealsProvided;
    }
    
    public Boolean getCleaningService() {
        return cleaningService;
    }
    
    public void setCleaningService(Boolean cleaningService) {
        this.cleaningService = cleaningService;
    }
    
    // Rules getters and setters
    public Boolean getSmokingAllowed() {
        return smokingAllowed;
    }
    
    public void setSmokingAllowed(Boolean smokingAllowed) {
        this.smokingAllowed = smokingAllowed;
    }
    
    public Boolean getDrinkingAllowed() {
        return drinkingAllowed;
    }
    
    public void setDrinkingAllowed(Boolean drinkingAllowed) {
        this.drinkingAllowed = drinkingAllowed;
    }
    
    public Boolean getVisitorsAllowed() {
        return visitorsAllowed;
    }
    
    public void setVisitorsAllowed(Boolean visitorsAllowed) {
        this.visitorsAllowed = visitorsAllowed;
    }
    
    public Boolean getPetsAllowed() {
        return petsAllowed;
    }
    
    public void setPetsAllowed(Boolean petsAllowed) {
        this.petsAllowed = petsAllowed;
    }
    
    // Contact getters and setters
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getContactPhone() {
        return contactPhone;
    }
    
    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }
    
    public String getContactEmail() {
        return contactEmail;
    }
    
    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsVerified() {
        return isVerified;
    }
    
    public void setIsVerified(Boolean isVerified) {
        this.isVerified = isVerified;
    }
    
    public BigDecimal getRating() {
        return rating;
    }
    
    public void setRating(BigDecimal rating) {
        this.rating = rating;
    }
    
    public Integer getTotalReviews() {
        return totalReviews;
    }
    
    public void setTotalReviews(Integer totalReviews) {
        this.totalReviews = totalReviews;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public User getOwner() {
        return owner;
    }
    
    public void setOwner(User owner) {
        this.owner = owner;
    }
    
    public List<PGImage> getImages() {
        return images;
    }
    
    public void setImages(List<PGImage> images) {
        this.images = images;
    }
    
    public List<Review> getReviews() {
        return reviews;
    }
    
    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }
}
