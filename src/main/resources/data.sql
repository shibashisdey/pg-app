-- Sample Users (Password: password123 encoded with BCrypt)
INSERT INTO users (name, email, password, phone_number, user_type, is_active, created_at, updated_at) VALUES
('John Doe', 'john@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjOhcxzBg9.CfhFBPwRqYdNjWIZx3oOe1.', '9876543210', 'USER', true, NOW(), NOW()),
('Jane Smith', 'jane@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjOhcxzBg9.CfhFBPwRqYdNjWIZx3oOe1.', '9876543211', 'OWNER', true, NOW(), NOW()),
('Mike Johnson', 'mike@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjOhcxzBg9.CfhFBPwRqYdNjWIZx3oOe1.', '9876543212', 'OWNER', true, NOW(), NOW()),
('Sarah Wilson', 'sarah@example.com', '$2a$10$eImiTXuWVxfM37uY4JANjOhcxzBg9.CfhFBPwRqYdNjWIZx3oOe1.', '9876543213', 'USER', true, NOW(), NOW()),
('Admin User', 'admin@pgfinder.com', '$2a$10$eImiTXuWVxfM37uY4JANjOhcxzBg9.CfhFBPwRqYdNjWIZx3oOe1.', '9876543214', 'ADMIN', true, NOW(), NOW());

-- Sample PGs
INSERT INTO pgs (name, description, address, city, state, pincode, rent, security_deposit, pg_type, gender_preference, total_rooms, available_rooms, max_occupancy_per_room, wifi_available, ac_available, parking_available, laundry_available, kitchen_available, meals_provided, cleaning_service, smoking_allowed, drinking_allowed, visitors_allowed, pets_allowed, contact_person, contact_phone, contact_email, is_active, is_verified, rating, total_reviews, owner_id, created_at, updated_at) VALUES

('Green Valley PG', 'Comfortable PG with all modern amenities in a peaceful location', '123 Green Valley Road, Sector 15', 'Bangalore', 'Karnataka', '560001', 12000, 24000, 'COED', 'MIXED', 20, 5, 2, true, true, true, true, true, false, true, false, false, true, false, 'Jane Smith', '9876543211', 'jane@example.com', true, true, 4.2, 15, 2, NOW(), NOW()),

('Boys Paradise PG', 'Exclusively for boys with excellent facilities and security', '456 Paradise Street, Block A', 'Mumbai', 'Maharashtra', '400001', 15000, 30000, 'BOYS', 'MALE_ONLY', 15, 3, 3, true, true, true, true, false, true, true, false, false, true, false, 'Mike Johnson', '9876543212', 'mike@example.com', true, true, 4.5, 22, 3, NOW(), NOW()),

('Girls Comfort Zone', 'Safe and secure accommodation for working women', '789 Comfort Lane, Near Metro', 'Delhi', 'Delhi', '110001', 13500, 27000, 'GIRLS', 'FEMALE_ONLY', 12, 2, 2, true, false, false, true, true, true, true, false, false, true, false, 'Jane Smith', '9876543211', 'jane@example.com', true, false, 4.0, 8, 2, NOW(), NOW()),

('Urban Living PG', 'Modern PG in the heart of the city with premium facilities', '321 Urban Complex, City Center', 'Pune', 'Maharashtra', '411001', 18000, 36000, 'COED', 'MIXED', 25, 8, 2, true, true, true, true, true, false, true, false, false, true, true, 'Mike Johnson', '9876543212', 'mike@example.com', true, true, 4.7, 35, 3, NOW(), NOW()),

('Budget Friendly PG', 'Affordable accommodation with basic amenities', '654 Budget Street, Old Town', 'Chennai', 'Tamil Nadu', '600001', 8000, 16000, 'BOYS', 'MALE_ONLY', 10, 4, 3, true, false, false, true, true, false, false, true, false, true, false, 'Jane Smith', '9876543211', 'jane@example.com', true, false, 3.5, 12, 2, NOW(), NOW()),

('Elite Women PG', 'Premium accommodation for professional women', '987 Elite Avenue, Business District', 'Hyderabad', 'Telangana', '500001', 20000, 40000, 'GIRLS', 'FEMALE_ONLY', 18, 1, 1, true, true, true, true, true, true, true, false, false, false, false, 'Mike Johnson', '9876543212', 'mike@example.com', true, true, 4.8, 28, 3, NOW(), NOW()),

('Student Hub PG', 'Perfect for students and young professionals', '147 Student Road, University Area', 'Bangalore', 'Karnataka', '560002', 10000, 20000, 'COED', 'MIXED', 30, 12, 3, true, false, true, true, true, false, false, false, false, true, false, 'Jane Smith', '9876543211', 'jane@example.com', true, false, 3.8, 18, 2, NOW(), NOW()),

('Premium Boys PG', 'High-end accommodation for working professionals', '258 Premium Plaza, IT Corridor', 'Mumbai', 'Maharashtra', '400002', 22000, 44000, 'BOYS', 'MALE_ONLY', 16, 2, 2, true, true, true, true, false, true, true, false, true, true, false, 'Mike Johnson', '9876543212', 'mike@example.com', true, true, 4.6, 31, 3, NOW(), NOW());

-- Sample Reviews
INSERT INTO reviews (rating, comment, pg_id, user_id, created_at) VALUES
(5, 'Excellent PG with great facilities. The staff is very helpful and the location is perfect.', 1, 1, NOW()),
(4, 'Good value for money. Clean rooms and decent amenities.', 1, 4, NOW()),
(5, 'Amazing place for boys! Great security and all facilities are top-notch.', 2, 1, NOW()),
(4, 'Nice PG but could improve the food quality. Overall satisfied.', 2, 4, NOW()),
(3, 'Decent place but a bit overpriced for the amenities provided.', 3, 1, NOW()),
(5, 'Love this place! Modern facilities and excellent management.', 4, 4, NOW()),
(4, 'Good budget option. Basic but clean and well-maintained.', 5, 1, NOW()),
(5, 'Perfect for working women. Safe, clean, and all amenities available.', 6, 4, NOW());
