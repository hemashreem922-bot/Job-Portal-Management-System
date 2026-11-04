# 💼 Job Portal Management System

### A Full-Stack Web Application for Smarter Recruitment

A centralized recruitment platform connecting candidates, employers, and administrators through a unified job management system.

---

## 📌 About the Project

The **Job Portal Management System** is a full-stack web application developed using Java and Spring Boot to simplify and organize the recruitment process.

It provides three role-based portals, allowing candidates to discover job opportunities, employers to manage recruitment activities, and administrators to oversee platform operations.

The system focuses on simplifying job applications, improving recruitment management, and providing an organized experience for all users.


## ✨ Core Features

### 01. Candidate Portal
- Search and explore available job opportunities.
- Filter vacancies by category, location, and employment type.
- View job descriptions and eligibility requirements.
- Submit job applications.
- Track application progress and status.
- Bookmark interesting opportunities.

### 02. Employer Portal
- Create and manage company profiles.
- Publish and maintain job vacancies.
- Access applications submitted by candidates.
- Review candidate profiles and resumes.
- Manage application statuses.
- Provide feedback to applicants.

### 03. Admin Portal
- Monitor platform statistics and activities.
- Manage candidate and employer accounts.
- Activate or deactivate user accounts.
- Access the H2 database console.

## 🧰 Technology Stack

| Category | Technologies |
|---|---|
| Language | Java |
| Backend | Spring Boot |
| Frontend | HTML, CSS, JavaScript |
| UI Framework | Bootstrap 5 |
| Database | H2, MySQL |
| ORM | Hibernate |
| Database Access | Spring Data JPA |
| Icons | Font Awesome |
| Build Tool | Maven |

## 🏛️ System Architecture

The application follows a layered architecture that separates responsibilities into different components.

| Layer | Responsibility |
|---|---|
| Controller | Handles HTTP requests and API endpoints |
| Service | Implements business logic |
| Repository | Performs database operations |
| Model | Defines database entities |
| DTO | Transfers request and response data |
| Frontend | Handles user interaction |

**Application Flow:**

`User Interface → Controller → Service → Repository → Database`

## 🗄️ Database Management

The application supports two database configurations:

**H2 Database**
- Default database.
- File-based persistence.
- Useful for development and testing.

**MySQL Database**
- Alternative relational database.
- Configured through the MySQL application profile.
- Connection details managed through project properties.

## 🔗 REST API Capabilities

The backend provides endpoints for:

- User registration and authentication.
- Job creation and management.
- Job searching and filtering.
- Bookmark management.
- Job application submission.
- Application status tracking.
- Administrative user management and statistics.

## 📁 Project Structure

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
├── docs/
│   └── images/
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

## 🚀 Getting Started

### Prerequisites

- Java JDK 21 or the version specified in the project.
- Maven Wrapper.
- Windows, Linux, or macOS.

### Installation and Execution

**Step 1: Clone the repository**

```bash
git clone https://github.com/hemashreem922-bot/Job-Portal-Management-System.git
```

**Step 2: Navigate to the project**

```bash
cd Job-Portal-Management-System
```

**Step 3: Run the application**

For Windows PowerShell:

```powershell
.\run.ps1
```

Alternatively:

```powershell
.\mvnw.cmd spring-boot:run
```

**Step 4: Access the application**

Open your browser and visit:

`http://localhost:8080`

The application uses H2 by default.

## 🔮 Future Enhancements

- Automated email notifications.
- Intelligent job recommendation system.
- Resume parsing and skill matching.
- Advanced recruitment analytics.
- Improved reporting and dashboard insights.

B.Tech | Software Development | AI & Machine Learning

GitHub: [@hemashreem922-bot](https://github.com/hemashreem922-bot)

---

*Project developed to explore full-stack application development, Java programming, Spring Boot, REST APIs, and relational database management.*
