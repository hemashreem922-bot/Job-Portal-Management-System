# Job Portal Management System

A full-stack web application built using Java and Spring Boot to simplify job searching, recruitment, and application management.

## Overview

The Job Portal Management System provides three dedicated portals for candidates, employers, and administrators. It enables candidates to explore job opportunities and track applications, while employers can publish vacancies and manage applicants. Administrators can monitor users and platform activity.

## Key Features

### Candidate Portal
- Browse and search available job opportunities.
- Filter jobs by category, location, and employment type.
- View detailed job descriptions and requirements.
- Apply for jobs and track application status.
- Bookmark jobs for future reference.

### Employer Portal
- Manage company profiles.
- Create, update, and manage job listings.
- View applications received from candidates.
- Review candidate profiles and resumes.
- Update application statuses and provide feedback.

### Admin Portal
- View platform statistics and activity.
- Manage candidate and employer accounts.
- Activate or deactivate user accounts.
- Access the H2 database console.

## Technology Stack

| Component | Technologies |
|---|---|
| Backend | Java, Spring Boot |
| Database | H2, MySQL |
| ORM | Spring Data JPA, Hibernate |
| Frontend | HTML, CSS, JavaScript |
| UI Framework | Bootstrap 5 |
| Icons | Font Awesome |
| Build Tool | Maven |

## Application Architecture

The application follows a layered architecture:

- **Controller:** Handles HTTP requests and API endpoints.
- **Service:** Contains application business logic.
- **Repository:** Manages database operations.
- **Model:** Represents database entities.
- **DTO:** Handles request and response data.
- **Frontend:** Provides the user interface.

## Getting Started

### Prerequisites

- Java JDK 21 or the version specified in the project configuration.
- Windows, Linux, or macOS.
- Maven Wrapper (included in the repository).

### Installation

**1. Clone the repository**

```bash
git clone https://github.com/hemashreem922-bot/Job-Portal-Management-System.git
```

**2. Navigate to the project directory**

```bash
cd Job-Portal-Management-System
```

**3. Run the application on Windows**

Using PowerShell:

```powershell
.\run.ps1
```

Alternatively, using Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

**4. Open the application**

Visit:

http://localhost:8080

The application uses the H2 database by default.

## Database Configuration

The project supports two database options:

**H2 Database**
- Default database configuration.
- File-based persistence.
- Suitable for local development and testing.

**MySQL Database**
- Alternative relational database configuration.
- Uses the MySQL application profile.
- Database settings can be configured in the project properties.

## REST API

The application provides REST API endpoints for:

- User registration and login.
- Job creation, searching, updating, and deletion.
- Saving and retrieving bookmarked jobs.
- Submitting and tracking applications.
- Managing application statuses.
- Administrator statistics and user management.

## Project Structure

```text
Job-Portal-Management-System/
│
├── src/
│   └── main/
│       ├── java/com/jobportal/
│       │   ├── controller/
│       │   ├── dto/
│       │   ├── model/
│       │   ├── repository/
│       │   ├── service/
│       │   └── util/
│       │
│       └── resources/
│           ├── static/
│           ├── application.properties
│           ├── application-mysql.properties
│           ├── schema.sql
│           └── data.sql
│
├── data/
├── .mvn/
├── pom.xml
├── mvnw
├── mvnw.cmd
├── run.bat
├── run.ps1
└── README.md
```

## Future Enhancements

- Email notifications for application updates.
- Advanced job recommendation system.
- Resume parsing and skill matching.
- Enhanced analytics and reporting.

## Author

**Hema Shree Muppala**

GitHub: [@hemashreem922-bot](https://github.com/hemashreem922-bot)

---

*Developed as a software development project to explore Java, Spring Boot, database management, and full-stack application development.*
