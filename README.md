# PG Finder Application

A comprehensive Spring Boot application for managing Paying Guest (PG) accommodations with JWT authentication, email verification, and image upload capabilities.

## Features

### For Users
- **Advanced Search**: Search PGs by city, rent range, PG type, and amenities
- **Filtering**: Filter results by various criteria like WiFi, AC, parking, etc.
- **User Registration & Login**: Secure user authentication
- **Favorites**: Save favorite PGs for later reference
- **Reviews & Ratings**: View and submit reviews for PGs

### For PG Owners
- **PG Registration**: Register and manage PG properties
- **Property Management**: Add/edit PG details, amenities, and availability
- **Room Management**: Track available and occupied rooms
- **Contact Management**: Manage contact information for inquiries

### System Features
- **RESTful APIs**: Comprehensive REST API for all operations
- **Database Integration**: H2 (development) and MySQL (production) support
- **Security**: Spring Security with password encryption
- **Validation**: Input validation and error handling
- **Responsive Web Interface**: Modern, mobile-friendly UI

## Technology Stack

- **Backend**: Spring Boot 3.2.0
- **Database**: H2 (development), MySQL (production)
- **ORM**: Spring Data JPA with Hibernate
- **Security**: Spring Security
- **Build Tool**: Maven
- **Java Version**: 17
- **Frontend**: HTML, CSS, JavaScript (included)

## Project Structure

```
src/
├── main/
│   ├── java/com/pgfinder/
│   │   ├── config/          # Security and configuration
│   │   ├── controller/      # REST controllers
│   │   ├── model/           # Entity models
│   │   ├── repository/      # Data repositories
│   │   ├── service/         # Business logic
│   │   └── PgFinderApplication.java
│   └── resources/
│       ├── static/          # Static web assets
│       └── application.properties
```

## Setup Instructions

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- MySQL (optional, for production)

### Running the Application

1. **Clone or navigate to the project directory**:
   ```bash
   cd pg-finder-app
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```

4. **Access the application**:
   - Web Interface: http://localhost:8080
   - API Base URL: http://localhost:8080/api
   - H2 Console: http://localhost:8080/api/h2-console

### Database Configuration

#### H2 (Default - Development)
The application uses H2 in-memory database by default. No additional setup required.

#### MySQL (Production)
To use MySQL, update `application.properties`:

```properties
# Comment out H2 configuration and uncomment MySQL configuration
spring.datasource.url=jdbc:mysql://localhost:3306/pgfinder
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
```

## API Endpoints

### User Management
- `POST /api/users/register` - Register new user
- `POST /api/users/login` - User login
- `GET /api/users/{id}` - Get user details
- `PUT /api/users/{id}` - Update user profile
- `POST /api/users/{id}/change-password` - Change password

### PG Management
- `POST /api/pgs` - Create new PG
- `GET /api/pgs` - Get all PGs (with pagination)
- `GET /api/pgs/{id}` - Get PG by ID
- `PUT /api/pgs/{id}` - Update PG
- `DELETE /api/pgs/{id}` - Deactivate PG
- `GET /api/pgs/search?keyword=...` - Search PGs
- `GET /api/pgs/filter?city=...&minRent=...` - Filter PGs
- `GET /api/pgs/city/{city}` - Get PGs by city
- `GET /api/pgs/owner/{ownerId}` - Get PGs by owner
- `GET /api/pgs/available` - Get available PGs
- `GET /api/pgs/verified` - Get verified PGs
- `GET /api/pgs/top-rated` - Get top-rated PGs
- `GET /api/pgs/cities` - Get all cities

### Sample API Requests

#### Register a User
```json
POST /api/users/register
{
  "name": "John Doe",
  "email": "john@example.com",
  "password": "password123",
  "phoneNumber": "9876543210",
  "userType": "USER"
}
```

#### Create a PG (Owner)
```json
POST /api/pgs
{
  "name": "Comfort PG",
  "description": "Modern PG with all amenities",
  "address": "123 Main Street, Sector 1",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "rent": 12000,
  "securityDeposit": 24000,
  "pgType": "COED",
  "genderPreference": "MIXED",
  "totalRooms": 10,
  "availableRooms": 3,
  "maxOccupancyPerRoom": 2,
  "wifiAvailable": true,
  "acAvailable": true,
  "parkingAvailable": true,
  "kitchenAvailable": true,
  "contactPerson": "Owner Name",
  "contactPhone": "9876543210",
  "contactEmail": "owner@example.com",
  "owner": {
    "id": 1
  }
}
```

#### Search PGs
```
GET /api/pgs/filter?city=Mumbai&minRent=10000&maxRent=15000&wifiRequired=true
```

## Data Models

### User
- Basic user information (name, email, phone)
- User type (USER, OWNER, ADMIN)
- Authentication details

### PG
- Property details (name, address, city, state)
- Pricing (rent, security deposit)
- Room information (total, available, occupancy)
- Amenities (WiFi, AC, parking, kitchen, etc.)
- Rules (smoking, drinking, visitors, pets)
- Contact information
- Verification and rating status

### Review
- User reviews and ratings for PGs
- Rating scale: 1-5 stars
- Comment/feedback text

## Default Configuration

- **Server Port**: 8080
- **Context Path**: /api
- **Database**: H2 in-memory
- **Security**: All endpoints open for testing (customize as needed)

## Testing the Application

1. **Start the application**
2. **Open your browser** and go to http://localhost:8080
3. **Use the web interface** to search for PGs
4. **Test API endpoints** using tools like Postman or curl
5. **Access H2 Console** at http://localhost:8080/api/h2-console for database inspection

## Production Deployment

For production deployment:

1. **Configure MySQL database**
2. **Update application.properties** with production settings
3. **Build the JAR**: `mvn clean package`
4. **Deploy**: `java -jar target/pg-finder-app-0.0.1-SNAPSHOT.jar`

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Test thoroughly
5. Submit a pull request

## Future Enhancements

- JWT token-based authentication
- Image upload for PGs
- Advanced search with geolocation
- Email notifications
- Payment integration
- Mobile application
- Admin dashboard
- Real-time chat between users and owners

## License

This project is open source and available under the MIT License.

## Support

For support or questions, please create an issue in the project repository.
