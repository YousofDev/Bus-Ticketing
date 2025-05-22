# Bus Ticketing System

A robust **Bus Ticketing System** built with **Java** and **Spring Boot**, designed to streamline bus travel operations for customers, staff, and administrators. This application provides a secure, user-friendly platform for managing user authentication, bus schedules, routes, appointments, tickets, and user accounts, with a focus on modern development practices including comprehensive testing, CI/CD, and containerization.

---

## 🚀 Features

### Authentication & User Management
- **User Registration & Login**: Secure user registration and login with JWT-based authentication.
- **Account Confirmation**: Email-based account verification using confirmation codes.
- **Password Management**: Password reset and change functionality for enhanced security.
- **User Profiles**: Users can view and update their profiles with personalized settings.
- **Token Refresh**: Supports token refresh for seamless user sessions.
- **Admin User Management**: Admins can create, read, update, and delete user accounts.

### Bus Management
- **CRUD Operations**: Admins can create, read, update, and delete bus records.
- **Role-Based Access**: Access restricted to authorized roles (Admin, Staff, Driver, Customer).

### Route Management
- **Route Creation & Updates**: Admins can manage bus routes with detailed information.
- **Public Access**: Customers, drivers, and staff can view available routes.

### Appointment Management
- **Schedule Creation**: Admins can create and manage bus trip schedules (appointments).
- **Seat Availability**: View active appointments with available seats for booking.

### Ticket Management
- **Ticket Booking**: Customers can book tickets for specific appointments.
- **Ticket Updates & Cancellation**: Staff and admins can update or cancel tickets.
- **Payment Processing**: Secure ticket payment functionality.
- **Ticket Retrieval**: View tickets by customer, appointment, or validity status.

---

## 🛠️ Technologies Used

- **Java 21**: Core programming language for backend development.
- **Spring Boot 3.4.5**: Framework for building a production-ready RESTful API.
- **PostgreSQL**: Relational database for storing user, bus, route, and ticket data.
- **Docker**: Containerization for consistent development and deployment environments.
- **GitHub Actions**: CI/CD pipeline for automated testing and building.
- **JUnit**: Comprehensive unit and integration tests for robust code quality.
- **Spring Security**: Secure authentication and role-based authorization.
- **JWT**: Token-based authentication for secure API access.
- **Maven**: Dependency management and build automation.

---

## 📚 API Endpoints

The application exposes a RESTful API under `/api/v1` with the following key endpoints:

### Authentication (`/api/v1/auth`)
- `POST /register`: Register a new user.
- `POST /login`: Authenticate and receive JWT tokens.
- `POST /confirm-account`: Confirm user account via email code.
- `POST /reset-password`: Initiate password reset.
- `POST /reset-password-confirm`: Confirm password reset.
- `POST /send-confirmation-code`: Send confirmation code to email.
- `POST /verify-confirmation-code`: Verify confirmation code.
- `GET /profile`: Retrieve user profile.
- `PUT /profile`: Update user profile.
- `PATCH /change-password`: Change user password.
- `POST /refresh-token`: Refresh JWT tokens.

### Users (`/api/v1/users`)
- `POST /`: Create a new user (Admin).
- `GET /`: List all users (Admin).
- `GET /{id}`: Get user details by ID (Admin).
- `PUT /{id}`: Update user details (Admin).
- `DELETE /{id}`: Delete a user (Admin).

### Buses (`/api/v1/buses`)
- `GET /`: List all buses (Admin, Staff, Driver, Customer).
- `POST /`: Create a new bus (Admin).
- `GET /{id}`: Get bus details by ID.
- `PUT /{id}`: Update bus details (Admin).
- `DELETE /{id}`: Delete a bus (Admin).

### Routes (`/api/v1/routes`)
- `GET /`: List all routes (Admin, Staff, Driver, Customer).
- `POST /`: Create a new route (Admin).
- `GET /{id}`: Get route details by ID.
- `PUT /{id}`: Update route details (Admin).
- `DELETE /{id}`: Delete a route (Admin).

### Appointments (`/api/v1/appointments`)
- `GET /`: List active appointments with available seats.
- `POST /`: Create a new appointment (Admin).
- `PUT /{id}`: Update an appointment (Admin).
- `DELETE /{id}`: Delete an appointment (Admin).

