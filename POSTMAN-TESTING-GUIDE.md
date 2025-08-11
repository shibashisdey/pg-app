# Postman Testing Guide for PG Finder API

## 🚀 Quick Setup

### 1. Import the Collection
1. Open Postman
2. Click **Import** > **Upload Files**
3. Select `PG-Finder-API-Collection.postman_collection.json`
4. The collection will be imported with all endpoints and variables

### 2. Environment Variables
The collection includes these pre-configured variables:
- `base_url`: http://localhost:8080/api
- `access_token`: Auto-populated after login
- `refresh_token`: Auto-populated after login
- `user_id`: Auto-populated after login/registration
- `pg_id`: Auto-populated after PG creation
- `image_id`: Auto-populated after image upload

## 🔥 Quick Testing Workflow

### Step 1: Start the Application
```bash
cd pg-app
mvn spring-boot:run
```
Wait for the message: "=== PG Finder Application Started Successfully ==="

### Step 2: Test Authentication Flow

#### A. Login with Admin (Recommended Start)
```
POST /users/auth/login
Body: {
  "email": "admin@pgfinder.com",
  "password": "admin123"
}
```
✅ **This will auto-populate your access_token and refresh_token variables**

#### B. Or Login with Pre-verified Owner
```
POST /users/auth/login  
Body: {
  "email": "jane@example.com",
  "password": "password123"
}
```

### Step 3: Test User Registration & Email Verification

#### Register New User
```
POST /users/auth/register
Body: {
  "name": "Test User",
  "email": "testuser@example.com",
  "password": "password123",
  "phoneNumber": "9876543210",
  "userType": "USER"
}
```

#### Resend Verification (if needed)
```
POST /users/resend-verification
Body: {
  "email": "testuser@example.com"
}
```

#### Verify Email (use token from email/logs)
```
GET /users/verify-email?token=YOUR_TOKEN_HERE
```

### Step 4: Test PG Management

#### Create PG (requires OWNER login)
```
POST /pgs
Headers: Authorization: Bearer {{access_token}}
Body: {JSON payload for PG}
```
✅ **This will auto-populate the pg_id variable**

#### Get All PGs
```
GET /pgs?page=0&size=10
```

#### Search & Filter PGs
```
GET /pgs/search?keyword=bangalore
GET /pgs/filter?city=Bangalore&minRent=10000&maxRent=20000
```

### Step 5: Test Image Upload

#### Upload PG Image
```
POST /pgs/1/images/upload
Headers: Authorization: Bearer {{access_token}}
Body: form-data
- file: [SELECT IMAGE FILE]
- caption: "Room view"
- isPrimary: false
```
✅ **This will auto-populate the image_id variable**

#### Manage Images
```
GET /pgs/1/images
PUT /pgs/images/{{image_id}}/set-primary
PUT /pgs/images/{{image_id}}/caption
DELETE /pgs/images/{{image_id}}
```

## 📋 Testing Scenarios by Feature

### 🔐 JWT Authentication
| Test Case | Endpoint | Expected Result |
|-----------|----------|-----------------|
| Admin Login | `POST /users/auth/login` | JWT tokens returned |
| Token Refresh | `POST /users/auth/refresh` | New tokens returned |
| Invalid Login | `POST /users/auth/login` | 401 Unauthorized |
| Expired Token | Any protected endpoint | 401 Unauthorized |
| Logout | `POST /users/auth/logout` | Refresh token cleared |

### 📧 Email Verification
| Test Case | Endpoint | Expected Result |
|-----------|----------|-----------------|
| Register User | `POST /users/auth/register` | User created, email sent |
| Login Unverified | `POST /users/auth/login` | 403 EMAIL_NOT_VERIFIED |
| Verify Email | `GET /users/verify-email?token=X` | Email verified |
| Resend Email | `POST /users/resend-verification` | New email sent |
| Resend Too Soon | `POST /users/resend-verification` | Rate limit error |

### 🏠 PG Management
| Test Case | Endpoint | Expected Result |
|-----------|----------|-----------------|
| Create PG | `POST /pgs` | PG created successfully |
| Get All PGs | `GET /pgs` | Paginated PG list |
| Search PGs | `GET /pgs/search?keyword=X` | Filtered results |
| Filter PGs | `GET /pgs/filter?city=X` | Filtered results |
| Update PG | `PUT /pgs/{id}` | PG updated |
| Delete PG | `DELETE /pgs/{id}` | PG deactivated |

### 🖼️ Image Management  
| Test Case | Endpoint | Expected Result |
|-----------|----------|-----------------|
| Upload Image | `POST /pgs/{id}/images/upload` | Image uploaded |
| Invalid File Type | Upload .txt file | 400 Bad Request |
| File Too Large | Upload >5MB file | 400 Bad Request |
| Get PG Images | `GET /pgs/{id}/images` | Image list returned |
| Set Primary | `PUT /pgs/images/{id}/set-primary` | Primary image set |
| Update Caption | `PUT /pgs/images/{id}/caption` | Caption updated |
| Delete Image | `DELETE /pgs/images/{id}` | Image deleted |

## ⚠️ Common Issues & Solutions

### Issue 1: Email Not Sending
**Problem**: Registration works but no verification email  
**Solution**: Check application.properties email configuration
```properties
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password  # Use Gmail App Password
```

### Issue 2: File Upload Failing  
**Problem**: Image upload returns 400 error  
**Solution**: 
- Ensure file is selected in form-data
- Check file size (max 5MB)
- Use supported formats: JPG, PNG, GIF, WEBP

### Issue 3: JWT Token Issues
**Problem**: 401 Unauthorized on protected endpoints  
**Solution**:
- Login first to get access token
- Check Authorization header: `Bearer {{access_token}}`
- Token expires in 24 hours, use refresh token

### Issue 4: Database Connection
**Problem**: Application won't start  
**Solution**: 
- Check PostgreSQL is running
- Verify database credentials in application.properties
- Or switch to H2 for testing

## 🎯 Test Results to Expect

### Successful Login Response:
```json
{
  "success": true,
  "message": "Login successful",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
    "tokenType": "Bearer",
    "email": "admin@pgfinder.com",
    "name": "System Administrator",
    "userType": "ADMIN",
    "userId": 5,
    "isVerified": true
  }
}
```

### Successful PG Creation:
```json
{
  "success": true,
  "message": "PG created successfully",
  "pg": {
    "id": 9,
    "name": "Test PG via Postman",
    "city": "Bangalore",
    "rent": 15000,
    "pgType": "COED",
    "isActive": true,
    "isVerified": false,
    // ... other fields
  }
}
```

### Successful Image Upload:
```json
{
  "success": true,
  "message": "Image uploaded successfully",
  "image": {
    "id": 1,
    "imageUrl": "http://localhost:8080/api/images/pg-images/uuid-filename.jpg",
    "caption": "Room view from API",
    "isPrimary": false,
    "uploadedAt": "2025-01-11T10:30:00"
  }
}
```

## 🔄 Automated Testing Tips

1. **Use Collection Variables**: The collection automatically stores tokens and IDs
2. **Run in Sequence**: Follow the folder order for best results
3. **Check Status Codes**: 
   - 200: Success
   - 400: Bad Request (check payload)
   - 401: Unauthorized (check token)
   - 403: Forbidden (email not verified)
   - 404: Not Found
   - 500: Server Error (check logs)

## 📊 Collection Structure

```
📁 Authentication
  ├── Register User with Email Verification
  ├── Register Owner  
  ├── Login (JWT)
  ├── Login with Test User (Pre-verified)
  ├── Refresh Token
  └── Logout

📁 Email Verification
  ├── Verify Email (GET)
  └── Resend Verification Email

📁 User Management
  ├── Get User by ID
  ├── Update User Profile
  ├── Change Password
  ├── Get Users by Type
  └── Get User Statistics

📁 PG Management
  ├── Create PG
  ├── Get All PGs
  ├── Get PG by ID
  ├── Update PG
  ├── Search PGs
  ├── Filter PGs
  ├── Get PGs by City/Owner/etc.
  ├── Verify PG (Admin Only)
  ├── Update Available Rooms
  └── Delete PG

📁 Image Management
  ├── Upload PG Image
  ├── Get PG Images
  ├── Set Primary Image
  ├── Update Image Caption
  └── Delete Image

📁 Legacy Endpoints (Non-JWT)
  ├── Legacy Register User
  └── Legacy Login
```

## 🎉 Happy Testing!

This collection provides comprehensive coverage of all implemented features. Start with authentication, then explore user management, PG operations, and image handling. The automated variable management will make your testing smooth and efficient!

For issues or questions, check the console logs in your Spring Boot application - they provide detailed error information.