### Tickets (`/api/v1/tickets`)
- `POST /`: Create a new ticket (Admin, Staff, Customer).
- `GET /`: List all tickets (Admin, Staff).
- `GET /{id}`: Get ticket details by ID.
- `PUT /{id}`: Update ticket details (Admin, Staff).
- `PATCH /{id}/cancel`: Cancel a ticket (Admin, Staff).
- `PATCH /{id}/pay`: Pay for a ticket (Admin, Staff, Customer).
- `GET /customers/{customerId}`: List tickets by customer ID.
- `GET /appointments/{appointmentId}`: List tickets by appointment ID.
- `GET /valid`: List all valid tickets (Admin, Staff).
- `GET /customers/{customerId}/valid`: List valid tickets by customer ID.
- `GET /appointments/{appointmentId}/valid`: List valid tickets by appointment ID.

---

## 🏗️ Project Structure

Based on the repository at `https://github.com/YousofDev/busticketing`, the project follows a clean, modular structure:

```
busticketing/
├── src/
│   ├── main/
│   │   ├── java/com/yousofdev/busticketing/
│   │   │   ├── core/
│   │   │   │   ├── config/           # Spring configuration (e.g., SecurityConfig)
│   │   │   │   ├── exception/        # Custom exception handling
│   │   │   │   ├── notification/     # Notification-related
│   │   │   │   ├── security/         # Security configurations
│   │   │   │   ├── util/             # General utility classes
│   │   │   ├── auth/
│   │   │   │   ├── controller/       # Authentication-related controllers
│   │   │   │   ├── service/          # Authentication business logic
│   │   │   │   ├── repository/       # Authentication data access layer
│   │   │   │   ├── model/            # Authentication entities
│   │   │   │   ├── dto/              # Authentication DTOs
│   │   │   ├── reservation/
│   │   │   │   ├── controller/       # Reservation-related controllers (buses, routes, appointments, tickets)
│   │   │   │   ├── service/          # Reservation business logic
│   │   │   │   ├── repository/       # Reservation data access layer
│   │   │   │   ├── model/            # Reservation entities
│   │   │   │   ├── dto/              # Reservation DTOs
│   │   │   └── BusTicketingApplication.java # Main application entry point
│   │   ├── resources/
│   │       ├── application.yml # Configuration file
│   ├── test/
│   │   └── java/com/yousofdev/busticketing/ # integration tests
│	│		    ├── AuthControllerTest
│	│			├── BusControllerTest
│	│			├── RouteControllerTest
│	│			├── AppointmentControllerTest
│	│			├── TicketControllerTest
│	│			├── BaseIntegrationTest
├── .github/workflows/ci-cd-pipeline.yml     # GitHub Actions CI/CD configuration
├── Dockerfile           # Docker configuration for containerization
├── docker-compose.yml   # Docker Compose Configuration
├── pom.xml              # Maven configuration for dependencies
└── README.md
```

---

## 🐳 Running with Docker

1. **Prerequisites**:
   - Docker and Docker Compose installed.
   - PostgreSQL database configured (or use Dockerized PostgreSQL).

2. **Steps**:
   ```bash
   # Clone the repository
   git clone https://github.com/YousofDev/busticketing.git
   cd busticketing
   
   # Set the env properties
   cp env.example .env
   
   # Build and run with Docker
   docker-compose up --build
   ```

3. **Access the API**:
   - The application runs on `http://localhost:8080`.
   - Use tools like Postman or cURL to interact with the API.

---

## 🔄 CI/CD Pipeline

The project uses **GitHub Actions** for continuous integration and deployment:
- **Testing**: Runs code quality checks and tests on every push/pull request.
- **Building**: Builds Docker images and push it to a container registry.
- **Running**: Runs Docker containers and ensure the api working successfully.

---

## 🧪 Testing

The application is **fully tested** with:
- **Unit Tests**: Covering controller, service and repository layers using JUnit.
- **Integration Tests**: Validating API endpoints and database interactions.
- **Test Coverage**: Achieves high coverage to ensure reliability.

To run tests:
```bash
./mvnw test
```

---

## 📈 Future Enhancements

- Implement pagination for ticket listing endpoints (noted in TODO for `/api/v1/tickets`).
- Add support for payment gateway integration (e.g., Stripe or PayPal).
- Develop a frontend interface using React or Angular for a better user experience.
- Enhance email notifications with customizable templates.

---

## 📬 Contact

For questions or contributions, feel free to reach out via [GitHub Issues](https://github.com/YousofDev/busticketing/issues) or connect with me on [Email](yousofdevpro@gmail.com).

---

*Built with ❤️ by [YousofDev](https://github.com/YousofDev)*
